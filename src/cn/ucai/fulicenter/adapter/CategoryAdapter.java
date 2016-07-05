package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryChildActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;



/**
 * Created by sks on 2016/6/28.
 */
public class CategoryAdapter extends BaseExpandableListAdapter {
    Context mContext;
    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;

    public CategoryAdapter(Context mContext,ArrayList<CategoryGroupBean> mGroupList,
                            ArrayList<ArrayList<CategoryChildBean>> mChildList) {
        this.mGroupList = mGroupList;
        this.mContext = mContext;
        this.mChildList = mChildList;
    }

    @Override
    public int getGroupCount() {
        return mGroupList == null ? 0 : mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList == null ||mGroupList.get(groupPosition)==null? 0 : mChildList.get(groupPosition).size();
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        return mChildList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View layout, ViewGroup parent) {
        ViewGroupHoder holder=null;
        Log.e("main", "layout=" + layout);
        if (layout == null) {
            Log.i("main", "1111111111111111");
            layout = View.inflate(mContext, R.layout.item_categroy_group, null);
            holder = new ViewGroupHoder();
            holder.ivIndicator = (ImageView) layout.findViewById(R.id.ivIndicator);
            holder.ivGroupThumb = (NetworkImageView) layout.findViewById(R.id.ivGroupThumb);
            holder.tvGroupName = (TextView) layout.findViewById(R.id.tvGroupName);
            layout.setTag(holder);
        } else {
            holder = (ViewGroupHoder)layout.getTag();

        }
        CategoryGroupBean group =  getGroup(groupPosition);
        holder.tvGroupName.setText(group.getName());
        String imUrl=group.getImageUrl();
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_CHILD_IMAGE_URL + imUrl;
        ImageUtils.setThunb(url,holder.ivGroupThumb);
        if (isExpanded) {
            holder.ivIndicator.setImageResource(R.drawable.expand_off);

        } else {
            holder.ivIndicator.setImageResource(R.drawable.expand_on);
        }
        return layout;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View layout, ViewGroup parent) {
        ViewChildHoder holder = null;
        if (layout == null) {
            layout = View.inflate(mContext, R.layout.item_categroy_child, null);
            holder = new ViewChildHoder();
            holder.layoutChild = (RelativeLayout) layout.findViewById(R.id.Layout_category_child);
            holder.ivCategoryChildThumb = (NetworkImageView) layout.findViewById(R.id.ivCategoryChildThumb);
            holder.tvCategoryChildName = (TextView) layout.findViewById(R.id.tvCategoryChildName);
            layout.setTag(holder);
        } else {
            holder = (ViewChildHoder) layout.getTag();
        }
        final CategoryChildBean child = getChild(groupPosition, childPosition);
        String name = child.getName();
        holder.tvCategoryChildName.setText(name);

        String imUrl = child.getImageUrl();
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_CHILD_IMAGE_URL + imUrl;
        ImageUtils.setThunb(url, holder.ivCategoryChildThumb);
        holder.layoutChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, CategoryChildActivity.class)
                .putExtra(I.CategoryChild.CAT_ID,child.getId())
                .putExtra(I.CategoryGroup.NAME,mGroupList.get(groupPosition).getName())
                .putExtra("childList",(ArrayList<CategoryChildBean>)mChildList.get(groupPosition)));
            }
        });
        return layout;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ViewGroupHoder {
        ImageView ivIndicator;
        NetworkImageView ivGroupThumb;
        TextView tvGroupName;
    }
    class ViewChildHoder {
        RelativeLayout layoutChild;
        NetworkImageView ivCategoryChildThumb;
        TextView tvCategoryChildName;

    }

    public void addItems(ArrayList<CategoryGroupBean> grouplist,
                         ArrayList<ArrayList<CategoryChildBean>> childlist) {
        this.mGroupList.addAll(grouplist);
        this.mChildList.addAll(childlist);
        notifyDataSetChanged();
    }
}
