package cn.ucai.fulicenter.bean;

import com.google.gson.annotations.SerializedName;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Created by sks on 2016/6/22.
 */
public class GoodDetailsBean implements Serializable {
    private PropertyBean[] properties;

    /**
     * id : 280
     * goodsId : 7677
     * catId : 291
     * goodsName : 双层分格饭盒 绿色
     * goodsEnglishName : Monbento
     * goodsBrief : PP食品级材质
     * shopPrice : ￥253
     * currencyPrice : ￥293
     * promotePrice : ￥0
     * rankPrice : ￥293
     * goodsThumb : 201509/thumb_img/7677
     * goodsImg : 201509/thumb_img/7677_thum
     * addTime : 1442419200000
     * shareUrl : http7677
     * promote : false
     */

    private int id;
    private int goodsId;
    private int catId;
    private String goodsName;
    private String goodsEnglishName;
    private String goodsBrief;
    private String shopPrice;
    private String currencyPrice;
    private String promotePrice;
    private String rankPrice;
    private String goodsThumb;
    private String goodsImg;
    private long addTime;
    private String shareUrl;
    @SerializedName("promote")
    private boolean isPromote;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsEnglishName() {
        return goodsEnglishName;
    }

    public void setGoodsEnglishName(String goodsEnglishName) {
        this.goodsEnglishName = goodsEnglishName;
    }

    public String getGoodsBrief() {
        return goodsBrief;
    }

    public void setGoodsBrief(String goodsBrief) {
        this.goodsBrief = goodsBrief;
    }

    public String getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(String shopPrice) {
        this.shopPrice = shopPrice;
    }

    public String getCurrencyPrice() {
        return currencyPrice;
    }

    public void setCurrencyPrice(String currencyPrice) {
        this.currencyPrice = currencyPrice;
    }

    public String getPromotePrice() {
        return promotePrice;
    }

    public void setPromotePrice(String promotePrice) {
        this.promotePrice = promotePrice;
    }

    public String getRankPrice() {
        return rankPrice;
    }

    public void setRankPrice(String rankPrice) {
        this.rankPrice = rankPrice;
    }

    public String getGoodsThumb() {
        return goodsThumb;
    }

    public void setGoodsThumb(String goodsThumb) {
        this.goodsThumb = goodsThumb;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    @JsonIgnore
    public boolean isPromote() {
        return isPromote;
    }
    @JsonIgnore
    public void setPromote(boolean promote) {
        isPromote = promote;
    }

    public PropertyBean[] getProperties() {

        return properties;
    }

    public void setProperties(PropertyBean[] properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodDetailsBean that = (GoodDetailsBean) o;

        return goodsId == that.goodsId;


    }

    @Override
    public int hashCode() {
        return goodsId;
    }
}
