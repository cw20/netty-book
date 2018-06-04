package netty.time;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 对网络事件进行读写操作
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter
{
	private int counter;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
//		ByteBuf buf = (ByteBuf) msg;
//		// readableBytes()获取缓冲区可读字节数
//		byte[] req = new byte[buf.readableBytes()];
//		buf.readBytes(req);
//		
//		// 正常接收(不考虑粘包/拆包)
////		String body = new String(req, "UTF-8");
////		System.out.println("The time server receive order: " + body);
////		String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
//		
//		// 粘包/拆包测试
//		String body = new String(req, "UTF-8").substring(0, req.length - System.getProperty("line.separator").length());
//		System.out.println("The time server receive order: " + body + ", the counter is: " + ++counter);
//		String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
//		currentTime = currentTime + System.getProperty("line.separator");
//		
//		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
//		// 异步发送消息,不直接写入SocketChannel,只是把待发送的消息放到发送缓冲数组中
//		ctx.write(resp);
		
		// 新增解码器后LineBasedFrameDecoder&StringDecoder后可直接获取body,不再需要对请求消息进行编码
		String body = (String) msg;
		System.out.println("The time server receive order: " + body + ", the counter is: " + ++counter);
		String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
		currentTime = currentTime + System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.write(resp);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
	{
		// 将消息发送队列中的消息写入到SocketChannel中发送给对方
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		ctx.close();
	}
}
