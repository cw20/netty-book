package netty.serializable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class Test
{
	public static void main(String[] args) throws IOException
	{
		int loop = 1000000;
		
		User user = new User();
		user.setId(100);
		user.setName("Welcome to Netty");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(user);
		oos.flush();
		oos.close();
		
		byte[] baosByte = baos.toByteArray();
		System.out.println("baosByte: " + baosByte.length);
		System.out.println("baosByte length: " + baosByte.length);
		baos.close();
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		byte[] codeByte = user.codeC(buffer);
		System.out.println("codeByte: " + codeByte);
		System.out.println("codeByte length: " + codeByte.length);
	}
}
