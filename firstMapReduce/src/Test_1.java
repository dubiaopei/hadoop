import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class Test_1 extends Configured implements Tool{
    /** 计数器 **/	
   enum Counter {
	   LINESKIP, //出错的行
   }
   
   /** map任务 **/	
   public static class Map extends Mapper<LongWritable,Text,NullWritable,Text>{
	   
	   public void map(LongWritable key,Text value,Context context) throws IOException, InterruptedException{
		   String line = value.toString();
		   try{
			   String[] lineSplit = line.split(" ");
			   String month = lineSplit[0];
			   String time = lineSplit[1];
			   String mac = lineSplit[6];
			   Text out = new Text(month + ' '+ time +' '+mac);
			   
			   context.write(NullWritable.get(), out);
		   }catch(java.lang.ArrayIndexOutOfBoundsException e){
			   context.getCounter(Counter.LINESKIP).increment(1);
			   return ;
		   }
	   }
   }

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		
		Job job = new Job(conf,"Test_1");
		job.setJarByClass(Test_1.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(Map.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.waitForCompletion(true);
		
		System.out.println("任务名称:" + job.getJobName());
		System.out.println("任务成功:" + (job.isSuccessful()? "是":"否") );
		System.out.println("输入行数:" + job.getCounters().findCounter("org.apache.hadoop.mapred.Task$Counter","MAP_INPUT_RECORDS").getValue());
		System.out.println("输出行数:" + job.getCounters().findCounter("org.apache.hadoop.mapred.Task$Counter","MAP_OUTPUT_RECORDS").getValue());
		System.out.println("跳过的行:" + job.getCounters().findCounter(Counter.LINESKIP).getValue());
		
		return job.isSuccessful() ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new Test_1(), args);
		System.exit(res);
	}
}
