package com.wondersgroup.cloud.deployment;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONArray;

public class DeployCommand extends PlainCommand {

	private static Log logger = LogFactory.getLog(DeployCommand.class);
	private String appId;
	// 源文件路径
	private String srcPath;
	private String appServers;

	public DeployCommand(int key) {
		this("", key, null);
	}

	// close, delete, transport, start, test
	public DeployCommand(String appId, int key, Map extraParams) {
		super(key);
		this.appId = appId;
		if (extraParams != null) {
			this.srcPath = String.valueOf(extraParams.get("publishDir"))
					+ File.separator
					+ String.valueOf(extraParams.get("bsVersion"))
					+ File.separator + String.valueOf(extraParams.get("bsWar"));
			this.appServers = String.valueOf(extraParams.get("appServers"));
		}
	}

	public DeployCommand(String appId, int key, String srcPath,
			String appServers) {
		super(key);
		this.appId = appId;
		this.srcPath = srcPath;
		this.appServers = appServers;
	}

	public String getAppId() {
		return appId;
	}

	@Override
	public String toString() {
		return super.getKey() + "," + this.getAppId() + ":" + this.srcPath
				+ ":" + this.appServers;
	}

	@Override
	public String[] getDoingIPs() {
		// 通过服务获取到appId 注册的对应服务器IP列表 作为内容发布出去
		// ["10.1.xss", "10.23.asf.xxx", "10.23.asf.xxx"]
		JSONArray jsonArray = JSONArray.fromObject(this.appServers);
		String[] ips = (String[]) jsonArray
				.toArray(new String[jsonArray.size()]);
		// String[] ips = new String[] { "随便写的IPs" };
		return ips;
	}

	public static String[] toData(String msg) {
		logger.info(msg);
		// 过滤掉key 也就是appId
		String content = msg.substring(msg.indexOf(","));
		String[] args = content.split(":");
		String appId = args[0];
		String srcPath = args[1];
		String ipList = args[2];
		return new String[] { appId, srcPath, ipList };
	}

}
