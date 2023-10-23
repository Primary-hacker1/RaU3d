package com.rick.recoveryapp.bluetooth;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.rick.recoveryapp.R;
import com.rick.recoveryapp.base.BaseApplication;
import com.rick.recoveryapp.entity.EcgData;
import com.rick.recoveryapp.entity.protocol.PoolMessage;
import com.rick.recoveryapp.entity.protocol.UploadData;
import com.rick.recoveryapp.utils.CRC16Util;
import com.rick.recoveryapp.utils.LocalConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BtDataPro {

    // 握手指令-发送
    public final String CONNECT_SEND = "A8810101B03CED";
    // 握手指令-发送
    //   public final String CONNECT_SEND_1 = "A8810101B03CED";
    // 握手指令-暂停
    public final String CONNECT_CLOSE = "A8810102F03DED";
    // 设备MAC地址绑定
    //public final String MAC_CODE = "A88512  E76B581B5164 A4C138402A4D 00A0503D0264  07F8ED";
    //血氧 00:A0:50:3B:CB:AC   血压 A4:C1:38:44:16:0C   心电 D2:08:AA:BB:37:AE
    public final String MAC_CODE = "A88512  D2 08 AA BB 37 AE   A4 C1 38 44 16 0C   00 A0 50 3B CB AC   0DA6ED";
    // 开始测量血压
    public final String CONTORL_CODE_BEGIN = "A884080000000000000051CE87ED";
    // 血压测量结束
    public final String CONTORL_CODE_END = "A8840800000000000000528E86ED";
    //握手指令应答
    public final String CONN_CODE_ANSWER = "A8 81 0C";
    //设备信息上传应答
    public final String UPLODE_ANSWER = "A8 82 0D";
    //心电数据包应答
    public final String ECGDATA_ANSWER = "A8 83";
    //Mac地址绑定应答
    public final String MAC_ANSWER = "A8 85 01";
    //设备控制应答（血压仪启动/停止）
    public final String CONTORL_ANSWER = "A8 84 01";
    String CMD_CODE;

    public String MassageStr = "";

    Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    int count = 0;

    public String GetCmdCode(String ecgMac, String bloodMac, String oygrenMac) {
        String cmd_head = "A88512",              //包头
                cmd_end = "ED";                   //结尾
        String splicingStr = cmd_head + ecgMac + bloodMac + oygrenMac;
        String CRC16 = CRC16Util.getCRC16(splicingStr);
        CMD_CODE = splicingStr + CRC16 + cmd_end;
        com.efs.sdk.base.core.util.Log.d("GetCmdCode", "ActiveFragemt,获取指令");
        return CMD_CODE;
    }

    //重写发送函数，参数不同。
    public void sendBTMessage(String message) {
        // 确保已连接
//        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
//            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
//                    .show();
//            return;
//        }
        // 检测是否有字符串发送
        if (message.length() > 0) {
            // 获取 字符串并告诉BluetoothChatService发送
            byte[] send = new byte[0];
            try {
                send = hexStr2Bytes(message);
            } catch (Exception e) {
                Log.d("hexStr2Bytes", e.getMessage());
            }
            BaseApplication.mConnectService.write(send);//回调service
            // 清空输出缓冲区
            //     mOutStringBuffer.setLength(0);
        } else {
//            Toast.makeText(this,"发送内容不能为空",
//                    Toast.LENGTH_SHORT).show();
        }
    }

    // 将16进制字符串转化为字节数组
    public static byte[] hexStr2Bytes(String paramString) throws Exception {

        int i = paramString.length() / 2;
        byte[] arrayOfByte = new byte[i];
        int j = 0;
        while (true) {
            if (j >= i)
                return arrayOfByte;
            int k = 1 + j * 2;
            int l = k + 1;
            arrayOfByte[j] = (byte) (0xFF & Integer.decode(
                    "0x" + paramString.substring(j * 2, k)
                            + paramString.substring(k, l)).intValue());
            ++j;
        }
    }

    public void Processing(ArrayList<String> RXlist, String type) {
        // String type = RXlist.get(1);

        PoolMessage poolMessage;
        switch (type) {
            //处理握手指令应答
            case "81":
                Connection(RXlist);
                break;

            //处理其他一般数据
            case "82":
                UploadData uploadData = Results_Data(RXlist);
                poolMessage = new PoolMessage();
                if (uploadData != null) {
                    poolMessage.setObjectJson(gson.toJson(uploadData));
                    poolMessage.setObjectName(UPLODE_ANSWER);
                    poolMessage.setState(true);
                    //   LocalConfig.poolMessage = poolMessage;
                } else {
                    poolMessage.setObjectJson(null);
                    poolMessage.setObjectName("");
                    poolMessage.setState(false);
                }
                LocalConfig.poolMessage = poolMessage;
                LiveEventBus.get("BT_PROTOCOL")
                        .post(poolMessage);
                break;

            //处理心电数据
            case "83":
                //EcgDataPro(hexstr);
                EcgData ecgData = EcgDataPro(RXlist);
                poolMessage = new PoolMessage();
                if (ecgData != null) {
                    poolMessage.setObjectJson(gson.toJson(ecgData));
                    poolMessage.setObjectName(ECGDATA_ANSWER);
                    poolMessage.setState(true);
                    LiveEventBus.get("BT_PROTOCOL")
                            .post(poolMessage);
                } else {
                    poolMessage.setObjectJson(null);
                    poolMessage.setObjectName("");
                    poolMessage.setState(false);
                }
                LocalConfig.poolMessage1 = poolMessage;
                break;

            //处理Mac地址绑定应答
            case "85":
                break;

            //处理设备控制应答
            case "84":
                poolMessage = new PoolMessage();
                poolMessage.setObjectJson(eqmentControl(RXlist));
                poolMessage.setObjectName(CONTORL_ANSWER);
                poolMessage.setState(true);
                //  LocalConfig.poolMessage = poolMessage;
                LiveEventBus.get("BT_PROTOCOL")
                        .post(poolMessage);
                break;

            default:
        }
    }

    public PoolMessage U3DProcessing(ArrayList<String> RXlist) {
        String type = RXlist.get(1);
        PoolMessage poolMessage = new PoolMessage();
        if (type.equals("81")) {
            Connection(RXlist);
        } else if (type.equals("82")) {
            UploadData uploadData = Results_Data(RXlist);

            if (uploadData != null) {
                poolMessage.setObjectJson(gson.toJson(uploadData));
                poolMessage.setObjectName(UPLODE_ANSWER);
                poolMessage.setState(true);
            } else {
                poolMessage.setObjectJson(null);
                poolMessage.setObjectName("");
                poolMessage.setState(false);
            }
        } else if (type.equals("83")) {
            EcgData ecgData = EcgDataPro(RXlist);
            poolMessage = new PoolMessage();
            if (ecgData != null) {
                poolMessage.setObjectJson(gson.toJson(ecgData));
                poolMessage.setObjectName(ECGDATA_ANSWER);
                poolMessage.setState(true);
            } else {
                poolMessage.setObjectJson(null);
                poolMessage.setObjectName("");
                poolMessage.setState(false);
            }
        }
        return poolMessage;
    }

    /**
     * 1.握手指令
     */
    public void Connection(ArrayList<String> hexlist) {
//        String[] split = hexstr.toString().split(" ");
        StringBuffer strbuf = new StringBuffer();
        for (int i = 3; i < 15; i++) {
            strbuf.append(hexlist.get(i));
        }
        String DeviceId = strbuf.toString();
        MassageStr = '\n' + "接收到数据：" + strbuf + '\n' + "设备ID号： " + DeviceId + '\n';
    }

    /**
     * 2．设备信息上传
     */
    public UploadData Results_Data(ArrayList<String> hexlist) {

        if (hexlist.size() < 22) {
            return null;
        }
        UploadData uploadData = new UploadData();

        String oxy_vaulestr = null;
        String ActiveType = hexlist.get(4);
        String ActiveState = hexlist.get(5);
        String SpasmState = hexlist.get(6);
        String time = hexlist.get(7) + hexlist.get(8);
        String speed = hexlist.get(9);

        String left = hexlist.get(10);
        String right = hexlist.get(11);

        //阻力 痉挛 速度 设定时间
        String stresistance = hexlist.get(12);
        String stspasm = hexlist.get(13);
        String stspeed = hexlist.get(14);
        String sttime = hexlist.get(15);

        String ECG = hexlist.get(16);
        String blood = hexlist.get(17);
        String blood_oxy = hexlist.get(18);
        String high = hexlist.get(19);
        String low = hexlist.get(20);
        String oxy_vaule = hexlist.get(21);

        //时间
        int times = Integer.parseInt(time, 16);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(times);
        Date date = new Date(lt);

        uploadData.setSTresistance(String.valueOf(covert16to10(stresistance)));
        uploadData.setSTspasm(String.valueOf(covert16to10(stspasm)));
        uploadData.setSTspeed(String.valueOf(covert16to10(stspeed)));
        uploadData.setSTtime(String.valueOf(covert16to10(sttime)));

        uploadData.setTime(simpleDateFormat.format(date));
        //运动速度
        uploadData.setSpeed(String.valueOf(covert16to10(speed)));
        //左对称性
        uploadData.setLeft(String.valueOf(covert16to10(left)));
//        Log.d("Data", leftstr);
        //右对称性
        uploadData.setRight(String.valueOf(covert16to10(right)));
        //血压高压
        uploadData.setHigh(String.valueOf(covert16to10(high)));
        //血压低压
        uploadData.setLow(String.valueOf(covert16to10(low)));

        if (oxy_vaule.equals("AA")) {
            uploadData.setOxy_vaulestr("手指未插入");
        } else if (oxy_vaule.equals("FF")) {
            uploadData.setOxy_vaulestr("探头脱落");
        } else {
            uploadData.setOxy_vaulestr(String.valueOf(covert16to10(oxy_vaule)));
        }

        return DataStr(uploadData, ActiveType, ActiveState, SpasmState, ECG, blood, blood_oxy);
//        MassageStr = res + "  " + speedstr + "  " + leftstr + "  " + rightstr + "  "
//                + highstr + "  " + lowstr + "  " + oxy_vaulestr + "  " + information + '\n';
    }

    private UploadData DataStr(UploadData uploadData, String ActiveType, String ActiveState, String SpasmState, String ECG, String blood, String blood_oxy) {
        String activeName, stateName, ecgstr, bloodstr, blood_oxystr, DataString;
        int SpasmName = -1;
        //运动模式
        switch (ActiveType) {
            case "00":
                activeName = "智能模式/非运动";
                break;

            case "01":
                activeName = "主动模式";
                break;

            case "02":
                activeName = "被动模式";
                break;

            case "0A":
                activeName = "智能模式/被动";
                break;

            case "0B":
                activeName = "智能模式/主动";
                break;

            default:
                activeName = "未知";
        }
        uploadData.setActiveType(activeName);

        //运行状态
        switch (ActiveState) {
            case "10":
                stateName = "停机状态";
                break;
            case "11":
                stateName = "运行状态";
                break;
            default:
                stateName = "未知";
        }
        uploadData.setActiveState(stateName);

        //痉挛状态
        switch (SpasmState) {
            case "20":
                SpasmName = 0;
                break;
            case "21":
                SpasmName = 1;
                break;
            case "22":
                SpasmName = 2;
                break;
            case "23":
                SpasmName = 3;
                break;
            case "24":
                SpasmName = 4;
                break;
            case "25":
                SpasmName = 5;
                break;
            default:
                SpasmName = -1;
        }
        uploadData.setSpasmState(SpasmName);

        //心电状态
        switch (ECG) {
            case "30":
                ecgstr = "心电仪未连接";
                break;

            case "31":
                ecgstr = "已连接";
                break;
            default:
                ecgstr = "未知";
        }
        uploadData.setECG(ecgstr);

        //血压状态
        switch (blood) {
            case "40":
                bloodstr = "血压仪未连接";
                break;

            case "41":
                bloodstr = "已连接";
                break;
            default:
                bloodstr = "未知";
        }
        uploadData.setBlood(bloodstr);

        //血氧状态
        switch (blood_oxy) {
            case "50":
                blood_oxystr = "血氧仪未连接";
                break;

            case "51":
                blood_oxystr = "已连接";
                break;
            default:
                blood_oxystr = "未知";
        }

        uploadData.setBlood_oxy(blood_oxystr);
        // DataString = activeName + "  " + stateName + "  " + SpasmName + "  " + bloodstr + "  " + ecgstr + "  " + blood_oxystr;
        return uploadData;
    }

    /**
     * 设备控制 eqmentcontrol
     */
    public String eqmentControl(ArrayList<String> hexstr) {
        if (hexstr.size() < 4) {
            return "52";
        } else {
            String controlState = hexstr.get(3);
            return controlState;
        }

    }

    public static String decToHex(int n) {
        String r = "";//空字符串
        while (n >= 16) {
            int yushu = n % 16;
            int shang = n / 16;
            if (yushu > 9) {//特殊处理
                char c = (char) ((yushu - 10) + 'A');
                r += c;//连接字符c
            } else {
                r += yushu;
            }
            n = shang;
        }
        if (n > 9) {
            char c = (char) ((n - 10) + 'A');
            r += c;
        } else {
            r += n;
        }
        return reverse(r);
    }

    //反转字符串（反转后就是正确顺序的十六进制数：从下到上的顺序）
    private static String reverse(String r) {
        String s = "";
        for (int i = r.length() - 1; i >= 0; i--) {
            s += r.charAt(i);
        }
        return s;
    }

    /**
     * 心电数据处理
     */
    public EcgData EcgDataPro(ArrayList<String> hexstr) {
        EcgData ecgData = new EcgData();
        ArrayList<Float> ecgCooy = new ArrayList<>();
        try {
            if (hexstr.size() > 6) {
                String heartrate = hexstr.get(0);
                int xinlv = covert16to10(heartrate);
                if (xinlv == 0) {
                    ecgData.setHeartrate("--");
                } else {
                    ecgData.setHeartrate(xinlv + "");
                }

                //心率的取值是4,5两位，心电的取值从第6位开始，所以i=6;
                String strrr = hexstr.get(2);
                Log.d("ecgdata", strrr);
                int i = 2;
                while (i < hexstr.size() - 4) {
                    String str = reverseHex(hexstr.get(i) + hexstr.get(i + 1));
                    int hex = Integer.valueOf(str, 16).shortValue();
                    Double ecg = 1.0035 * (-hex * 1800) / (4096 * 178.74);
                    Float cooy = 0f;
                    if (ecg != -80.8434542284271) {
                        cooy = ecg.floatValue();

                    }
                    ecgCooy.add(cooy);
                    i = i + 2;
                }
                LocalConfig.CoorYList = ecgCooy;
                ecgData.setEcgCoorY(ecgCooy);
            } else {

                ecgCooy.add(0f);
                LocalConfig.CoorYList = ecgCooy;
                ecgData.setHeartrate("--");
                ecgData.setEcgCoorY(ecgCooy);

            }
        } catch (Exception e) {
            ecgData = null;
            Log.d("ECG", e.getMessage());
        }
        return ecgData;
    }

    /**
     * 十六进制字符串高低位转换
     *
     * @param
     * @return
     */
    public String reverseHex(String hex) {
        char[] charArray = hex.toCharArray();
        int length = charArray.length;
        int times = length / 2;
        for (int c1i = 0; c1i < times; c1i += 2) {
            int c2i = c1i + 1;
            char c1 = charArray[c1i];
            char c2 = charArray[c2i];
            int c3i = length - c1i - 2;
            int c4i = length - c1i - 1;
            charArray[c1i] = charArray[c3i];
            charArray[c2i] = charArray[c4i];
            charArray[c3i] = c1;
            charArray[c4i] = c2;
        }
        return new String(charArray);
    }

    /**
     * 16进制转10进制
     *
     * @param content String
     * @return int
     */
    public int covert16to10(String content) {
        int number = 0;
        String[] HighLetter = {"A", "B", "C", "D", "E", "F"};
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i <= 9; i++) {
            map.put(i + "", i);
        }
        for (int j = 10; j < HighLetter.length + 10; j++) {
            map.put(HighLetter[j - 10], j);
        }
        String[] str = new String[content.length()];
        for (int i = 0; i < str.length; i++) {
            str[i] = content.substring(i, i + 1);
        }
        for (int i = 0; i < str.length; i++) {
            number += map.get(str[i]) * Math.pow(16, str.length - 1 - i);
        }
        return number;
    }

}
