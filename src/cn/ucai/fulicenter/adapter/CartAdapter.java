package cn.ucai.fulicenter.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/6/23.
 */
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<CartBean> mCartList;

    CartItemViewHolder cartItemViewHolder;
    private boolean isMore;

    public boolean isMore () {
        return isMore;
    }


    public void setMore(boolean more) {
        isMore = more;
    }


    public CartAdapter(Context mContext, ArrayList<CartBean> list) {
        this.mContext = mContext;
        this.mCartList = list;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           LayoutInflater inflater = LayoutInflater.from(mContext);
           RecyclerView.ViewHolder holder = new CartItemViewHolder(inflater.inflate(R.layout.item_cart, parent, false));
           return holder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        cartItemViewHolder = (CartItemViewHolder) holder;
        final CartBean cart = mCartList.get(position);
        GoodDetailsBean goods = cart.getGoods();
        if (goods == null) {
            return;
        }
        cartItemViewHolder.tvgoodName.setText(goods.getGoodsName());
        cartItemViewHolder.tvCartCount.setText(""+cart.getCount());
        cartItemViewHolder.mchkCart.setChecked(cart.isChecked());
        cartItemViewHolder.tvprice.setText(goods.getRankPrice());
        ImageUtils.setNewGoodThunb(goods.getGoodsThumb(), cartItemViewHolder.iv);
        AddDelCartClickListener listener = new AddDelCartClickListener(goods);
        cartItemViewHolder.mivAdd.setOnClickListener(listener);
        cartItemViewHolder.mivReduce.setOnClickListener(listener);

        cartItemViewHolder.mchkCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cart.setChecked(isChecked);
                new UpdateCartTask(mContext, cart).execute();
            }
        });
    }
    @Override
    public int getItemCount() {

        Log.i("main","mGoodList.size="+(mCartList==null?1:mCartList.size()+1));
        return mCartList==null?0:mCartList.size();

    }

    public void initItem(ArrayList<CartBean> list) {
        if (mCartList != null && !mCartList.isEmpty()) {
            mCartList.clear();
        }
        mCartList.addAll(list);
        notifyDataSetChanged();
    }



    public void addItems(ArrayList<CartBean> list) {
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView iv;

        TextView tvprice;
        TextView tvgoodName;
        TextView tvCartCount;
        CheckBox mchkCart;
        ImageView mivAdd;
        ImageView mivReduce;


        public CartItemViewHolder(View itemView) {
            super(itemView);
            iv = (NetworkImageView) itemView.findViewById(R.id.uvGoodsThumb);
            tvprice = (TextView) itemView.findViewById(R.id.tvGoodsPrice);
            tvgoodName = (TextView) itemView.findViewById(R.id.goodsName);
            tvCartCount = (TextView) itemView.findViewById(R.id.tvCart_Count);
            mchkCart = (CheckBox) itemView.findViewById(R.id.chkSelect);
            mivAdd = (ImageView) itemView.findViewById(R.id.ivAddCat);
            mivReduce = (ImageView) itemView.findViewById(R.id.ivReduceCart);
        }

    }

    class AddDelCartClickListener implements View.OnClickListener {
        public AddDelCartClickListener(GoodDetailsBean good) {
            this.good = good;
        }

        GoodDetailsBean good;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivAddCat:
                    Utils.addCart(mContext,good);
                    break;
                case R.id.ivReduceCart:
                    Utils.delCart(mContext, good);
                    break;
            }
        }
    }
}
