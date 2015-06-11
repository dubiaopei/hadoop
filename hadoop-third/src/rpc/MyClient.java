package rpc;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

public class MyClient {
	public static void main(String[] args) throws IOException {
		final MyProtocol client = RPC.getProxy(MyProtocol.class, 245, new InetSocketAddress("localhost", 3333), new Configuration());
		final String result = client.hello("zhangsan");
		System.out.println(result);
	}
}
