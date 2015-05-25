package com.wondersgroup.cloud.deployment.file;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.wondersgroup.cloud.deployment.AbstractReceiveHandler;
import com.wondersgroup.cloud.deployment.DeployCommand;
import com.wondersgroup.cloud.deployment.IReceiveHandler;
import com.wondersgroup.cloud.deployment.Node;

public class TransportReceiveHandler extends AbstractReceiveHandler implements
		IReceiveHandler {
	private Log logger = LogFactory.getLog(TransportReceiveHandler.class);
	private int PORT;

	public TransportReceiveHandler(Node node) {
		super(node);
		try {
			Properties props = new Properties();
			props.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("node.properties"));
			PORT = Integer.parseInt(props
					.getProperty("network.fileserver.port"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handle(String msg, String srcIp) {
		String[] datas = DeployCommand.toData(msg);
		String appId = datas[0];
		String srcPath = datas[1];
		String ipList = datas[2];
		// TODO 从IPList列表中找到是否是本地IP
		// 如果是的话 在本地执行 execute相关指令
		// TODO 后面整理成全局的
		HttpClient httpClient = new HttpClient(node, appId, srcIp, PORT, srcPath, ipList);
		ChannelFuture connectFuture = httpClient.connect();
		if (httpClient.checkFutureState(connectFuture)) {
			System.out.println("connect ok");
			HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
					HttpMethod.GET, "thunder.zip");
			request.addHeader("srcPath", srcPath);
			request.addHeader("ipList", ipList);
			ChannelFuture writeFuture = httpClient.write(request);
			if (httpClient.checkFutureState(writeFuture)) {
				System.out.println("write ok");
//				writeFuture.getChannel().close();
//				httpClient.close();
			}
		}
	}

}
