package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/5/23.
 */
public class DownloadCartListTask extends BaseActivity {
    private static final String TAG = DownloadCartListTask.class.getName();
    Context mContext;
    String username;
    int pageId ;
    int pageSize;
    String path;
    int ListSize;
    ArrayList<CartBean> list;

    public DownloadCartListTask(Context mContext, String username,int pageId,int pageSize) {
        this.mContext = mContext;
        this.username = FuLiCenterApplication.getInstance().getUserName();
        this.pageId = pageId;
        this.pageSize = pageSize;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams().with(I.Cart.USER_NAME, username)
                    .with(I.PAGE_ID,pageId+"")
                    .with(I.PAGE_SIZE,pageSize+"")
                    .getRequestUrl(I.REQUEST_FIND_CARTS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void execute(){
        executeRequest(new GsonRequest<CartBean[]>(path,CartBean[].class,
                responseDownloadCartListTaskListener(),errorListener()));
    }

    private Response.Listener<CartBean[]> responseDownloadCartListTaskListener() {
            return  new Response.Listener<CartBean[]>() {
                @Override
                public void onResponse(CartBean[] CartBeans) {
                    if (CartBeans != null) {
                         list = Utils.array2List(CartBeans);
                        for (CartBean cart : list) {
                            try {
                                path = new ApiParams().with(I.CategoryGood.GOODS_ID, cart.getGoodsId() + "")
                                        .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
                                executeRequest(new GsonRequest<GoodDetailsBean>(path,GoodDetailsBean.class,
                                        responseDownloadGoodDetailListener(cart),errorListener()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        mContext.sendStickyBroadcast(new Intent("updapte_CartBean_list"));
                    }
                }
            };
    }

    private Response.Listener<GoodDetailsBean> responseDownloadGoodDetailListener(final CartBean cart) {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                ListSize++;
                if (goodDetailsBean != null) {
                    cart.setGoods(goodDetailsBean);
                    ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
                    if (!cartList.contains(cart)) {
                        cartList.add(cart);
                    }
                }
                if (ListSize == list.size()) {
                    mContext.sendStickyBroadcast(new Intent("update_cart_list"));
                }
            }
        };
    }

}

