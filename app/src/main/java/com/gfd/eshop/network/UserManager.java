package com.gfd.eshop.network;


import android.support.annotation.NonNull;

import com.gfd.eshop.network.api.ApiAddressList;
import com.gfd.eshop.network.api.ApiCartList;
import com.gfd.eshop.network.api.ApiUserInfo;
import com.gfd.eshop.network.core.IUserManager;
import com.gfd.eshop.network.core.ResponseEntity;
import com.gfd.eshop.network.core.UiCallback;
import com.gfd.eshop.network.entity.Address;
import com.gfd.eshop.network.entity.CartBill;
import com.gfd.eshop.network.entity.CartGoods;
import com.gfd.eshop.network.entity.Session;
import com.gfd.eshop.network.entity.User;
import com.gfd.eshop.network.event.AddressEvent;
import com.gfd.eshop.network.event.CartEvent;
import com.gfd.eshop.network.event.UserEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
//用户管理类
public class UserManager implements IUserManager {

    //用户单列对象
    private static IUserManager sInstance = new UserManager();

    public static IUserManager getInstance() {
        return sInstance;
    }

    //网络接口操作类单例对象
    private EShopClient mClient = EShopClient.getInstance();

    //事件分发单例对象
    private EventBus mBus = EventBus.getDefault();

    //Session对象  包括sessionId和userId
    private Session mSession;

    //User 对象  包含user信息
    private User mUser;

    //购物车列表
    private List<CartGoods> mCartGoodsList;

    //购物车 总价
    private CartBill mCartBill;

    //地址列表
    private List<Address> mAddressList;

    //设置用户
    @Override public void setUser(@NonNull User user, @NonNull Session session) {
        mUser = user;
        mSession = session;

        mBus.postSticky(new UserEvent());
        retrieveCartList();
        retrieveAddressList();
    }

    //获取用户信息
    @Override public void retrieveUserInfo() {
        ApiUserInfo apiUserInfo = new ApiUserInfo();
        UiCallback callback = new UiCallback() {
            @Override
            public void onBusinessResponse(boolean success, ResponseEntity responseEntity) {
                if (success) {
                    ApiUserInfo.Rsp userRsp = (ApiUserInfo.Rsp) responseEntity;
                    mUser = userRsp.getUser();
                }
                mBus.postSticky(new UserEvent());
            }
        };
        mClient.enqueue(apiUserInfo, callback, getClass().getSimpleName());
    }

    //获取购物车列表
    @Override public void retrieveCartList() {
        ApiCartList apiCartList = new ApiCartList();
        UiCallback cb = new UiCallback() {
            @Override
            public void onBusinessResponse(boolean success, ResponseEntity rsp) {

                if (success) {
                    ApiCartList.Rsp listRsp = (ApiCartList.Rsp) rsp;
                    mCartGoodsList = listRsp.getData().getGoodsList();
                    mCartBill = listRsp.getData().getCartBill();
                }

                mBus.postSticky(new CartEvent());
            }
        };

        mClient.enqueue(apiCartList, cb, getClass().getSimpleName());
    }

    //获取地址列表
    @Override public void retrieveAddressList() {
        ApiAddressList apiAddressList = new ApiAddressList();
        UiCallback uiCallback = new UiCallback() {
            @Override
            public void onBusinessResponse(boolean success, ResponseEntity responseEntity) {
                if (success) {
                    ApiAddressList.Rsp listRsp = (ApiAddressList.Rsp) responseEntity;
                    mAddressList = listRsp.getData();
                }
                mBus.postSticky(new AddressEvent());
            }
        };
        mClient.enqueue(apiAddressList, uiCallback, getClass().getSimpleName());
    }

    //获取默认地址
    @Override public Address getDefaultAddress() {
        if (hasAddress()) {
            for (Address address : mAddressList) {
                if (address.isDefault()) return address;
            }
        }
        return null;
    }

    //清空用户
    @Override public void clear() {
        mUser = null;
        mSession = null;
        mCartBill = null;
        mCartGoodsList = null;

        mClient.cancelByTag(getClass().getSimpleName());

        mBus.postSticky(new UserEvent());
        mBus.postSticky(new CartEvent());
        mBus.postSticky(new AddressEvent());
    }

    //用户是否存在
    @Override public boolean hasUser() {
        return mSession != null && mUser != null;
    }

    //购物车是否存在
    @Override public boolean hasCart() {
        return mCartGoodsList != null && !mCartGoodsList.isEmpty();
    }

    //地址是否存在
    @Override public boolean hasAddress() {
        return mAddressList != null && !mAddressList.isEmpty();
    }

    //获取sessionid
    @Override public Session getSession() {
        return mSession;
    }

    //获取用户
    @Override public User getUser() {
        return mUser;
    }

    //获取购物车列表
    @Override public List<CartGoods> getCartGoodsList() {
        return mCartGoodsList;
    }

    @Override public CartBill getCartBill() {
        return mCartBill;
    }

    @Override public List<Address> getAddressList() {
        return mAddressList;
    }

}
