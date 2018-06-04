package netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception
	{
		if(!request.decoderResult().isSuccess())
		{
			return;
		}
		
		if(request.method() != HttpMethod.GET)
		{
			return;
		}
		
		final String uri = request.uri();
		HttpHeaders headers = request.headers();
		
		sendListing(ctx);
	}
	
	private static void sendListing(ChannelHandlerContext ctx)
	{
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set("Content-Type", "text/html;charset=UTF-8");
		
		StringBuilder builder = new StringBuilder();
		builder.append("<html><body><div>well come to netty http server</div></body></html>");
		
		ByteBuf byteBuf = Unpooled.copiedBuffer(builder, CharsetUtil.UTF_8);
		response.content().writeBytes(byteBuf);
		
		byteBuf.release();
		
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
