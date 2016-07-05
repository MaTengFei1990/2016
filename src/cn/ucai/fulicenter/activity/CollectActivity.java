package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by sks on 2016/6/23.
 */
public class CollectActivity extends BaseActivity {
    public static final String TAG = CollectActivity.class.getName();

    CollectActivity mContext;
    ArrayList<CollectBean> mCollectList;
    CollectAdapter mAdapter;
    private int pageId = 0;
    private int action = I.ACTION_DOWNLOAD;
    String path;

    SwipeRefreshLayout mSwipRefreshLayout;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        mContext = this;
        mCollectList = new ArrayList<CollectBean>();
        initView();
        setListener();
        initData();

    }

    private void setListener() {
        setPullDownRefreshListener();
        setrPullUpRefreshListener();
        registerUpdateCollectListener();
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
                        action=I.ACTION_PULL_DOWN;
                        pageId += I.PAGE_ID_DEFAULT;
                        getPath(pageId);
                        mContext.executeRequest(new GsonRequest<CollectBean[]>(path,
                                CollectBean[].class, responseDownloadCollectListener(),
                                mContext.errorListener()));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LastItemPostion=mGridLayoutManager.findLastVisibleItemPosition();

                mSwipRefreshLayout.setEnabled(mGridLayoutManager
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
                pageId=0;
                action=I.ACTION_PULL_DOWN;
                getPath(pageId);
                mContext.executeRequest(new GsonRequest<CollectBean[]>(path,
                        CollectBean[].class, responseDownloadCollectListener(),
                        mContext.errorListener()));

            }
        });

    }

    private void initData() {
        try {

            getPath(pageId);
            mContext.executeRequest(new GsonRequest<CollectBean[]>(path,
                    CollectBean[].class, responseDownloadCollectListener(),
                    mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<CollectBean[]> responseDownloadCollectListener() {
        return new Response.Listener<CollectBean[]>() {
            @Override
            public void onResponse(CollectBean[] Collects) {
                Log.e(TAG, "22222222");
                if (Collects != null) {
                    mAdapter.setMore(true);
                    mSwipRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText(getResources().getString(R.string.load_more));

                    ArrayList<CollectBean> list = Utils.array2List(Collects);
                    Log.e(TAG, "list.size=" + list.size() );


                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItem(list);

                    } else if (action == I.ACTION_PULL_UP) {
                        mAdapter.addItems(list);
                    }
                    if (Collects.length < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(getResources().getString(R.string.no_more));
                    }
                }
            }
        };
    }

    private String getPath(int pageId) {
        try {
            String userName = FuLiCenterApplication.getInstance().getUser().getMUserName();
            path = new ApiParams()
                    .with(I.Collect.USER_NAME, userName)
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_COLLECTS);
            Log.e(TAG, "path=" + path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//
    private void initView() {
        mSwipRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sfl_collect);
//        mSwipRefreshLayout.setColorSchemeColors(
//                R.color.google_blue,
//                R.color.google_green,
//                R.color.google_red,
//                R.color.google_yellow
//        );
        mCollectList = new ArrayList<CollectBean>();
        mtvHint = (TextView) findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_collect);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new CollectAdapter(mContext, mCollectList);
        Log.e("main", "mCollect=" + mCollectList);
        mRecyclerView.setAdapter(mAdapter);
        DisplayUtils.initBackWithTitle(mContext,"收藏的宝贝");
    }

    class UpdateCollectListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();

        }
    }
    UpdateCollectListReceiver mReceiver;
    private void registerUpdateCollectListener() {
        IntentFilter filter = new IntentFilter("update_collect_count");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}