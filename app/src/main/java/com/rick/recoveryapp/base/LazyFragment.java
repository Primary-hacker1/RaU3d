package com.rick.recoveryapp.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract  class LazyFragment extends Fragment {

    /**
     * rootView是否初始化标志，防止回调函数在rootView为空的时候触发
     */
    private boolean isViewCreated=false;//根布局是否创建

    /**
     * 当前Fragment是否处于可见状态标志，防止因ViewPager的缓存机制而导致回调函数的触发
     */
    private boolean currentVisibleState=false;//当前View是否可见

    /**
     * onCreateView()里返回的view，修饰为protected,所以子类继承该类时，在onCreateView里必须对该变量进行初始化
     */
    protected View rootView;//fragment 根布局

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      if(rootView==null){
          rootView=inflater.inflate(getLayoutRes(),container,false);
      }
        initView(rootView);
        isViewCreated=true;

        if(getUserVisibleHint()){
            dispatchUserVisibleHint(true);
        }
        return rootView;
    }

    //初始化
    protected abstract void initView(View rootView);

    //Fragment布局文件
    protected abstract int getLayoutRes();

   //判断Fragment是否可见
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isViewCreated){ //只有当fragment创建后才能判断是否可见

            if(isVisibleToUser &&!currentVisibleState){
                dispatchUserVisibleHint(true);
            }else if(!isVisibleToUser && currentVisibleState){
                dispatchUserVisibleHint(false);
            }
        }
    }

    //Fragment可见时进行的逻辑操作
    private void dispatchUserVisibleHint(boolean isVisible){
        if( currentVisibleState == isVisible){
            return;
        }
        currentVisibleState=isVisible;

            if(isVisible){
                //当fragment可见时，进行数据加载操作
                onFragmentLoad();

            }else {
                //当fragment不可见时，停止数据加载操作
                onFragmentLoadStop();
            }
    }

    //由继承的子类实现这两个方法，进行不同的Fragment处理
    protected void onFragmentLoad(){}

    protected void onFragmentLoadStop(){}

    @Override
    public void onResume() {
        super.onResume();
        if(!currentVisibleState && getUserVisibleHint()){
            dispatchUserVisibleHint(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(currentVisibleState && getUserVisibleHint()){
            dispatchUserVisibleHint(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated=false;
        currentVisibleState=false;

    }
}
