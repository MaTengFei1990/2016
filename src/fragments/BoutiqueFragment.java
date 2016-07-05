package fragments;

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

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuliCenterMainActivity;
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/6/28.
 */
public class BoutiqueFragment extends Fragment {
    FuliCenterMainActivity mContext;
    ArrayList<BoutiqueBean> mBoutiqueList;

    BoutiqueAdapter mAdapter;
    private int action = I.ACTION_DOWNLOAD;
    String path;
    SwipeRefreshLayout mSwipRefreshLayout;

    RecyclerView mRecyclerView;
    TextView mtvHint;
    LinearLayoutManager mLinearLayoutManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuliCenterMainActivity) getActivity();
        View layout = View.inflate(mContext, R.layout.fragment_boutique, null);
        mBoutiqueList = new ArrayList<BoutiqueBean>();
        initView(layout);
        setListener();
        initData();
        return layout;
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

                                getPath();
                                mContext.executeRequest(new GsonRequest<BoutiqueBean[]>(path,
                                        BoutiqueBean[].class, responseDownloadNewGoodListener(),
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
                        mContext.executeRequest(new GsonRequest<BoutiqueBean[]>(path,
                                BoutiqueBean[].class, responseDownloadNewGoodListener(),
                                mContext.errorListener()));

                    }
                });

    }

    private void initData() {
        try {

            getPath();
            mContext.executeRequest(new GsonRequest<BoutiqueBean[]>(path,
                    BoutiqueBean[].class, responseDownloadNewGoodListener(),
                    mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private com.android.volley.Response.Listener<BoutiqueBean[]> responseDownloadNewGoodListener() {
        return new Response.Listener<BoutiqueBean[]>() {
            @Override
            public void onResponse(BoutiqueBean[] boutiqueBeen) {
                Log.i("main", "responseDownloadNewGoodListener=" + boutiqueBeen);

                if (boutiqueBeen != null) {
                    Log.i("main", "responseDownloadNewGoodListener=" + boutiqueBeen.length);
                    mAdapter.setMore(true);
                    mSwipRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText(getResources().getString(R.string.load_more));

                    ArrayList<BoutiqueBean> list = Utils.array2List(boutiqueBeen);
                    Log.i("main", "list.size=" + list.size() );
                    Log.i("main", "action=" + action );

                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItem(list);

                    } else if (action == I.ACTION_PULL_UP) {
                        mAdapter.addItems(list);
                    }
                    if (boutiqueBeen.length < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(getResources().getString(R.string.no_more));
                    }
                }
            }
        };
    }

    private String getPath() {
        try {
            path = new ApiParams()
                    .getRequestUrl(I.REQUEST_FIND_BOUTIQUES);
            Log.i("main", "path=" + path);
            return path;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //
    private void initView(View layout) {
        mSwipRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.sfl_boutique);
        mSwipRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mtvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_boutique);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new BoutiqueAdapter(mContext, mBoutiqueList);
        mRecyclerView.setAdapter(mAdapter);
    }
}
