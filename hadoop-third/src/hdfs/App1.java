package hdfs;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

public class App1 {

	public static void main(String[] args) throws Exception {
		//http://www.baidu.com
		final Configuration conf = new Configuration();
		final FileSystem fileSystem = FileSystem.get(new URI("hdfs://crxy1:9000/"), conf);
		System.out.println("**************"+fileSystem.toString());
		System.out.println("**************"+fileSystem.getClass());

		//创建文件夹mkdirs
		//fileSystem.mkdirs(new Path("mydir"));
//		fileSystem.mkdirs(new Path("/dir2"), new FsPermission("111"));
		
		//上传文件create
//		final FSDataOutputStream out = fileSystem.create(new Path("/dir1/file1"), true, 1024000, (short)2, 1048576);
//		final FileInputStream in = new FileInputStream("/root/Downloads/hello");
//		IOUtils.copyBytes(in, out, 1024, true);
		
		
//		final AtomicInteger writeBytes = new AtomicInteger(0);
//		final FSDataOutputStream out = fileSystem.create(new Path("/dir1/file2"), new Progressable() {
//			@Override
//			public void progress() {
//				System.out.println("WriteBytes = "+writeBytes.get());
//			}
//		});
//		
//		final FileInputStream in = new FileInputStream("/root/Downloads/hello");
//		byte[] buffer = new byte[4];
//		int readBytes = in.read(buffer);
//		while(readBytes!=-1) {
//			out.write(buffer);
//			out.flush();
//			out.hsync();
//			writeBytes.addAndGet(readBytes);
//			readBytes = in.read(buffer);
//		}
		
		//读取数据
//		final FSDataInputStream in = fileSystem.open(new Path("/dir1/file1"));
//		IOUtils.copyBytes(in, System.out, 1024, true);

		//遍历
		final FileStatus[] listStatus = fileSystem.listStatus(new Path("/"));
		for (FileStatus fileStatus : listStatus) {
			System.out.println(fileStatus);
			if(!fileStatus.isDirectory()) {
				final BlockLocation[] fileBlockLocations = fileSystem.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
				for (BlockLocation blockLocation : fileBlockLocations) {
					final String[] names = blockLocation.getHosts();
					for (String hostname : names) {
						System.out.println("HOST NAME:"+hostname);
					}
				}
				
			}
		}

		
		//获取工作目录
		//fileSystem.getWorkingDirectory().toString();
		
		
		//删除
//		final Trash trash = new Trash(fileSystem, fileSystem.getConf());
//		trash.moveToTrash(new Path("/dir1/file1"));
//		fileSystem.delete(new Path("/dir1/file1"), true);	
		
		
	}

}
