package fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectActivity;
import cn.ucai.fulicenter.activity.SettingsActivity;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.UserUtils;

/**
 * Created by sks on 2016/6/30.
 */
public class PersonalCenterFragment extends Fragment {
    Context mContext;

    NetworkImageView mivUserAvatar;
    TextView mtvUserName;
    TextView mtvCollectCount;
    TextView mtvSettings;
    ImageView mivMassage;
    LinearLayout mLayoutCenterCollect;
    RelativeLayout mLyoutCenterUserInfo;
    int mCollectCount;
    MyClickListener listener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View layout = View.inflate(mContext, R.layout.fragment_personal_center, null);
        initView(layout);
        initData();
        setListener();

        return layout;

    }

    private void setListener() {
        registerCollectCountChangedListener();
        registerUpdateUserReceiver();
        listener = new MyClickListener();
        mtvSettings.setOnClickListener(listener);
        mLyoutCenterUserInfo.setOnClickListener(listener);
        mLayoutCenterCollect.setOnClickListener(listener);
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_center_setting:
                case R.id.center_user_info:
                    startActivity(new Intent(mContext,SettingsActivity.class));
                    break;
                case R.id.layout_center_collect:
                    startActivity(new Intent(mContext, CollectActivity.class));
                    break;
            }
        }
    }

    private void initData() {
        mCollectCount = FuLiCenterApplication.getInstance().getCollectCount();
        mtvCollectCount.setText(""+mCollectCount);
        Log.e("main", "mCollectCount=" + mCollectCount);
        if (FuLiCenterApplication.getInstance().getUser() != null) {
            UserUtils.setCurrentUserBeanAvatar(mivUserAvatar);
            UserUtils.setCurrentUserBeanNick(mtvUserName);

        }
    }

    private void initView(View layout) {
        mivUserAvatar = (NetworkImageView) layout.findViewById(R.id.iv_use_avtar);
        mtvUserName = (TextView) layout.findViewById(R.id.iv_use_name);
        mLayoutCenterCollect = (LinearLayout) layout.findViewById(R.id.layout_center_collect);
        mtvCollectCount = (TextView) layout.findViewById(R.id.tv_collect_count);
        mtvSettings = (TextView) layout.findViewById(R.id.tv_center_setting);
        mivMassage = (ImageView) layout.findViewById(R.id.iv_persona_center_msg);
        mLyoutCenterUserInfo = (RelativeLayout) layout.findViewById(R.id.center_user_info);
        initOrderList(layout);
    }

    private void initOrderList(View layout) {
        GridView mOrderList = (GridView) layout.findViewById(R.id.center_user_order_list);
        ArrayList<HashMap<String, Object>> imageList = new ArrayList<HashMap<String, Object>>();



        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("image", R.drawable.order_list1);
        imageList.add(map1);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("image", R.drawable.order_list2);
        imageList.add(map2);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("image", R.drawable.order_list3);
        imageList.add(map3);
        HashMap<String, Object> map4 = new HashMap<String, Object>();
        map4.put("image", R.drawable.order_list4);
        imageList.add(map4);
        HashMap<String, Object> map5 = new HashMap<String, Object>();
        map5.put("image", R.drawable.order_list5);
        imageList.add(map5);
        Log.e("main", "imageList.size=" + imageList.size());

        SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, imageList, R.layout.simple_grid_item, new String[]{"image"}, new int[]{R.id.images});

        mOrderList.setAdapter(simpleAdapter);
    }

    class CollectCountChangedReivcer extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }

    CollectCountChangedReivcer mReceiver;
    private void registerCollectCountChangedListener() {
        mReceiver = new CollectCountChangedReivcer();
        IntentFilter filter = new IntentFilter("update_collect_count");
        mContext.registerReceiver(mReceiver, filter);

    }
    class UpdateUserChangedReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            new DownloadCollectCountTask(mContext).execute();
            initData();
        }
    }

    UpdateUserChangedReciver mUserReceiver;
    private void registerUpdateUserReceiver() {
        mUserReceiver = new UpdateUserChangedReciver();
        IntentFilter filter = new IntentFilter("update_user");
        mContext.registerReceiver(mUserReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
        if (mUserReceiver != null) {
            mContext.unregisterReceiver(mUserReceiver);
        }
    }


}
