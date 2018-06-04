package netty.protobuf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class SubReqClient
{
	public void connect() throws Exception
	{
		NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
		
		try
		{
			Bootstrap bootstrap = new Bootstrap();
			
			bootstrap.group(nioEventLoopGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
			.handler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				protected void initChannel(SocketChannel ch) throws Exception
				{
					
					ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
					ch.pipeline().addLast(new ProtobufDecoder(SubscribeRespProto.SubscribeResp.getDefaultInstance()));
					
					ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
					ch.pipeline().addLast(new ProtobufEncoder());
					
					ch.pipeline().addLast(new SubReqClientHandler());
				}
			});
			
			ChannelFuture f = bootstrap.connect("127.0.0.1", 8088).sync();
			f.channel().closeFuture().sync();
		}
		finally
		{
			nioEventLoopGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		new SubReqClient().connect();
	}
}
