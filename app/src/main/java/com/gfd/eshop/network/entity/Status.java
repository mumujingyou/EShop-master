package com.gfd.eshop.network.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 响应状态对象.
 */
public class Status {

    //成功
    @SerializedName("succeed") private int mSucceed;

    //错误
    @SerializedName("error_code") private int mErrorCode;

    //错误描述
    @SerializedName("error_desc") private String mErrorDesc;

    public boolean isSucceed() {
        return mSucceed == 1;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorDesc() {
        return mErrorDesc;
    }
}
