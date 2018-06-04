package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandler implements Runnable
{
	private String host;
	private int port;
	private Selector selector = null;
	private SocketChannel socketChannel = null;
	private volatile boolean stop = false;
	
	public TimeClientHandler(String host, int port)
	{
		this.host = host;
		this.port = port;
		
		try
		{
			this.selector = Selector.open();
			this.socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
		}
		catch (IOException e)
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
		try
		{
			doConnect();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			System.exit(1);
		}
		
		while(!stop)
		{
			try
			{
				selector.select(1000);
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
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
							key.cancel();
							
							if(key.channel() != null)
								key.channel().close();
						}
					}
				}
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
				System.exit(1);
			}
		}
		
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
	
	private void handleInput(SelectionKey key) throws IOException
	{
		if(key.isValid())
		{
			SocketChannel sc = (SocketChannel) key.channel();
			if(key.isConnectable())
			{
				// 连接状态
				if(sc.finishConnect())
				{
					// 连接成功
					sc.register(selector, SelectionKey.OP_READ);
					doWrite(sc);
				}
				else
				{
					// 连接失败,进程退出
					System.exit(1);
				}
			}
			
			if(key.isReadable())
			{
				ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(byteBuffer);
				
				if(readBytes > 0)
				{
					byteBuffer.flip();
					byte[] bytes = new byte[byteBuffer.remaining()];
					byteBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					
					System.out.println("The time client receive order: " +  body);
					this.stop = true;
				}
				else if(readBytes < 0)
				{
					key.channel();
					sc.close();
				}
				else
				{
				}
			}
		}
	}
	
	private void doConnect() throws Exception
	{
		if(socketChannel.connect(new InetSocketAddress(host, port)))
		{
			// 如果直接连接成功,则注册读,发送请求消息,读应答
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		}
		else
		{
			// 连接不成功,注册连接,等待服务端返回ack
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}
	
	private void doWrite(SocketChannel sc) throws IOException
	{
		byte[] bytes = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		writeBuffer.put(bytes);
		writeBuffer.flip();
		
		sc.write(writeBuffer);
		if(!writeBuffer.hasRemaining())
			System.out.println("Send order 2 server success");
	}
}
