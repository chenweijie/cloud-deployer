package com.wondersgroup.cloud.deployment.file;

import static org.jboss.netty.channel.Channels.pipeline;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

public class HttpClientPipelineFactory implements ChannelPipelineFactory {
	private final HttpResponseHandler handler;

	public HttpClientPipelineFactory(HttpResponseHandler handler) {
		this.handler = handler;
	}

	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();

		pipeline.addLast("decoder", new HttpResponseDecoder());
		// pipeline.addLast("aggregator", new HttpChunkAggregator(6048576));
		pipeline.addLast("encoder", new HttpRequestEncoder());
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
		pipeline.addLast("handler", handler);

		return pipeline;
	}
}
