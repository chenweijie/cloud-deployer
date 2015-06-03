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
	// 本地模拟原创调用使用到的参数
	private String srcIp;

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

	// appId, nextState, srcPath, ipList
	public DeployCommand(String appId, int key, String srcPath,
			String appServers) {
		super(key);
		this.appId = appId;
		this.srcPath = srcPath;
		this.appServers = appServers;
	}

	// appId, nextState, srcPath, ipList
	public DeployCommand(String appId, int key, String srcPath,
			String appServers, String srcIp) {
		this(appId, key, srcPath, appServers);
		this.srcIp = srcIp;
	}

	public String getAppId() {
		return appId;
	}

	public String getSrcIp() {
		return srcIp;
	}

	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}

	@Override
	public String toString() {
		logger.info("deploy command debug:::" + this.getKey());
		logger.info("deploy command debug:::" + this.getAppId());
		logger.info("deploy command debug:::" + this.srcPath);
		logger.info("deploy command debug:::" + this.appServers);
		return this.getKey() + "," + this.getAppId() + ":" + this.srcPath + ":"
				+ this.appServers;
	}

	@Override
	public String[] getDoingIPs() {
		// 通过服务获取到appId 注册的对应服务器IP列表 作为内容发布出去
		// ["10.1.xss", "10.23.asf.xxx", "10.23.asf.xxx"]
		JSONArray jsonArray = JSONArray.fromObject(this.appServers);
		String[] ips = (String[]) jsonArray
				.toArray(new String[jsonArray.size()]);
		return ips;
	}

	public static String[] toData(String msg) {
		// 过滤掉key 也就是appId
		//
		String content = msg.substring(msg.indexOf(",") + 1);
		String[] args = content.split(":");
		String appId = args[0];
		String srcPath = args[1];
		String ipList = args[2];
		return new String[] { appId, srcPath, ipList };
	}

	public static void main(String[] args) {
		System.out.println(Node.compareOf(Node.DELETE, Node.DEPLOY) >= 0);
		System.out.println(Node.compareOf(Node.DEPLOY, Node.DEPLOY) >= 0);
		System.out.println(Node.compareOf(Node.INIT, Node.DEPLOY) < 0);
	}
}
