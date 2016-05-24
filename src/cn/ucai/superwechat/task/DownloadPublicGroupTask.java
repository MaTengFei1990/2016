package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperweChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.Contact;
import cn.ucai.superwechat.bean.Group;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by sks on 2016/5/23.
 */
public class DownloadPublicGroupTask extends BaseActivity {
    private static final String TAG = DownloadContactListTask.class.getName();
    Context mContext;
    String username;
    String path;

    public DownloadPublicGroupTask(Context mContext, String username) {
        this.mContext = mContext;
        this.username = username;
        initPath();
    }

    public DownloadPublicGroupTask(Context mContext, String username, int pageIdDefult, int pageSizeDefult) {
        super();
    }

    private void initPath() {
        try {
            path = new ApiParams().with(I.Contact.USER_NAME, username)
                    .with(I.PAGE_ID,I.PAGE_ID_DEFULT+"").with(I.PAGE_SIZE,I.PAGE_SIZE_DEFULT+"")
                    .getRequestUrl(I.REQUEST_FIND_PUBLIC_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void excute(){
        executeRequest(new GsonRequest<Group[]>(path,Group[].class,
                responseDownloadPublicGroupTaskListener(),errorListener()));
    }

    private Response.Listener<Group[]> responseDownloadPublicGroupTaskListener() {
        return  new Response.Listener<Group[]>() {
            @Override
            public void onResponse(Group[] groups) {
                if (groups != null) {
                    ArrayList<Group> list = Utils.array2List(groups);
                    ArrayList<Group> publicGroupList = SuperweChatApplication.getInstance()
                            .getPublicGroupList();
                    for (Group g : list) {
                        if (!publicGroupList.contains(g)) {
                            publicGroupList.add(g);

                        }

                    }
                    mContext.sendStickyBroadcast(new Intent("updapte_contact_list"));
                }
            }
        };
    }

}

