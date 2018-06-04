package netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer
{
	private void bind(int port) throws Exception
	{
		NioEventLoopGroup parentGroup = new NioEventLoopGroup();
		NioEventLoopGroup childGroup = new NioEventLoopGroup();
		
		try
		{
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			
			serverBootstrap.group(parentGroup, childGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>()
				{
					@Override
					protected void initChannel(SocketChannel ch) throws Exception
					{
						// 以"$_"做为分隔符拆包
//						ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//						ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
						
						// 以指定长度拆包
						ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
						
						ch.pipeline().addLast(new StringDecoder());
						ch.pipeline().addLast(new EchoServelHandler());
					}
				});
			
			ChannelFuture f = serverBootstrap.bind(port).sync();
			
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
		int port = 8088;
		new EchoServer().bind(port);
	}
}
