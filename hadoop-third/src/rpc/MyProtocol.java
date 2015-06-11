package rpc;

import org.apache.hadoop.ipc.VersionedProtocol;

public interface MyProtocol extends VersionedProtocol{
	public static long versionID = 24234L;
	public String hello(String name);
}
