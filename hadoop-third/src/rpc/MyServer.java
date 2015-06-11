package rpc;

import java.io.IOException;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Server;

public class MyServer implements MyProtocol{

	public static void main(String[] args) throws HadoopIllegalArgumentException, IOException {
		//构造RPC的服务端
		final RPC.Builder builder = new RPC.Builder(new Configuration());
		builder.setProtocol(MyProtocol.class);
		builder.setInstance(new MyServer());
		builder.setBindAddress("localhost");
		builder.setPort(3333);
		
		final Server server = builder.build();
		server.start();
	}
	
	
	
	@Override
	public String hello(String name) {
		return "hello "+name;
	}
	
	@Override
	public ProtocolSignature getProtocolSignature(String arg0, long arg1,
			int arg2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getProtocolVersion(String arg0, long arg1) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}



}
