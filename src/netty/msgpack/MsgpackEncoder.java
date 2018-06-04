package netty.msgpack;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object>
{
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception
	{
		MessagePack messagePack = new MessagePack();
		
		// Serializable
		byte[] raw = messagePack.write(msg);
		out.writeBytes(raw);
	}
}
