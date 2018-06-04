package bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 时间服务器客户端
 */
public class TimeClient
{
	public static void main(String[] args)
	{
		int port = 8088;
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		
		try
		{
			socket = new Socket("127.0.0.1", port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println("QUERY TIME ORDER");
			
			System.out.println("Send order 2 server success");
			
			String resp = in.readLine();
			System.out.println(resp);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
			if(out != null)
			{
				try
				{
					out.close();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
			if(socket != null)
			{
				try
				{
					socket.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}
}
