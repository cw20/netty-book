package netty.custom;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 心跳响应
 */
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter
{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		NettyMessage message = (NettyMessage) msg;
		
		if(message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQ)
		{
			System.out.println("Receive client heart beat message: " + message);
			
			NettyMessage heartBeat = buildHeatBeat();
			System.out.println("Send heart beat response message to client: " + heartBeat);
			
			ctx.writeAndFlush(heartBeat);
		}
	}
	
	private NettyMessage buildHeatBeat()
	{
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.HEARTBEAT_RESP);
		
		// 附件
		Map<String, Object> attachment = new HashMap<String, Object>(2);
		attachment.put("time", new Date());
		attachment.put("auth", "008");
		header.setAttachment(attachment);
					
		message.setHeader(header);
		return message;
	}
}
