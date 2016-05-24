package cn.ucai.superwechat.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperweChatApplication;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Contact;
import cn.ucai.superwechat.bean.User;
import cn.ucai.superwechat.data.RequestManager;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.domain.EMUser;

import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

public class UserUtils {
	/**
	 * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
	 * @param username
	 * @return
	 */
	public static EMUser getUserInfo(String username){
		EMUser user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(username);
		if(user == null){
			user = new EMUser(username);
		}

		if(user != null){
			//demo没有这些数据，临时填充
			if(TextUtils.isEmpty(user.getNick()))
				user.setNick(username);
		}
		return user;
	}
	public static Contact getUserBeanInfo(String username) {
		Contact contact = SuperweChatApplication.getInstance().getUserList().get(username);
		return contact;
	}


	/**
	 * 设置用户头像
	 * @param username
	 */
	public static void setUserAvatar(Context context, String username, ImageView imageView){
		EMUser user = getUserInfo(username);
		if(user != null && user.getAvatar() != null){
			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
		}else{
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}


	/***
	 * 设置加载服务器自己的头像
	 * @param username
	 * @param imageView
	 */
	public static void setUserBeanAvatar(String username , NetworkImageView imageView) {
		Contact contact = getUserBeanInfo(username);
		if (contact !=null && contact.getMContactCname()!=null) {
			setUserAvatar(getAvatarPath(username),imageView);

		}

	}

	private static void setUserAvatar(String url,NetworkImageView imageView) {
		Log.e("main", "url=" + url);
		if (url ==null || url.isEmpty())return;
		imageView.setDefaultImageResId(R.drawable.default_avatar);
		imageView.setImageUrl(url, RequestManager.getImageLoader());
		imageView.setErrorImageResId(R.drawable.default_avatar);

	}

	private static String getAvatarPath(String username) {
		if (username==null || username.isEmpty()) return null;
		return I.REQUEST_DOWNLOAD_AVATAR_USER + username;

	}

	/**
	 * 设置当前用户头像
	 */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		EMUser user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}


	public static void setCurrentUserBeanAvatar( NetworkImageView imageView) {
		User user = SuperweChatApplication.getInstance().getUser();
		Log.i("main", "user="+user);
		setUserAvatar(getAvatarPath(user.getMUserName()), imageView);
	}
	/**
	 * 设置远端服务器的当前用户头像
	 */
	public static void setCurrentUserBeanAvatar(String username,TextView textView) {
		Contact userBeanInfo = getUserBeanInfo(username);
		if (userBeanInfo != null) {
			if (userBeanInfo.getMUserNick() != null) {
				textView.setText(userBeanInfo.getMUserNick());
			} else if (userBeanInfo.getMContactCname() != null) {

				textView.setText(userBeanInfo.getMContactCname());
			}
		} else {
			textView.setText(username);
		}
	}

	/**
	 * 设置用户昵称
	 */
	public static void setUserNick(String username,TextView textView){
		EMUser user = getUserInfo(username);
		if(user != null){
			textView.setText(user.getNick());
		}else{
			textView.setText(username);
		}
	}

	/**
	 * 设置显示自己的昵称
	 * @param username
	 * @param textView
	 */
	public static void setUserBeanNick(String username,TextView textView) {
		Contact userBeanInfo = getUserBeanInfo(username);
		if (userBeanInfo != null) {
			if (userBeanInfo.getMUserNick() != null) {

				textView.setText(userBeanInfo.getMUserNick());
			} else if (userBeanInfo.getMContactCname() != null) {

				textView.setText(userBeanInfo.getMContactCname());
			}
		} else {
			textView.setText(username);
		}

	}

	/**
	 * 设置当前用户昵称
	 */
	public static void setCurrentUserNick(TextView textView){
		EMUser user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if(textView != null){
			textView.setText(user.getNick());
		}
	}
	/**
	 * 保存或更新某个用户
	 * @param newUser
	 */
	public static void saveUserInfo(EMUser newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}

}
