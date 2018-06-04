package netty.custom;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

/**
 * 心跳请求
 */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter
{
	private volatile ScheduledFuture<?> heartBeat;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		NettyMessage message = (NettyMessage) msg;
		
		if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP)
		{
			// 握手成功，主动发送心跳消息， 启动无限循环定时器用于定期发送心跳消息
			ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx)
					, 0, 5000, TimeUnit.MILLISECONDS);
		}
		else if(message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP)
		{
			// 服务端心跳应答消息
			System.out.println("Client receive server heart beat message: " + message);
		}
		else
		{
			ctx.fireChannelRead(msg);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		if(heartBeat != null)
		{
			heartBeat.cancel(true);
			heartBeat = null;
		}
		
		ctx.fireExceptionCaught(cause);
	}
	
	private class HeartBeatTask implements Runnable
	{
		private final ChannelHandlerContext ctx;
		
		public HeartBeatTask(final ChannelHandlerContext ctx)
		{
			this.ctx = ctx;
		}

		@Override
		public void run()
		{
			NettyMessage heatBest = buildHeatBeat();
			System.out.println("Client send heart beat message to server: " + heatBest);
			ctx.writeAndFlush(heatBest);
		}
		
		private NettyMessage buildHeatBeat()
		{
			NettyMessage message = new NettyMessage();
			Header header = new Header();
			header.setType(MessageType.HEARTBEAT_REQ);
			
			// 附件
			Map<String, Object> attachment = new HashMap<String, Object>(2);
			attachment.put("time", new Date());
			attachment.put("auth", "007");
			header.setAttachment(attachment);
			
			message.setHeader(header);
			return message;
		}
	}
}