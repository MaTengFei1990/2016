package cn.ucai.fulicenter.activity;

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

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by sks on 2016/6/23.
 */
public class BoutiqueChildActivity extends BaseActivity {
    public static final String TAG = BoutiqueChildActivity.class.getName();

    BoutiqueChildActivity mContext;
    ArrayList<NewGoodBean> mGoodList;
    GoodAdapter mAdapter;
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
        setContentView(R.layout.activity_boutique_child);
        mContext = this;
        mGoodList = new ArrayList<NewGoodBean>();
        initView();
        setListener();
        initData();

    }

    private void setListener() {
        setPullDownRefreshListener();
        setrPullUpRefreshListener();
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
                mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                        NewGoodBean[].class, responseDownloadNewGoodListener(),
                        mContext.errorListener()));

            }
        });

    }

    private void initData() {
        try {

            getPath(pageId);
            mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                    NewGoodBean[].class, responseDownloadNewGoodListener(),
                    mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<NewGoodBean[]> responseDownloadNewGoodListener() {
        return new Response.Listener<NewGoodBean[]>() {
            @Override
            public void onResponse(NewGoodBean[] newGoodBeen) {
                Log.i("main", "responseDownloadNewGoodListener=" + newGoodBeen);

                if (newGoodBeen != null) {
                    Log.i("main", "responseDownloadNewGoodListener=" + newGoodBeen.length);
                    mAdapter.setMore(true);
                    mSwipRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText(getResources().getString(R.string.load_more));

                    ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
                    Log.i("main", "list.size=" + list.size() );
                    Log.i("main", "action=" + action );

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

    private String getPath(int pageId) {
        try {
            int catId = getIntent().getIntExtra(I.Boutique.CAT_ID, 0);
            path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID, catId + "")
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            Log.e("main", "path=" + path);
            return path;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//
    private void initView() {
        mSwipRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sfl_boutique_child);
        mSwipRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mtvHint = (TextView) findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_boutique_child);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodAdapter(mContext, mGoodList,I.SORT_BY_ADDTIME_DESC);
        mRecyclerView.setAdapter(mAdapter);
        String BoutiqueChildTitle = getIntent().getStringExtra(I.Boutique.NAME);
        DisplayUtils.initBackWithTitle(mContext,BoutiqueChildTitle);
    }
}