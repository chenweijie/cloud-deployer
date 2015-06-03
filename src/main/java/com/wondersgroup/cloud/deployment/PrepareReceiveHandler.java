package com.wondersgroup.cloud.deployment;

import java.io.File;
import java.util.Iterator;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondersgroup.cloud.deployment.utils.Pinger;

public class PrepareReceiveHandler extends AbstractReceiveHandler {

	private Log logger = LogFactory.getLog(PrepareReceiveHandler.class);

	public PrepareReceiveHandler(Node node) {
		super(node);
	}

	@Override
	public void handle(String msg, String srcIp) {
		logger.info("enter..prepare stage.");
		String[] datas = DeployCommand.toData(msg);
		String appId = datas[0];
		String srcPath = datas[1];
		String ipList = datas[2];
		// 查看文件夹是不是存在
		File srcFile = new File(srcPath);
		boolean result = srcFile.exists();
		JSONArray jsonArray = JSONArray.fromObject(ipList);
		Iterator iter = jsonArray.iterator();
		while (iter.hasNext()) {
			String serverIp = String.valueOf(iter.next());
			Pinger p = new Pinger(serverIp, 1, 5000);
			result = p.isReachable();
			
			if (result) {
				node.executeCommand(new DeployCommand(appId, Node.PREPARE
						| Node.SUCCESS, srcPath, ipList, serverIp));
			} else {
				node.executeCommand(new DeployCommand(appId, Node.PREPARE
						| Node.FAILURE, srcPath, ipList, serverIp));
			}
		}
		logger.info("ping result::::" + result);
	}

}
