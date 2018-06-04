package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO时间服务器多路转换器, Reactor(反应堆)线程
 */
public class MultiplexerTimeServer implements Runnable
{
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private volatile boolean stop;
	
	public MultiplexerTimeServer(int port)
	{
		try
		{
			// 创建多路复用器
			selector = Selector.open();
			
			// 打开ServerSocketChannel,用于监听客户端的链接,它是所有客户端连接的父管道
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
			// 设置为非阻塞模式
			serverSocketChannel.configureBlocking(false);
			// 将ServerSocketChannel注册到Reactor线程的多路复用器上
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			System.out.println("The time server is start in port: " + port);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void stop()
	{
		this.stop = true;
	}

	@Override
	public void run()
	{
		while(!stop)
		{
			try
			{
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;
				while(it.hasNext())
				{
					key = it.next();
					it.remove();
					
					try
					{
						handleInput(key);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						if(key != null)
						{
							key.channel();
							if(key.channel() != null)
							{
								key.channel().close();
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		// 多路复用器关闭后,所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭,所以不需要重复释放资源
		if(selector != null)
		{
			try
			{
				selector.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void handleInput(SelectionKey key) throws IOException
	{
		if(key.isValid())
		{
			if(key.isAcceptable())
			{
				// 处理新接入的请求消息
				ServerSocketChannel channel = (ServerSocketChannel) key.channel();
				SocketChannel socketChannel = channel.accept();
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_READ);
			}
			
			if(key.isReadable())
			{
				// 处理客户端消息
				SocketChannel socketChannel = (SocketChannel) key.channel();
				
				// 创建1M大小的缓冲区
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				// 读取客户端消息至缓冲区
				int readBytes = socketChannel.read(readBuffer);
				if(readBytes > 0)
				{
					// 读写转换, 将缓冲区当前的limit(能读多少数据)设置为position(当前读到什么位置), position设置为0, 用于后续对缓冲区的读取操作
					readBuffer.flip();
					// 根据缓冲区可读的字节个数创建字节数组
					byte[] bytes = new byte[readBuffer.remaining()];
					// 将缓冲区刻度的字节数组赋值到新创建的字节数组中
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					
					System.out.println("the time server receive order: " + body);
					
					String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? (new Date()).toString() : "BAD ORDER";
					
					doWrite(socketChannel, currentTime);
				}
				else if(readBytes < 0)
				{
					// 对端链路关闭
					key.channel();
					socketChannel.close();
				}
				else
				{
					// 读到0字节,忽略
				}
			}
		}
	}
	
	private void doWrite(SocketChannel socketChannel, String response) throws IOException
	{
		if(response != null && response.trim().length() > 0)
		{
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			// 将字节数复制到缓冲区中
			writeBuffer.put(bytes);
			writeBuffer.flip();
			
			// 发送
			socketChannel.write(writeBuffer);
		}
	}
}
