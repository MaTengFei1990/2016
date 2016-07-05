package fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuliCenterMainActivity;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/6/28.
 */
public class CartFragment extends Fragment {
    FuliCenterMainActivity mContext;
    ArrayList<CartBean> mCartList;

    CartAdapter mAdapter;
    private int action = I.ACTION_DOWNLOAD;
    String path;
    SwipeRefreshLayout mSwipRefreshLayout;

    RecyclerView mRecyclerView;
    TextView mtvHint;
    LinearLayoutManager mLinearLayoutManager;
    TextView mtvNothing;
    int pageId;
    TextView mtvRankPrice;
    TextView mtvSaveprice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuliCenterMainActivity) getActivity();
        View layout = View.inflate(mContext, R.layout.fragment_cart, null);
        mCartList = new ArrayList<CartBean>();
        initView(layout);
        setListener();
        initData();
        return layout;
    }


    private void setListener() {
        setPullDownRefreshListener();
        setrPullUpRefreshListener();
        registerUpdateCartListener();
    }

    private void setrPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    int LastItemPostion;
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                                LastItemPostion == mAdapter.getItemCount() - 1) {
                            if (mAdapter.isMore()) {
                                mSwipRefreshLayout.setRefreshing(true);
                                action=I.ACTION_PULL_UP;
                                pageId += I.PAGE_ID_DEFAULT;
                                getPath();
                                mContext.executeRequest(new GsonRequest<CartBean[]>(path,
                                        CartBean[].class, responseDownloadCartListener(),
                                        mContext.errorListener()));
                            }
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LastItemPostion=mLinearLayoutManager.findLastVisibleItemPosition();

                        mSwipRefreshLayout.setEnabled(mLinearLayoutManager
                                .findFirstCompletelyVisibleItemPosition()==0);
                    }
                });
    }




    private void setPullDownRefreshListener() {
        mSwipRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mtvHint.setVisibility(View.VISIBLE);

                        action=I.ACTION_PULL_DOWN;
                        getPath();
                        mContext.executeRequest(new GsonRequest<CartBean[]>(path,
                                CartBean[].class, responseDownloadCartListener(),
                                mContext.errorListener()));

                    }
                });

    }

    private void initData() {
        try {

            getPath();
            ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
            mCartList.clear();
            mCartList.addAll(cartList);
            mAdapter.notifyDataSetChanged();
            sumPrice();
            if (mCartList == null || mCartList.size() == 0) {
                mtvNothing.setVisibility(View.VISIBLE);
            } else {
                mtvNothing.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<CartBean[]> responseDownloadCartListener() {
        return new Response.Listener<CartBean[]>() {
            @Override
            public void onResponse(CartBean[] boutiqueBeen) {
                Log.i("main", "responseDownloadNewGoodListener=" + boutiqueBeen);

                if (boutiqueBeen != null) {
                    Log.i("main", "responseDownloadNewGoodListener=" + boutiqueBeen.length);
                    mAdapter.setMore(true);
                    mSwipRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);


                    ArrayList<CartBean> list = Utils.array2List(boutiqueBeen);
                    Log.i("main", "list.size=" + list.size() );
                    Log.i("main", "action=" + action );

                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItem(list);

                    } else if (action == I.ACTION_PULL_UP) {
                        mAdapter.addItems(list);
                    }
                    if (boutiqueBeen.length < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                    }
                }
            }
        };
    }

    private String getPath() {
        try {
            path = new ApiParams()
                    .with(I.PAGE_ID,pageId+"")
                    .with(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                    .with(I.Cart.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                    .getRequestUrl(I.REQUEST_FIND_CARTS);
            return path;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //
    private void initView(View layout) {
        mSwipRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.sfl_cart);
        mSwipRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mtvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        mtvNothing = (TextView) layout.findViewById(R.id.tv_nothing);
        mtvNothing.setVisibility(View.GONE);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CartAdapter(mContext, mCartList);
        mRecyclerView.setAdapter(mAdapter);
        mtvSaveprice = (TextView) layout.findViewById(R.id.tvSavePrice);
        mtvRankPrice = (TextView) layout.findViewById(R.id.tvSumPrice);
    }
    public void sumPrice() {
        int sumPrice = 0;
        int currentPrice = 0;
        if (mCartList != null && mCartList.size() > 0) {
            for (CartBean cart : mCartList) {
                GoodDetailsBean goods = cart.getGoods();
                if (goods != null && cart.isChecked()) {
                    sumPrice += convertPrice(goods.getCurrencyPrice()) * cart.getCount();
                    currentPrice += convertPrice(goods.getRankPrice()) * cart.getCount();
                }

            }
        }
        int savePrice = sumPrice - currentPrice;
        mtvRankPrice.setText("合计：￥" + sumPrice);
        mtvSaveprice.setText("节省：￥" + savePrice);
    }
    private int convertPrice(String price) {
        price = price.substring((price.indexOf("￥") + 1));
        int p1 = Integer.parseInt(price);
        return p1;
    }

    class UpdateCartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }
    UpdateCartReceiver mReceiver;
    private void registerUpdateCartListener() {
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart");
        mContext.registerReceiver(mReceiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);

        }
    }
}
