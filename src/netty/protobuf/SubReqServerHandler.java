package netty.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.protobuf.SubscribeReqProto.SubscribeReq;

public class SubReqServerHandler extends ChannelInboundHandlerAdapter
{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		SubscribeReqProto.SubscribeReq req = (SubscribeReq) msg;
		
		if("ZhanSan".equalsIgnoreCase(req.getUserName()))
		{
			System.out.println("service accept client subscribe req: [" + req.toString() + "]");
			ctx.writeAndFlush(resp(req.getSubReqID()));
		}
	}
	
	private SubscribeRespProto.SubscribeResp resp(int subReqID)
	{
		SubscribeRespProto.SubscribeResp.Builder buider = SubscribeRespProto.SubscribeResp.newBuilder();
		buider.setSubReqID(subReqID);
		buider.setRespCode(200);
		buider.setDesc("netty protobuf success, one days later, sent to the designated address");
		
		return buider.build();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		cause.printStackTrace();
		ctx.close();
	}
}
