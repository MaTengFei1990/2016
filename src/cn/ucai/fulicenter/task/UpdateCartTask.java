package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by sks on 2016/7/4.
 */
public class UpdateCartTask extends BaseActivity {

    Context mContext;
    CartBean mCart;
    String path;
    int actionType = 0;


    public UpdateCartTask(Context mContext, CartBean mCart) {
        this.mContext = mContext;
        this.mCart = mCart;
        initPath();
    }

    private void initPath() {
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        try {
            if (cartList.contains(mCart)) {
                if (mCart.getCount() <= 0) {
                    actionType = 0;
                    path = new ApiParams()
                            .with(I.Cart.ID, mCart.getId() + "")
                            .getRequestUrl(I.REQUEST_DELETE_CART);
                } else {
                    actionType = 1;
                    path = new ApiParams()
                            .with(I.Cart.IS_CHECKED, mCart.isChecked() + "")
                            .with(I.Cart.COUNT, mCart.getCount() + "")
                            .with(I.Cart.ID, mCart.getId() + "")
                            .getRequestUrl(I.REQUEST_UPDATE_CART);
                }
            } else {
                actionType = 2;
                path = new ApiParams()
                        .with(I.Cart.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                        .with(I.Cart.GOODS_ID, mCart.getGoodsId() + "")
                        .with(I.Cart.COUNT, "1")
                        .with(I.Cart.IS_CHECKED, mCart.isChecked() + "")
                        .getRequestUrl(I.REQUEST_ADD_CART);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseUpdateCartListener(), errorListener()));
    }

    private Response.Listener<MessageBean> responseUpdateCartListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    ArrayList<CartBean> CartList = FuLiCenterApplication.getInstance().getCartList();
                    if (actionType == 0) {

                        CartList.remove(mCart);

                    }
                    if (actionType == 1) {
                        CartList.set(CartList.indexOf(mCart), mCart);
                    }
                    if (actionType == 2) {
                        mCart.setId(Integer.parseInt(messageBean.getMsg()));
                        CartList.add(mCart);

                    }
                    mContext.sendBroadcast(new Intent("update_cart"));

                }
            }
        };
    }
 }


