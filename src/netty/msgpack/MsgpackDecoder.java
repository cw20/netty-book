package netty.msgpack;

import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * 解码器,接收数据时调用
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf>
{
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception
	{
		final int length = msg.readableBytes();
		final byte[] array = new byte[length];
		
		// 获取需要解码的数组
		msg.getBytes(msg.readerIndex(), array, 0, length);
		
		// 反序列化为Object对象
		MessagePack messagePack = new MessagePack();
		out.add(messagePack.read(array));
	}

}
