package com.gfd.eshop.network.core;


import android.os.Handler;
import android.os.Looper;

import com.gfd.eshop.R;
import com.gfd.eshop.base.utils.LogUtils;
import com.gfd.eshop.base.wrapper.ToastWrapper;
import com.gfd.eshop.network.EShopClient;
import com.gfd.eshop.network.UserManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class UiCallback implements Callback {


    //创建消息事件handler对象
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    //创建返回数据类型对象
    private Class<? extends ResponseEntity> mResponseType;

    //私有化构造方法
    public UiCallback() {
    }

    /**
     * <p>当网络请求由于以下原因失败时被调用: 请求被取消, 网络连接问题或者请求超时.
     * <p>由于网络可能在数据交换的中途发生故障, 有可能在失败前, 服务器处理了请求.
     */
    //获取数据失败
    @Override public final void onFailure(Call call, final IOException e) {
        HANDLER.post(new Runnable() {
            @Override public void run() {
                onFailureInUi(e);
            }
        });
    }


    /**
     * 当HTTP响应从服务器成功返回时被调用. 可以通过{@link Response#body}读取响应体.
     * 在响应体变为{@linkplain ResponseBody closed}状态之前, 响应会一直处于可用状态.
     * <p/>
     * 注意: 此回调在后台线程执行, 响应的接收者可以将数据发送到另一个线程来处理.
     * <p/>
     * 注意: 传输层的成功(接受到了HTTP响应码, 响应头和响应体)不代表应用层的成功:
     * {@code response}依然可能是一个失败的响应码, 例如404或500.
     */
    //获取数据成功
    @Override public final void onResponse(Call call, Response response) throws IOException {
        final ResponseEntity responseEntity = EShopClient.getInstance()
                .getResponseEntity(response, mResponseType);

        HANDLER.post(new Runnable() {
            @Override public void run() {
                onResponseInUi(responseEntity);
            }
        });
    }

    //获取数据失败后UI线程操作
    public final void onFailureInUi(IOException e) {
        LogUtils.error("onFailureInUi", e);
        ToastWrapper.show(R.string.error_network);
        onBusinessResponse(false, null);
    }

    //获取数据成功后UI线程操作
    public final void onResponseInUi(ResponseEntity responseEntity) {

        if (responseEntity == null || responseEntity.getStatus() == null) {
            throw new RuntimeException("Fatal Api Error!");
        }

        if (responseEntity.getStatus().isSucceed()) {
            onBusinessResponse(true, responseEntity);
        } else {
            ToastWrapper.show(responseEntity.getStatus().getErrorDesc());
            onBusinessResponse(false, null);

            if (responseEntity.getStatus().getErrorCode() == ApiError.SESSION_EXPIRE) {
                UserManager.getInstance().clear();
            }
        }
    }

    //设置返回数据类型
    public void setResponseType(Class<? extends ResponseEntity> responseType) {
        mResponseType = responseType;
    }

    //获取数据后操作    （抽象方法，创建对象时必须实现这个抽象方法，具体就交给创建对象时实现）
    public abstract void onBusinessResponse(boolean success, ResponseEntity responseEntity);
}
