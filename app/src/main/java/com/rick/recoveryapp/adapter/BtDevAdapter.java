package com.rick.recoveryapp.adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rick.recoveryapp.R;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BtDevAdapter extends RecyclerView.Adapter<BtDevAdapter.VH> {
    private static final String TAG = BtDevAdapter.class.getSimpleName();
    private final List<BluetoothDevice> mDevices = new ArrayList<>();
    private final Listener mListener;

    public BtDevAdapter(Listener listener) {
        mListener = listener;
        addBound();
    }

    private void addBound() {
        Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (bondedDevices != null)
            mDevices.addAll(bondedDevices);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dev_item, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        BluetoothDevice dev = mDevices.get(position);
        String name = dev.getName();
        String address = dev.getAddress();

        holder.dev_txt_devname.setCenterString(name);
        holder.dev_txt_devname.setCenterBottomString(address);
        //   int bondState = dev.getBondState();
        //  holder.dev_txt_devaddress.setCenterString(address);
        //  holder.dev_txt_devaddress.setCenterString(String.format("%s (%s)", address, bondState == 10 ? "未配对" : "配对"));

    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void add(BluetoothDevice dev) {
        if (mDevices.contains(dev))
            return;
        mDevices.add(dev);
        notifyDataSetChanged();
    }

    public void reScan() {
        mDevices.clear();
        addBound();
        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        if (!bt.isDiscovering())
            bt.startDiscovery();
        notifyDataSetChanged();
    }

    class VH extends RecyclerView.ViewHolder {
        SuperTextView dev_txt_devname;
        //  SuperTextView dev_txt_devaddress;

        VH(final View itemView) {
            super(itemView);

            dev_txt_devname = itemView.findViewById(R.id.dev_txt_devname);
            //   itemView.setOnClickListener(this);
            dev_txt_devname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d(TAG, "onClick, getAdapterPosition=" + pos);
                    if (pos >= 0 && pos < mDevices.size())
                        mListener.onItemClick(mDevices.get(pos));

                }
            });
            //dev_txt_devaddress = itemView.findViewById(R.id.dev_txt_devaddress);
        }

//        @Override
//        public void onClick(View v) {
//            int pos = getAdapterPosition();
//            Log.d(TAG, "onClick, getAdapterPosition=" + pos);
//            if (pos >= 0 && pos < mDevices.size())
//                mListener.onItemClick(mDevices.get(pos));
//        }
    }

    public interface Listener {
        void onItemClick(BluetoothDevice dev);
    }
}
