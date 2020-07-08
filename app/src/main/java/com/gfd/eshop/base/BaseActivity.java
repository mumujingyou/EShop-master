package com.gfd.eshop.base;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.gfd.eshop.network.EShopClient;
import com.gfd.eshop.network.core.ApiInterface;
import com.gfd.eshop.network.core.ResponseEntity;
import com.gfd.eshop.network.core.UiCallback;
import com.gfd.eshop.network.event.UserEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 通用Activity基类.
 */
public abstract class BaseActivity extends TransitionActivity {

    //绑定id对象
    private Unbinder mUnbinder;

    @Override protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayout());//设置布局
        mUnbinder = ButterKnife.bind(this);//添加绑定
        initView();//初始化视图
        EventBus.getDefault().register(this);//新增事件注册
    }

    @Override protected final void onDestroy() {
        super.onDestroy();
        EShopClient.getInstance().cancelByTag(getClass().getSimpleName());
        EventBus.getDefault().unregister(this);//解绑事件
        mUnbinder.unbind();//解除绑定UI
        mUnbinder = null;//销毁绑定
    }

    //异步执行获取数据
    protected Call enqueue(final ApiInterface apiInterface) {
        UiCallback uiCallback = new UiCallback() {
            @Override
            public void onBusinessResponse(boolean success, ResponseEntity responseEntity) {
                BaseActivity.this.onBusinessResponse(
                        apiInterface.getPath(),
                        success,
                        responseEntity);
            }
        };

        return EShopClient.getInstance()
                .enqueue(apiInterface, uiCallback, getClass().getSimpleName());
    }

    //事件发布
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(UserEvent event) {
    }

    //获取布局文件资源id
    @LayoutRes protected abstract int getContentViewLayout();

    //初始化（抽象方法，子类必须实现）
    protected abstract void initView();

    //最终获取到数据后处理（抽象方法，子类必须实现）
    protected abstract void onBusinessResponse(String apiPath, boolean success, ResponseEntity rsp);
}
