package netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter
{
	/* ------ 正常接收(不考虑粘包/拆包) begin ------ */
	
//	private final ByteBuf firstMessage;
//	
//	public TimeClientHandler()
//	{
//		byte[] req = "QUERY TIME ORDER".getBytes();
//		firstMessage = Unpooled.buffer(req.length);
//		firstMessage.writeBytes(req);
//	}
//	
//	/**
//	 * 客户端和服务端TCP链路建立成功之后,NIO线程会调用此方法
//	 */
//	@Override
//	public void channelActive(ChannelHandlerContext ctx) throws Exception
//	{
//		// 发送消息给服务端
//		ctx.writeAndFlush(firstMessage);
//	}
//	
//	@Override
//	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
//	{
//		ByteBuf buf = (ByteBuf) msg;
//		byte[] req = new byte[buf.readableBytes()];
//		buf.readBytes(req);
//		String body = new String(req, "UTF-8");
//		System.out.println("Now is: " + body);
//	}
	
	/* ------ 正常接收(不考虑粘包/拆包) end ------ */
	
	/* ------ 粘包/拆包测试 begin ------ */
	
	private int counter;
	private byte[] req;
	
	public TimeClientHandler()
	{
		// System.getProperty("line.separator") 换行符
		req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
	}
	
	/**
	 * 客户端和服务端TCP链路建立成功之后,NIO线程会调用此方法
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		ByteBuf message = null;
		
		for(int i = 0; i < 100; i++)
		{
			message = Unpooled.buffer(req.length);
			message.writeBytes(req);
			ctx.writeAndFlush(message);
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
//		ByteBuf buf = (ByteBuf) msg;
//		byte[] req = new byte[buf.readableBytes()];
//		buf.readBytes(req);
//		String body = new String(req, "UTF-8");
//		System.out.println("Now is: " + body + ", the counter is: " + ++counter);
		
		// 新增解码器后LineBasedFrameDecoder&StringDecoder后可直接获取body,不再需要对请求消息进行编码
		String body = (String) msg;
		System.out.println("Now is: " + body + ", the counter is: " + ++counter);
	}
	
	/* ------ 粘包/拆包测试 end ------ */
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		System.out.println(cause.getMessage());
		
		// 释放资源
		ctx.close();
	}
}
