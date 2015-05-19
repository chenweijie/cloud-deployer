package com.wondersgroup.cloud.deployment.file;

import static org.jboss.netty.channel.Channels.pipeline;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import com.wondersgroup.cloud.deployment.INodeListener;
import com.wondersgroup.cloud.deployment.Node;

public class FileServer implements INodeListener {

	private Node node;
	private int PORT;

	public FileServer(Node node) {
		this.node = node;
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
	public void fireNodeEvent(String msg, String srcIp, Object... params) {
		if (srcIp == node.getIp()
				&& (Node.runStateOf(node.selectKey(msg)) == Node.INIT)) {
			ServerBootstrap bootstrap = new ServerBootstrap(
					new NioServerSocketChannelFactory(
							Executors.newCachedThreadPool(),
							Executors.newCachedThreadPool()));

			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

				@Override
				public ChannelPipeline getPipeline() throws Exception {
					ChannelPipeline pipeline = pipeline();
					pipeline.addLast("decoder", new HttpRequestDecoder());
					pipeline.addLast("aggregator", new HttpChunkAggregator(
							65536));
					pipeline.addLast("encoder", new HttpResponseEncoder());
					pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
					pipeline.addLast("handler", new FileServerHandler());
					return pipeline;
				}

			});

			bootstrap.bind(new InetSocketAddress(PORT));
		}
	}

}
