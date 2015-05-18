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
		// ƴ�ӵ����� this.getDoingIPs();
		return super.getKey() + "," + this.getAppId() + ",[\"���д��IPs\"]";
	}

	@Override
	public String[] getDoingIPs() {
		// ͨ�������ȡ��appId ע��Ķ�Ӧ������IP�б� ��Ϊ���ݷ�����ȥ
		String[] ips = new String[]{"���д��IPs"};
		return ips;
	}

}
