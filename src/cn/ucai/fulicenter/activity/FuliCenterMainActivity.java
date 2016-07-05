package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.Utils;
import fragments.BoutiqueFragment;
import fragments.CartFragment;
import fragments.CategoryFragment;
import fragments.NewGoodFragment;
import fragments.PersonalCenterFragment;

public class FuliCenterMainActivity extends BaseActivity {
    TextView mTvCartHint;
    RadioButton mRadioNewGood;
    RadioButton mRadioBoutique;
    RadioButton mRadioCategory;
    RadioButton mRadioCart;
    RadioButton mRadioPersonalCenter;
    RadioButton[] mRadios=new RadioButton[5];
    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    PersonalCenterFragment mPersonalCenterFragment;
    CartFragment mCartFragment;
    Fragment[] mfragments = new Fragment[5];


    private int index;
    private int currentTabIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuli_center_main);
        initview();
        initFragment();
        // 添加显示第一个fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mNewGoodFragment)
                .add(R.id.fragment_container, mBoutiqueFragment)
                .hide(mBoutiqueFragment)
                .add(R.id.fragment_container, mCategoryFragment)
                .hide(mCategoryFragment)
                .show(mNewGoodFragment)
                .commit();
        registerCartReceiver();
    }

    private void initFragment() {

        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mPersonalCenterFragment = new PersonalCenterFragment();
        mCartFragment = new CartFragment();
        mfragments[0] = mNewGoodFragment;
        mfragments[1] = mBoutiqueFragment;
        mfragments[2] = mCategoryFragment;
        mfragments[3] = mCartFragment;
        mfragments[4] = mPersonalCenterFragment;
        mTvCartHint.setVisibility(View.GONE);

    }

    private void initview() {
        mRadioNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        mRadioBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        mRadioCategory = (RadioButton) findViewById(R.id.layout_category);
        mRadioCart = (RadioButton) findViewById(R.id.cart);
        mRadioPersonalCenter = (RadioButton) findViewById(R.id.layout_personcenter);
        mTvCartHint = (TextView) findViewById(R.id.tvCarHint);
        mRadios[0]=mRadioNewGood;
        mRadios[1]=mRadioBoutique;
        mRadios[2]=mRadioCategory ;
        mRadios[3]=mRadioCart;
        mRadios[4]=mRadioPersonalCenter;

    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.layout_new_good:
                index = 0;
                break;
            case R.id.layout_boutique:
                index = 1;
                break;
            case R.id.layout_category:
                index = 2;
                break;
            case R.id.cart:
                if (FuLiCenterApplication.getInstance().getUser() != null) {

                    index = 3;
                } else {
                    gotoLogin(I.ACTION_TRPE_CART);
                }
                break;
            case R.id.layout_personcenter:
                if (FuLiCenterApplication.getInstance().getUser() != null) {

                    index = 4;
                } else {
                    gotoLogin(I.ACTION_TRPE_PERSON);
                }
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mfragments[currentTabIndex]);

            if (!mfragments[index].isAdded()) {
                trx.add(R.id.fragment_container, mfragments[index]);
            }
            trx.show(mfragments[index]).commit();
            setRadioChecked(index);
            currentTabIndex = index;

        }
    }

    private void gotoLogin(String action) {
        Log.e("login","action="+action);
        startActivity(new Intent(this, LoginActivity.class).putExtra("action", action));
        Log.e("login","action over");
    }

    private void setRadioChecked(int index) {
        for(int i=0;i<mRadios.length;i++) {
            if (i == index) {
                mRadios[i].setChecked(true);

            }else {
                mRadios[i].setChecked(false);

            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setRadioChecked(index);
        String action = getIntent().getStringExtra("action");
        Log.e("main", "currentTabIndex=" + currentTabIndex);
        Log.e("main", "action=" + action);
        Log.e("main", "FuLiCenterApplication.getInstance().getUser()=" + FuLiCenterApplication.getInstance().getUser());
        if (action != null && FuLiCenterApplication.getInstance().getUser() != null) {
            if (action.equals(I.ACTION_TRPE_PERSON)) {
                index = 4;
            }
            if (action.equals(I.ACTION_TRPE_CART)) {
                index=3;
            }
        }
            else {
                setRadioChecked(index);

            }
            if (currentTabIndex == 4 &&  FuLiCenterApplication.getInstance().getUser() == null) {
                index = 0;
            }
            Log.e("main", "index=" + index);

            if (currentTabIndex != index) {
                FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                trx.hide(mfragments[currentTabIndex]);

                if (!mfragments[index].isAdded()) {
                    trx.add(R.id.fragment_container, mfragments[index]);
                }
                trx.show(mfragments[index]).commit();
                setRadioChecked(index);
                currentTabIndex = index;
            }
        }

    class UpdateCartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Utils.sumCartCount();
            if (count > 0) {
                mTvCartHint.setVisibility(View.VISIBLE);
                mTvCartHint.setText(""+count);
            } else {
                mTvCartHint.setVisibility(View.GONE);

            }
            if (FuLiCenterApplication.getInstance().getUser() == null) {
                mTvCartHint.setText("0");
                mTvCartHint.setVisibility(View.GONE);
            }
        }
    }

    UpdateCartReceiver mReceiver;
    private void registerCartReceiver() {
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        filter.addAction("update_user");
        filter.addAction("update_cart");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}

