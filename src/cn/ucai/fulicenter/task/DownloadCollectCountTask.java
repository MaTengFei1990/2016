package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by sks on 2016/5/23.
 */
public class DownloadCollectCountTask extends BaseActivity {
    private static final String TAG = DownloadCollectCountTask.class.getName();
    Context mContext;
    String username;
    String path;

    public DownloadCollectCountTask(Context mContext) {
        this.mContext = mContext;
        initPath();
    }

    private void initPath() {
        try {
            User user = FuLiCenterApplication.getInstance().getUser();
            if (user != null) {
                path = new ApiParams()
                        .with(I.Collect.USER_NAME,username)
                        .getRequestUrl(I.REQUEST_FIND_COLLECT_COUNT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void execute() {
        Log.e(TAG, "path=" + path);
        if (path == null || path.isEmpty()) return;
        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class,
                responseDownloadCollectCountListener(), errorListener()));
    }
    private Response.Listener<MessageBean> responseDownloadCollectCountListener() {
            return  new Response.Listener<MessageBean>() {
                @Override
                public void onResponse(MessageBean messageBean) {
                    if (messageBean.isSuccess()) {
                        String count = messageBean.getMsg();
                        FuLiCenterApplication.getInstance().setCollectCount(Integer.parseInt(count));
                    } else {
                        FuLiCenterApplication.getInstance().setCollectCount(0);
                    }
                    Intent intent = new Intent("update_collect_count");
                    mContext.sendBroadcast(intent);
                }
            };
    }

}

