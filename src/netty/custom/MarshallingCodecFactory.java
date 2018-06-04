package netty.custom;

import java.io.IOException;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;

/**
 * MarshallingDecoder工厂类
 */
public final class MarshallingCodecFactory
{
	public static MarshallingDecoder buildMarshallingDecoder()
	{
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		DefaultUnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
		MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024);
		
		return decoder;
	}
	
	public static io.netty.handler.codec.marshalling.MarshallingEncoder buildMarshallingEncoder()
	{
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		DefaultUnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
		io.netty.handler.codec.marshalling.MarshallingEncoder encoder = new io.netty.handler.codec.marshalling.MarshallingEncoder((MarshallerProvider) provider);
		
		return encoder;
	}
	
	public static Marshaller buildMarshaller() throws IOException
	{
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
		
		return marshaller;
	}
	
	public static Unmarshaller buildUnmarshaller() throws IOException
	{
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
		
		return unmarshaller;
	}
}
