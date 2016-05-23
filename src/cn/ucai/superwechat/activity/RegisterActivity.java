/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperweChatApplication;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Message;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.ImageUtils;
import cn.ucai.superwechat.utils.Utils;

import com.easemob.exceptions.EaseMobException;

import java.io.File;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity {
	OnSetAvatarListener mOnSetAvatarListener;
	String avatarName;
	Activity mContext;
	String username;
	String pwd;
	String nick;
	ProgressDialog pd;

	private final  static String TAG=RegisterActivity.class.getName();
	private EditText userNickEditText;
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	ImageView mAvatar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initView();
		setOnListener();

	}

	private void setOnListener() {
		onLoginClickListener();
		onRigisterClickListener();

		onSetAvatarlistener();
	}

	private void onSetAvatarlistener() {
		findViewById(R.id.iv_user_Avtar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnSetAvatarListener = new OnSetAvatarListener(mContext, R.id.Layout_Register, getAvatarName(), I.AVATAR_TYPE_USER_PATH);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_OK) {
			mOnSetAvatarListener.setAvatar(requestCode, data, mAvatar);
		}
	}

	private void onLoginClickListener() {
		findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	private String getAvatarName() {
		avatarName = System.currentTimeMillis()+"";
		return avatarName;
	}
	private void initView() {
		userNameEditText = (EditText) findViewById(R.id.username);
		userNickEditText = (EditText) findViewById(R.id.et_usernick);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.et_comfirmPassword);
		mAvatar = (ImageView) findViewById(R.id.iv_avatar);

	}


	private void onRigisterClickListener() {
		findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				username = userNameEditText.getText().toString().trim();
				 pwd = passwordEditText.getText().toString().trim();
				nick = userNickEditText.getText().toString().trim();
				String confirm_pwd = confirmPwdEditText.getText().toString().trim();
				if (TextUtils.isEmpty(username)) {
					userNameEditText.requestFocus();
					userNameEditText.setError(getResources().getString(R.string.User_name_cannot_be_empty));
					return;
				} else if (!username.matches("[\\w][\\w\\d_]+")) {
					userNameEditText.requestFocus();
					userNameEditText.setError(getResources().getString(R.string.User_name_cannot_be_wd));
					return;
				}else if (TextUtils.isEmpty(nick)) {
					userNickEditText.requestFocus();
					userNickEditText.setError(getResources().getString(R.string.Nick_name_cannot_be_empty));
					return;
				}
				else if (TextUtils.isEmpty(pwd)) {
					passwordEditText.requestFocus();
					passwordEditText.setError(getResources().getString(R.string.Password_cannot_be_empty));
					return;
				}else if (TextUtils.isEmpty(confirm_pwd)) {
					confirmPwdEditText.requestFocus();
					confirmPwdEditText.setError(getResources().getString(R.string.Confirm_password_cannot_be_empty));
					return;
				} else if (!pwd.equals(confirm_pwd)) {

					Toast.makeText(mContext, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
					return;
				}

				if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
					final ProgressDialog pd = new ProgressDialog(mContext);
					pd.setMessage(getResources().getString(R.string.Is_the_registered));
					pd.show();


				}
			}
		});

	}
	private void registerAppServer() {
		File file =new File (ImageUtils.getAvatarPath(mContext,I.AVATAR_TYPE_USER_PATH),
				avatarName+I.AVATAR_SUFFIX_JPG);
		OkHttpUtils<Message> utils = new OkHttpUtils<Message>();
		utils.url(SuperweChatApplication.SERVER_ROOT)
				.addParam(I.KEY_REQUEST, I.REQUEST_REGISTER)
				.addParam(I.User.USER_NAME,username)
				.addParam(I.User.NICK,nick)
				.addParam(I.User.PASSWORD,pwd)
				.targetClass(Message.class)
				.addFile(file)
				.execute(new OkHttpUtils.OnCompleteListener<Message>() {
					@Override
					public void onSuccess(Message result) {
						if (result.isResult()) {
							registerEMServer();

						} else {
							Utils.showToast(mContext, Utils.getResourceString(mContext, result.getMsg()), Toast.LENGTH_SHORT);
							pd.dismiss();


						}
					}

					@Override
					public void onError(String error) {

					}
				});
	}
	private void registerEMServer() {
		new Thread(new Runnable() {
			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance().createAccountOnServer(username, pwd);
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							// 保存用户名
							SuperweChatApplication.getInstance().setUserName(username);
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_LONG).show();
							finish();
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							int errorCode=e.getErrorCode();
							if(errorCode==EMError.NONETWORK_ERROR){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_ALREADY_EXISTS){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.UNAUTHORIZED){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.ILLEGAL_USER_NAME){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}
		}).start();


	}
	public void back(View view) {
		finish();
	}

	public void Login(View view) {
		Intent intent = new Intent();

	}
}
