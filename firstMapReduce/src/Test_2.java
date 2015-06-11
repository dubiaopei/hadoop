import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class Test_2 extends Configured implements Tool{
    /** ������ **/	
   enum Counter {
	   LINESKIP, //�������
   }
   
   /** map���� **/	
   public static class Map extends Mapper<LongWritable,Text,Text,Text>{
	   
	   public void map(LongWritable key,Text value,Context context) throws IOException, InterruptedException{
		   String line = value.toString();
		   try{
			  String[] lineSplit = line.split(" ");
			  String anum = lineSplit[0];
			  String bnum = lineSplit[1];
			   
			  context.write(new Text(bnum), new Text(anum));
		   }catch(java.lang.ArrayIndexOutOfBoundsException e){
			   context.getCounter(Counter.LINESKIP).increment(1);
			   return ;
		   }
	   }
   }
   
   /** REDUCE����*/
   public static class Reduce extends Reducer<Text, Text, Text, Text> {
        
       public void reduce(Text key, Iterable<Text> values, Context context)
               throws IOException, InterruptedException {
           String valueString;
           String out = "";

           for (Text value : values) {
               valueString = value.toString();
               out += valueString + "|";
           }
            
           context.write(key, new Text(out));
       }
   }
   

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		
		Job job = new Job(conf,"Test_2");
		job.setJarByClass(Test_2.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(Map.class);       //��������Map����ΪMap�������
		job.setReducerClass(Reduce.class);   //��������Reduce����ΪReduce�������
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(Text.class);   //ָ�����key�ĸ�ʽ
		job.setOutputValueClass(Text.class); //ָ�����value�ĸ�ʽ
		
		job.waitForCompletion(true);
		
		System.out.println("��������:" + job.getJobName());
		System.out.println("����ɹ�:" + (job.isSuccessful()? "��":"��") );
		System.out.println("��������:" + job.getCounters().findCounter("org.apache.hadoop.mapred.Task$Counter","MAP_INPUT_RECORDS").getValue());
		System.out.println("�������:" + job.getCounters().findCounter("org.apache.hadoop.mapred.Task$Counter","MAP_OUTPUT_RECORDS").getValue());
		System.out.println("��������:" + job.getCounters().findCounter(Counter.LINESKIP).getValue());
		
		return job.isSuccessful() ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new Test_2(), args);
		System.exit(res);
	}
}
