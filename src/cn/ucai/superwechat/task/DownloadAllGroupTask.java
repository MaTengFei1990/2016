package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;


import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperweChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.Group;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by sks on 2016/5/23.
 */
public class DownloadAllGroupTask extends BaseActivity {
    private static final String TAG = DownloadContactListTask.class.getName();
    Context mContext;
    String username;
    String path;

    public DownloadAllGroupTask(Context mContext, String username) {
        this.mContext = mContext;
        this.username = username;
        initPath();
    }

    private void initPath()  {
        try {
            path = new ApiParams().with(I.Contact.USER_NAME, username)
                     .getRequestUrl(I.REQUEST_DOWNLOAD_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void excute(){
        executeRequest(new GsonRequest<Group[]>(path,Group[].class,
                responseDownloadAllGroupTaskListener(),errorListener()));
    }

    private Response.Listener<Group[]> responseDownloadAllGroupTaskListener() {
//        return  new Response.Listener<Group[]>() {
//            @Override
//            public void onResponse(Group[] groups) {
//                if (groups != null) {
//                    ArrayList<Group> groupList = SuperweChatApplication.getInstance()
//                            .getGropList();
//                    ArrayList<Group> list = Utils.array2List(groups);
//                    groupList.clear();
//                    groupList.addAll(list);
//                    ArrayList<Group> gropList =
//                            SuperweChatApplication.getInstance().getGropList();
//                    gropList.clear();
//                    gropList.addAll(list);
//
//                    mContext.sendStickyBroadcast(new Intent("update_group_list"));
        return null;
//                }
//            }
//        };
        }
    }



