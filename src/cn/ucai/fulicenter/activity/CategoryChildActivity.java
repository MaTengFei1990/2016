package cn.ucai.fulicenter.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.ColorBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.CatChildFilterButton;
import cn.ucai.fulicenter.view.ColorFilterButton;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by sks on 2016/6/29.
 */
public class CategoryChildActivity extends BaseActivity {
    CategoryChildActivity mContext;
    ArrayList<NewGoodBean> mGoodList;
    GoodAdapter mAdapter;
    private int pageId;
    private int action = I.ACTION_DOWNLOAD;
    String path;
    int catId;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;
    Button mbtnPriceSort;
    Button mbtnAddTimeSort;
    boolean mSortByPriceAsc;
    boolean mSortByAddTimeAsc;
    ColorFilterButton mColorFilterButton;
    String mGroupName;
    ArrayList<CategoryChildBean> mChildList;
    CatChildFilterButton mCatChildFilterButton;
    String groupName;
    ArrayList<CategoryChildBean> mchildList;
    private int sortBy;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_category_child);
        mContext = this;
        sortBy = I.SORT_BY_ADDTIME_DESC;
        mGoodList = new ArrayList<NewGoodBean>();
        mchildList = new ArrayList<CategoryChildBean>();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setPullDownRefreshListener();
        setrPullUpRefreshListener();
        SortStateChangedListener mSortStateChangedListener=new SortStateChangedListener();
        mbtnPriceSort.setOnClickListener(mSortStateChangedListener);
        mbtnAddTimeSort.setOnClickListener(mSortStateChangedListener);
        mCatChildFilterButton.setOnCatFilterClickListener(groupName,mChildList);
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
                                mSwipeRefreshLayout.setRefreshing(true);
                                action=I.ACTION_PULL_DOWN;
                                pageId += I.PAGE_ID_DEFAULT;
                                getPath(pageId);
                                mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                                        NewGoodBean[].class, responseDownloadNewGoodListener(),
                                        mContext.errorListener()));
                            }
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LastItemPostion=mGridLayoutManager.findLastVisibleItemPosition();

                        mSwipeRefreshLayout.setEnabled(mGridLayoutManager
                                .findFirstCompletelyVisibleItemPosition()==0);
                    }
                });
    }



    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mtvHint.setVisibility(View.VISIBLE);
                        pageId=0;
                        action=I.ACTION_PULL_DOWN;
                        getPath(pageId);
                        mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                                NewGoodBean[].class, responseDownloadNewGoodListener(),
                                mContext.errorListener()));

                    }
                });

    }

    private void initData() {
        mchildList = (ArrayList<CategoryChildBean>) getIntent().getSerializableExtra("childList");
        getPath(pageId);
        mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                NewGoodBean[].class, responseDownloadNewGoodListener(),
                mContext.errorListener()));
        String colorPath = null;
        try {
            colorPath = new ApiParams()
                    .with(I.Color.CAT_ID, catId + "")
                    .getRequestUrl(I.REQUEST_FIND_COLOR_LIST);

            mContext.executeRequest(new GsonRequest<ColorBean[]>(colorPath, ColorBean[].class,
                    responseDownloadColorListener(), mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private com.android.volley.Response.Listener<ColorBean[]> responseDownloadColorListener() {
        return new Response.Listener<ColorBean[]>() {
            @Override
            public void onResponse(ColorBean[] colorBeen) {
                if (colorBeen != null) {
                    mColorFilterButton.setVisibility(View.VISIBLE);
                    ArrayList<ColorBean> list = Utils.array2List(colorBeen);
                    mColorFilterButton.setOnColorFilterClickListener(mGroupName,mChildList,list);
                }
            }
        };
    }


    private String getPath(int pageId) {
        try {
            catId = getIntent().getIntExtra(I.CategoryChild.CAT_ID, 0);
            path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID, catId + "")
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private com.android.volley.Response.Listener<NewGoodBean[]> responseDownloadNewGoodListener() {
        return new Response.Listener<NewGoodBean[]>() {
            @Override
            public void onResponse(NewGoodBean[] newGoodBeen) {
                Log.i("main", "responseDownloadNewGoodListener=" + newGoodBeen);

                if (newGoodBeen != null) {
                    Log.i("main", "responseDownloadNewGoodListener=" + newGoodBeen.length);
                    mAdapter.setMore(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText(getResources().getString(R.string.load_more));

                    ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);

                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItem(list);

                    } else if (action == I.ACTION_PULL_UP) {
                        mAdapter.addItems(list);
                    }
                    if (newGoodBeen.length < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(getResources().getString(R.string.no_more));
                    }
                }
            }
        };
    }
    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sfl_category_child);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mtvHint = (TextView) findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_category_child);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodAdapter(mContext, mGoodList,sortBy);
        mRecyclerView.setAdapter(mAdapter);
       // mGroupName = getIntent().getStringExtra(I.CategoryGroup.NAME);
        groupName = getIntent().getStringExtra(I.CategoryGroup.NAME);
        DisplayUtils.initBack(mContext);
        mbtnPriceSort = (Button) findViewById(R.id.btn_price_sort);
        mbtnAddTimeSort = (Button) findViewById(R.id.btn_add_time_sort);
        mColorFilterButton = (ColorFilterButton) findViewById(R.id.btnColorfilter);
        mColorFilterButton.setVisibility(View.VISIBLE);
        mCatChildFilterButton = (CatChildFilterButton) findViewById(R.id.btnCatChildFilter);
        mCatChildFilterButton.setText(groupName);
        Log.e("main", "groupName=" + groupName);



    }

    class SortStateChangedListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Drawable right = null;
            int resId;
            switch (v.getId()) {
                case R.id.btn_price_sort:
                    if (mSortByPriceAsc) {
                        sortBy = I.SORT_BY_PRICE_ASC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_up);
                        resId = R.drawable.arrow_order_up;
                    } else {
                        sortBy = I.SORT_BY_PRICE_DESC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_down);
                        resId = R.drawable.arrow_order_down;
                    }
                    mSortByPriceAsc =! mSortByPriceAsc;
                    right.setBounds(0,0, ImageUtils.getDrawableWidth(mContext,resId),
                            ImageUtils.getDrawableHeight(mContext,resId));
                    mbtnPriceSort.setCompoundDrawables(null, null, right, null);
                    break;
                case R.id.btn_add_time_sort:
                    if (mSortByAddTimeAsc) {
                        sortBy = I.SORT_BY_ADDTIME_ASC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_up);
                        resId = R.drawable.arrow_order_up;
                    } else {
                        sortBy = I.SORT_BY_ADDTIME_DESC;
                        right = mContext.getResources().getDrawable(R.drawable.arrow_order_down);
                        resId = R.drawable.arrow_order_down;
                    }
                    mSortByAddTimeAsc = !mSortByAddTimeAsc;
                    right.setBounds(0,0, ImageUtils.getDrawableWidth(mContext,resId),
                            ImageUtils.getDrawableHeight(mContext,resId));
                    mbtnAddTimeSort.setCompoundDrawables(null, null, right, null);
                    break;
            }
            mAdapter.setSortBy(sortBy);
        }
    }
}
