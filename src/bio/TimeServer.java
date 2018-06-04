package bio;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * 时间服务器
 */
public class TimeServer
{
	public static void main(String[] args) throws Exception
	{
		int port = 8088;
		ServerSocket server = null;
		
		try
		{
			server = new ServerSocket(port);
			System.out.println("The time server is start in port: " + port);
			
			Socket socket = null;
			// 引入IO线程池(原bio只能一对一的优化)
			TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);
			
			for(;;)
			{
				socket = server.accept();
				
				// 原bio一对一
//				new Thread(new TimeServerHandler(socket)).start();
				
				// 优化有一对多
				singleExecutor.execute(new TimeServerHandler(socket));
			}
		}
		finally
		{
			if(server != null)
			{
				System.out.println("The time server close");
				server.close();
				server = null;
			}
		}
	}
}
