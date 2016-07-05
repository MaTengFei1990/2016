package cn.ucai.fulicenter.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailActivity;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

import static cn.ucai.fulicenter.R.id.niv_good_thumb;

/**
 * Created by sks on 2016/6/23.
 */
public class GoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<NewGoodBean> mGoodList;

    GoodItemViewHolder goodHolder;
    FooterViewHolder footerHolder;

    private String footerText;
    private boolean isMore;

    int sortBy;

    public void getSortBy(int sortBy) {
        this.sortBy = sortBy;
        sort(sortBy);
        notifyDataSetChanged();
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }

    public boolean isMore () {
        return isMore;
    }


    public void setMore(boolean more) {
        isMore = more;
    }


    public GoodAdapter(Context mContext, ArrayList<NewGoodBean> mGoodList,int sortBy) {
        this.mContext = mContext;
        this.mGoodList = mGoodList;
        this.sortBy = sortBy;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           LayoutInflater inflater = LayoutInflater.from(mContext);
           RecyclerView.ViewHolder holder = null;
           Log.i("main", "viewType=" + viewType);
           switch (viewType) {
               case I.TYPE_ITEM:
                   holder = new GoodItemViewHolder(inflater.inflate(R.layout.item_new_good, parent, false));
                   break;
               case I.TYPE_FOOTER:
                   holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer, parent, false));
                   break;

           }
           return holder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            footerHolder = (FooterViewHolder) holder;
            footerHolder.tvFooter.setText(footerText);
            footerHolder.tvFooter.setVisibility(View.VISIBLE);
        }
        if (holder instanceof GoodItemViewHolder) {
            goodHolder = (GoodItemViewHolder) holder;
            Log.i("main", "mGoodList=" + mGoodList);
            final NewGoodBean good = mGoodList.get(position);
            goodHolder.tvGoodName.setText(good.getGoodsName());
            goodHolder.tvGoodPrice.setText(good.getCurrencyPrice());
            ImageUtils.setNewGoodThunb(good.getGoodsThumb(), goodHolder.nivThumb);

            goodHolder.layouGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, GoodDetailActivity.class)
                            .putExtra(D.NewGood.KEY_GOODS_ID, good.getGoodsId()));
                }
            });
        }
    }
    @Override
    public int getItemCount() {

        Log.i("main","mGoodList.size="+(mGoodList==null?1:mGoodList.size()+1));
        return mGoodList==null?1:mGoodList.size()+1;

    }

    @Override
    public int getItemViewType(int position) {
        Log.i("main", "position=" + position);
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }
    public void initItem(ArrayList<NewGoodBean> list) {
        if (mGoodList != null && !mGoodList.isEmpty()) {
            mGoodList.clear();
        }
        mGoodList.addAll(list);
        sort(sortBy);
        notifyDataSetChanged();
    }

    private void sort( final int sortBy) {
        Collections.sort(mGoodList,new Comparator<NewGoodBean>(){

            @Override
            public int compare(NewGoodBean g1, NewGoodBean g2) {
                int result=0;
                switch (sortBy) {
                    case I.SORT_BY_ADDTIME_ASC:
                        result = (int) (g1.getAddTime() - g2.getAddTime());
                        break;
                    case I.SORT_BY_ADDTIME_DESC:
                        result = (int) (g2.getAddTime() - g1.getAddTime());
                        break;
                    case I.SORT_BY_PRICE_ASC:
                    {
                        int p1 = convertPrice(g1.getCurrencyPrice());
                        int p2 = convertPrice(g2.getCurrencyPrice());
                        result = p1 - p2;
                    }
                    break;
                }
                return result;
            }

            private int convertPrice(String price) {
                price = price.substring((price.indexOf("￥") + 1));
                int p1 = Integer.parseInt(price);
                return p1;
            }
        });
    }

    public void addItems(ArrayList<NewGoodBean> list) {
        mGoodList.addAll(list);
        sort(sortBy);
        notifyDataSetChanged();
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        sort(sortBy);
        notifyDataSetChanged();
    }

    class GoodItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layouGood;
        NetworkImageView nivThumb;
        TextView tvGoodName;
        TextView tvGoodPrice;

        public GoodItemViewHolder(View itemView) {
            super(itemView);
            layouGood = (LinearLayout) itemView.findViewById(R.id.layout_good);
            nivThumb = (NetworkImageView) itemView.findViewById(niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            tvGoodPrice = (TextView) itemView.findViewById(R.id.tv_good_price);
        }

    }
}
