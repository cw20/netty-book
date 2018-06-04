package netty.msgpack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer
{
	public void bind() throws Exception
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
					// 处理半包,消息接收时读取发送前增加的2个字节消息长度后进行拆包/粘包
					ch.pipeline().addLast("Frame Decoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
					ch.pipeline().addLast("Msgpack Decoder", new MsgpackDecoder());
					
					// 处理半包,消息发送前增加2个字节的消息长度字段
					ch.pipeline().addLast("Frame Encoder", new LengthFieldPrepender(2));
					ch.pipeline().addLast("Msgpack Encoder", new MsgpackEncoder());
					
					ch.pipeline().addLast(new EchoServerHandler());
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
		new EchoServer().bind();
	}
}
