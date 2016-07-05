package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

public class GoodDetailActivity extends BaseActivity {
    public static String TAG = GoodDetailActivity.class.getName();
    GoodDetailActivity mContext;
    GoodDetailsBean mGoodDetails;

    int mGoodId;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;

    LinearLayout mLayoutColor;
    ImageView mivCollect;
    ImageView mivAddCart;
    ImageView mivShare;
    TextView mtvCartCount;

    TextView tvGoodName;
    TextView tvGoodEnglishName;
    TextView tvShopPrice;
    TextView tvCurrentPrice;
    WebView wvGoodBrief;

    int  mCurrentColor;
    boolean isCollect;
    int actionCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_details);
        mContext = this;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setCollectClickListener();
        setCartClickListener();
        RegisterUpdateCartListener();
        mivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "mivShareon.Click");
                showShare();
                Log.e(TAG, "mivShareon.showShare");
            }
        });

    }

    private void setCollectClickListener() {
        mivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = FuLiCenterApplication.getInstance().getUser();

                if (user == null) {
                    startActivity(new Intent(GoodDetailActivity.this, LoginActivity.class));
                } else {
                    try {
                    String path;
                    if (isCollect) {
                        actionCollect = I.ACTION_DEL_COLLECT;
                        path = new ApiParams()
                                .with(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                                .with(I.Collect.GOODS_ID, mGoodId+ "")
                                .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                        Log.e(TAG, "path=" + path);
                    }else{
                            actionCollect = I.ACTION_ADD_COLLECT;
                            path = new ApiParams()
                                    .with("m_user_Name", user.getMUserName())
                                    .with(I.Collect.GOODS_ID, mGoodId + "")
                                    .with(I.Collect.GOODS_NAME, mGoodDetails.getGoodsName())
                                    .with(I.Collect.GOODS_ENGLISH_NAME, mGoodDetails.getGoodsEnglishName())
                                    .with(I.Collect.GOODS_THUMB, mGoodDetails.getGoodsThumb())
                                    .with(I.Collect.GOODS_IMG, mGoodDetails.getGoodsImg())
                                    .with(I.Collect.ADD_TIME, mGoodDetails.getAddTime() + "")
                                    .getRequestUrl(I.REQUEST_ADD_COLLECT);
                            Log.e("main", "Add.path=" + path);
                        }
                        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class,
                                responseSetCollectListener(), errorListener()));
                    }catch (Exception e) {
                            e.printStackTrace();
                        }

                }
            }
        });
    }

    private Response.Listener<MessageBean> responseSetCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    if (actionCollect == I.ACTION_ADD_COLLECT) {
                        isCollect = true;
                        mivCollect.setImageResource(R.drawable.bg_collect_out);
                    } else {
                        isCollect = false;
                        mivCollect.setImageResource(R.drawable.bg_collect_in);
                    }

                    new DownloadCollectCountTask(mContext).execute();
                }
                Utils.showToast(mContext, messageBean.getMsg(), Toast.LENGTH_LONG);
            }
        };
    }

    private void setCartClickListener() {
        mivAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addCart(mContext, mGoodDetails);
            }
        });
    }

    private void initData() {
       mGoodId = getIntent().getIntExtra(D.NewGood.KEY_GOODS_ID, 0);
        try {
            String path = new ApiParams()
                    .with(D.NewGood.KEY_GOODS_ID, mGoodId + "")
                    .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);


                executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class,
            responseDownloadGoodDetailListener(), errorListener()));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private com.android.volley.Response.Listener<GoodDetailsBean> responseDownloadGoodDetailListener() {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                if (goodDetailsBean != null) {
                    mGoodDetails = goodDetailsBean;
                    DisplayUtils.initBackWithTitle(mContext, getResources().getString(R.string.title_activity_good_details));
                    tvGoodEnglishName.setText(mGoodDetails.getGoodsEnglishName());
                    tvGoodName.setText(mGoodDetails.getGoodsName());
                    tvShopPrice.setText(mGoodDetails.getCurrencyPrice());
                    wvGoodBrief.loadDataWithBaseURL(null, mGoodDetails.getGoodsBrief().trim(), D.TEXT_HTML, D.UTF_8, null);

                    initColorBanner();

                } else {
                    Utils.showToast(mContext, "商品下载失败", Toast.LENGTH_LONG);
                } 
            }
        };
    }

    private void initColorBanner() {
        updateColor(0);
        for (int i=0;i<mGoodDetails.getProperties().length;i++) {
            mCurrentColor = i;
            View layout = View.inflate(mContext, R.layout.layout_property_color, null);
            final NetworkImageView ivColor = (NetworkImageView) layout.findViewById(R.id.ivColorItem);
            String colorImg = mGoodDetails.getProperties()[i].getColorImg();
            if (colorImg.isEmpty()) {
                continue;
            }
            ImageUtils.setGoodDetailThunb(colorImg,ivColor);
            mLayoutColor.addView(layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateColor(mCurrentColor);

                }
            });

        }

    }

    private void updateColor(int i) {
        AlbumBean[] albums=mGoodDetails.getProperties()[i].getAlbums();
        String[] albumImgUrl = new String[albums.length];
        for (int j = 0; j < albumImgUrl.length; j++) {
            albumImgUrl[j] = albums[j].getImgUrl();
        }
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator, albumImgUrl, albums.length);
    }


    private void initView() {
        mivCollect = (ImageView) findViewById(R.id.ivCollect);
        mivAddCart = (ImageView) findViewById(R.id.ivAddCat);
        mivShare = (ImageView) findViewById(R.id.ivShare);
        mtvCartCount = (TextView) findViewById(R.id.tvCartCount);

        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        mLayoutColor = (LinearLayout) findViewById(R.id.LayoutColorSelector);
        tvCurrentPrice = (TextView) findViewById(R.id.tvCurrencyPrice);
        tvGoodEnglishName = (TextView) findViewById(R.id.tvGoodEngLishName);
        tvGoodName = (TextView) findViewById(R.id.tvGoodName);
        tvShopPrice = (TextView) findViewById(R.id.tvShopPrice);
        wvGoodBrief = (WebView) findViewById(R.id.wvGoodBrief);
        WebSettings setting = wvGoodBrief.getSettings();
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        setting.setBuiltInZoomControls(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initCollectStatus();
        initCartStattus();
    }

    private void initCartStattus() {
        int count = Utils.sumCartCount();
        if (count > 0) {
            mtvCartCount.setVisibility(View.VISIBLE);
            mtvCartCount.setText("" + count);
        } else {
            mtvCartCount.setVisibility(View.GONE);
            mtvCartCount.setText("0");

        }
    }

    private void initCollectStatus() {
        User user = FuLiCenterApplication.getInstance().getUser();
        if (user != null) {
            try {
                String path = new ApiParams()
                        .with(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUser().getMUserName())
                        .with(I.Collect.GOODS_ID, mGoodId+"")
                        .getRequestUrl(I.REQUEST_IS_COLLECT);
                Log.e("main", "REQUEST_IS_COLLECT.path=" + path);
                executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseIsCollectListener(), errorListener()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isCollect = false;
            mivCollect.setImageResource(R.drawable.bg_collect_in);

        }
    }

    private Response.Listener<MessageBean> responseIsCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override

            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    isCollect = true;
                    mivCollect.setImageResource(R.drawable.bg_collect_out);
                } else {
                    isCollect = false;
                    mivCollect.setImageResource(R.drawable.bg_collect_in);

                }
            }
        };
    }

    class UpdateCartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initCartStattus();

        }
    }
    UpdateCartReceiver mReceiver;
    private void RegisterUpdateCartListener() {
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);

        }
    }


    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }
}
