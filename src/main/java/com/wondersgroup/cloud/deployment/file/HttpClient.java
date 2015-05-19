package com.wondersgroup.cloud.deployment.file;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.wondersgroup.cloud.deployment.Node;

public class HttpClient {

	private ClientBootstrap bootstrap;
	private String host = "localhost";
	private Channel channel;
	private boolean futureSuccess;
	private int port = 8080;
	private Node node;
	private String appId;

	public HttpClient(Node node, String appId, String srcIp, int port) {
		this.node = node;
		this.appId = appId;
		this.host = srcIp;
		this.port = port;
	}

	public ChannelFuture connect() {
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		HttpResponseHandler clientHandler = new HttpResponseHandler(node, this.appId);
		bootstrap.setPipelineFactory(new HttpClientPipelineFactory(
				clientHandler));

		//bootstrap.setOption("tcpNoDelay", true);
		//bootstrap.setOption("keepAlive", true);

		return bootstrap.connect(new InetSocketAddress(host, port));
	}

	public boolean checkFutureState(ChannelFuture channelFuture) {
		// Wait until the connection attempt succeeds or fails.
		channel = channelFuture.awaitUninterruptibly().getChannel();
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture connectFuture)
					throws Exception {
				if (!connectFuture.isSuccess()) {
					connectFuture.getCause().printStackTrace();
					// connectFuture.getChannel().close();
					// bootstrap.releaseExternalResources();
					futureSuccess = false;
				} else {
					futureSuccess = true;
				}
			}
		});
		return futureSuccess;
	}

	public ChannelFuture write(HttpRequest request) {
		return channel.write(request);
	}

	public void close() {
		// Close the connection. Make sure the close operation ends because
		// all I/O operations are asynchronous in Netty.
		
		// Wait until the connection is closed or the connection attempt fails.
		channel.getCloseFuture().awaitUninterruptibly();
		// Shut down all thread pools to exit.
		bootstrap.releaseExternalResources();
	}
}