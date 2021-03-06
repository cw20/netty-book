package netty.socket;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object>
{
	private WebSocketServerHandshaker handshaker;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		// 传统HTTP接入
		if(msg instanceof FullHttpRequest)
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		
		// webSocket接入
		if(msg instanceof WebSocketFrame)
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
	{
		ctx.flush();
	}
	
	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req)
	{
		// 如果HTTP解码失败, 返回HTTP异常
		if(!req.decoderResult().isSuccess() || !("websocket".equals(req.headers().get("Upgrade"))))
		{
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1
					, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		
		// 构造握手响应返回, 本机测试
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8088/websocket", null, false);
		handshaker = wsFactory.newHandshaker(req);
		if(handshaker == null)
		{
			// 不支持webSocket
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		}
		else
		{
			// 握手应答时会动态增加编解码handler
			handshaker.handshake(ctx.channel(), req);
		}
	}
	
	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame)
	{
		// 判断是否关闭链路的指令
		if(frame instanceof CloseWebSocketFrame)
		{
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		
		// 判断是否是ping消息(心跳)
		if(frame instanceof PingWebSocketFrame)
		{
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		
		// 当前仅支持文本消息, 不支持二进制消息
		if(!(frame instanceof TextWebSocketFrame))
		{
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
		}
		
		// 返回应答消息
		String request = ((TextWebSocketFrame) frame).text();
		
		ctx.channel().write(new TextWebSocketFrame(request + ", 欢迎使用 netty websocket服务, 现在时刻: " + new Date().toString()));
	}
	
	private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res)
	{
		// 返回应答给客户端
		if(res.status().code() != 200)
		{
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
			HttpUtil.setContentLength(res, res.content().readableBytes());
		}
		
		// 如果是非keep-alive, 关闭连接
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if(HttpUtil.isKeepAlive(req) || res.status().code() != 200)
			f.addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		cause.printStackTrace();
		ctx.close();
	}
}
