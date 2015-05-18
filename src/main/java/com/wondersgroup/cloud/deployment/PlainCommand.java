package com.wondersgroup.cloud.deployment;

public class PlainCommand implements ICommand {

	private int key;

	public int getKey() {
		return key;
	}

	public PlainCommand(int key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return String.valueOf(key);
	}

	@Override
	public String getAppId() {
		return "";
	}

	@Override
	public String[] getDoingIPs() {
		return new String[]{};
	}

}
