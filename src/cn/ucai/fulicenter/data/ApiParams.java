package cn.ucai.fulicenter.data;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;

public class ApiParams extends HashMap<String, String> {
	private static final long serialVersionUID = 8112047472727256876L;

	public ApiParams with(String key, String value) {
		put(key, value);
		return this;
	}

	/**
	 * 将服务端根地址和请求参数集合转换为GET格式的url
	 * @param rootUrl:根地址
	 * @param params：GET请求参数集合
	 * @return
	 * @throws Exception
	 */
	public static String getUrl(String rootUrl, ArrayList<ApiParams> params) throws Exception {
		StringBuilder url = new StringBuilder(rootUrl);
		if (params == null || params.isEmpty()) {
			return rootUrl;
		}
		url.append("?");

		for (ApiParams param : params) {
			url.append(param.getKey())
					.append("=")
					.append(URLEncoder.encode(param.getValue(), "utf-8"))
					.append("&");
		}
		url.deleteCharAt(url.length() - 1);
		return url.toString();
	}

	public String getUrl(String rootUrl) throws Exception {
		StringBuilder url = new StringBuilder(rootUrl);
		if (this == null || this.isEmpty()) {
			return rootUrl;
		}
		url.append("?");
		Set<String> set = this.keySet();
		for(String key:set){
			url.append(key)
				.append("=")
				.append(URLEncoder.encode(this.get(key), "utf-8"))
				.append("&");
		}
		url.deleteCharAt(url.length() - 1);
		return url.toString();
	}

	public String getRequestUrl(String request) throws Exception {
		StringBuilder url = new StringBuilder(FuLiCenterApplication.SERVER_ROOT);
		url.append("?")
				.append(I.KEY_REQUEST)
				.append("=")
				.append(request)
				.append("&");
		if (this == null || this.isEmpty()) {
			url.deleteCharAt(url.length() - 1);
			return url.toString();
		}

		Set<String> set = this.keySet();
		for(String key:set){
			url.append(key)
					.append("=")
					.append(URLEncoder.encode(this.get(key), "utf-8"))
					.append("&");
		}
		url.deleteCharAt(url.length() - 1);
		return url.toString();
	}

	public String getKey() {
		Set<String> set = this.keySet();
		for (String key : set) {
			return key;
		}
		return null;
	}

	public String getValue() {
		Collection<String> values = this.values();
		for (String value : values) {
			return value;
		}
		return null;
	}

}
