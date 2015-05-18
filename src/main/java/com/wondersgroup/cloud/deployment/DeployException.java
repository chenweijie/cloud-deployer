package com.wondersgroup.cloud.deployment;

import java.io.IOException;

public class DeployException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String msg;

	public DeployException(String msg, IOException ex) {
		this.msg = msg;
	}

	public DeployException(String msg) {
		this.msg = msg;
	}

	@Override
	public String getMessage() {
		return msg;
	}

	@Override
	public String getLocalizedMessage() {
		return msg;
	}

	
	
}
