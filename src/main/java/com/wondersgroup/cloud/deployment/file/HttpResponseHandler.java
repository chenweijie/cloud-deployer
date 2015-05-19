package com.wondersgroup.cloud.deployment.file;

import java.io.File;
import java.io.FileOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.wondersgroup.cloud.deployment.DeployCommand;
import com.wondersgroup.cloud.deployment.Node;

@ChannelPipelineCoverage("one")
public class HttpResponseHandler extends SimpleChannelUpstreamHandler {
	private volatile boolean readingChunks;
	private File downloadFile;
	private FileOutputStream fOutputStream = null;
	private Node node;
	private String appId;

	public HttpResponseHandler(Node node, String appId) {
		this.node = node;
		this.appId = appId;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (e.getMessage() instanceof HttpResponse) {
			DefaultHttpResponse httpResponse = (DefaultHttpResponse) e
					.getMessage();
			String fileName = httpResponse.getHeader("Content-Disposition")
					.substring(20);
			// downloadFile = new File(System.getProperty("user.dir")+
			// File.separator + "download" + fileName);
			downloadFile = new File("E:\\tmp" + File.separator + "download"
					+ fileName);
			readingChunks = httpResponse.isChunked();
		} else {
			HttpChunk httpChunk = (HttpChunk) e.getMessage();
			if (!httpChunk.isLast()) {
				ChannelBuffer buffer = httpChunk.getContent();
				if (fOutputStream == null) {
					fOutputStream = new FileOutputStream(downloadFile);
				}
				while (buffer.readable()) {
					byte[] dst = new byte[buffer.readableBytes()];
					buffer.readBytes(dst);
					fOutputStream.write(dst);
				}
			} else {
				readingChunks = false;
				if (node != null) {
					node.executeCommand(new DeployCommand(appId, Node.TRANSPORT
							| Node.SUCCESS));
				}
			}
			fOutputStream.flush();
		}
		if (!readingChunks) {
			fOutputStream.close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		System.out.println(e.getCause());
		if (node != null) {
			node.executeCommand(new DeployCommand(appId, Node.TRANSPORT
					| Node.FAILURE));
		}
	}
}
