package netty.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer
{
	public void run() throws Exception
	{
		NioEventLoopGroup parentGroup = new NioEventLoopGroup();
		NioEventLoopGroup childGroup = new NioEventLoopGroup();
		
		try
		{
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			
			serverBootstrap.group(parentGroup, childGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				protected void initChannel(SocketChannel ch) throws Exception
				{
					ch.pipeline().addLast("http-codec", new HttpServerCodec());
					ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
					ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
					
					ch.pipeline().addLast("webSocketServerHandler", new WebSocketServerHandler());
				}
			});
			
			ChannelFuture f = serverBootstrap.bind(8088).sync();
			f.channel().closeFuture().sync();
		}
		finally
		{
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		new WebSocketServer().run();
	}
}
