package com.gfd.eshop.network;

import com.gfd.eshop.network.core.ApiInterface;
import com.gfd.eshop.network.core.ResponseEntity;
import com.gfd.eshop.network.core.UiCallback;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * <p>网络接口的操作类, 网络请求使用{@link OkHttpClient}实现.
 */
public class EShopClient {

    //接口连接路径
    public static final String BASE_URL = "http://106.14.32.204/eshop/emobile/?url=";


    private static EShopClient sInstance;

    //单例模式
    public static EShopClient getInstance() {
        if (sInstance == null) {
            sInstance = new EShopClient();
        }
        return sInstance;
    }

    //OkHttpClient  创建OkHttpClient对象，单例
    private final OkHttpClient mOkHttpClient;
    //Gson 创建Gson对象，单例
    private final Gson mGson;

    //是否显示打印
    private boolean mShowLog = false;

    //构造函数初始化
    private EShopClient() {
        mGson = new Gson();

        //添加拦截器
        HttpLoggingInterceptor mLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if (mShowLog) System.out.println(message); // NOPMD
            }
        });

        mLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //创建mOkHttpClient 对象
        mOkHttpClient = new OkHttpClient.Builder().addInterceptor(mLoggingInterceptor).build();

    }

    /**
     * 同步执行Api请求.
     *
     * @param apiInterface 服务器Api接口.
     * @param <T>          响应体的实体类型.
     * @return 响应数据实体.
     * @throws IOException 请求被取消, 连接超时, 失败的响应码等等.
     */
    //同步执行
    public <T extends ResponseEntity> T execute(ApiInterface apiInterface) throws IOException {

        Response response = newApiCall(apiInterface, null).execute();
        //noinspection unchecked
        Class<T> entityClass = (Class<T>) apiInterface.getResponseType();
        return getResponseEntity(response, entityClass);
    }

    /**
     * 异步执行Api请求.
     *
     * @param apiInterface 服务器Api接口.
     * @param uiCallback   回调
     * @return {@link Call}对象.
     */
    //异步执行
    public Call enqueue(ApiInterface apiInterface, UiCallback uiCallback, String tag) {
        Call call = newApiCall(apiInterface, tag);
        //noinspection unchecked
        uiCallback.setResponseType(apiInterface.getResponseType());//uicallback设置返回类型
        call.enqueue(uiCallback);
        return call;
    }

    //取消请求
    public void cancelByTag(String tag) {
        // A call may transition from queue -> running. Remove queued Calls first.
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(tag)) call.cancel();
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (call.request().tag().equals(tag)) call.cancel();
        }
    }

    //是否显示打印
    public void setShowLog(boolean showLog) {
        mShowLog = showLog;
    }

    //通过gson  反序列化得到最终的数据类型
    public <T extends ResponseEntity> T getResponseEntity(Response response, Class<T> clazz) throws IOException {
        if (!response.isSuccessful()) {//response 必须成功
            throw new IOException("Response code is " + response.code());
        }
        return mGson.fromJson(response.body().charStream(), clazz);
    }

    //通过打标签，接口创创建Call 对象
    private Call newApiCall(ApiInterface apiInterface, String tag) {
        //创建请求体
        Request.Builder builder = new Request.Builder();
        //请求体包含请求路径
        builder.url(BASE_URL + apiInterface.getPath());

        if (apiInterface.getRequestParam() != null) {
            //获取请求参数
            String param = mGson.toJson(apiInterface.getRequestParam());
            FormBody formBody = new FormBody.Builder().add("json", param).build();
            //发送请求参数
            builder.post(formBody);
        }
        //请求体打标签
        builder.tag(tag);
        //获取最终请求对象
        Request request = builder.build();
        //获取call对象
        return mOkHttpClient.newCall(request);
    }

}
