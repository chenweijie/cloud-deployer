package com.wondersgroup.cloud.deployment;

public class DeployCommand extends PlainCommand {

	private String appId;

	// close, delete, transport, start, test
	public DeployCommand(String appId, int key) {
		super(key);
		this.appId = appId;
	}

	public String getAppId() {
		return appId;
	}

	@Override
	public String toString() {
		return super.getKey() + "," + this.getAppId() + ",[\"随便写的IPs\"]";
	}

	@Override
	public String[] getDoingIPs() {
		// 通过服务获取到appId 注册的对应服务器IP列表 作为内容发布出去
		String[] ips = new String[]{"随便写的IPs"};
		return ips;
	}

}
