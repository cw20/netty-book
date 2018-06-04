package netty.protobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SubReqServer
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
					// 用于半包处理
					ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
					// 解码器,参数是解码的目标类
					ch.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
					// 用于半包处理
					ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
					// 编码器
					ch.pipeline().addLast(new ProtobufEncoder());
					
					ch.pipeline().addLast(new SubReqServerHandler());
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
		new SubReqServer().bind();
	}
}
