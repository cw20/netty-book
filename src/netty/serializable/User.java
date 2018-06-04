package netty.serializable;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class User implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String name;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public byte[] codeC(ByteBuffer buffer)
	{
		byte[] value = this.name.getBytes();
		buffer.putInt(value.length);
		buffer.put(value);
		buffer.putInt(this.id);
		buffer.flip();
		
		value = null;
		byte[] result = new byte[buffer.remaining()];
		buffer.get(result);
		return result;
	}
}
