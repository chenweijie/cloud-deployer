package com.wondersgroup.cloud.deployment;

public abstract class AbstractReceiveHandler implements IReceiveHandler {

	protected Node node;

	public AbstractReceiveHandler(Node node) {
		this.node = node;
	}

}
