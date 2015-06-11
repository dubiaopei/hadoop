package cmd;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCountApp extends Configured implements Tool{
	private static String INPUT_PATH = null;
	private static String OUT_PATH = null;

	@Override
	public int run(String[] args) throws Exception {
		INPUT_PATH = args[0];
		OUT_PATH = args[1];
		
		Configuration conf = getConf();
		final FileSystem filesystem = FileSystem.get(new URI(OUT_PATH), conf);
		filesystem.delete(new Path(OUT_PATH), true);
		
		final Job job = new Job(conf , WordCountApp.class.getSimpleName());
		job.setJarByClass(WordCountApp.class);
		
		FileInputFormat.setInputPaths(job, INPUT_PATH);
		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		
		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		FileOutputFormat.setOutputPath(job, new Path(OUT_PATH));
		
		job.waitForCompletion(true);
		return 0;
	}
	
	public static void main(String[] args) throws Exception{
		ToolRunner.run(new Configuration(), new WordCountApp(), args);
	}
	
	public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
		protected void map(LongWritable key, Text value, org.apache.hadoop.mapreduce.Mapper<LongWritable,Text,Text,LongWritable>.Context context) throws java.io.IOException ,InterruptedException {
			final String line = value.toString();
			final String[] splited = line.split("\t");
			
			for (String word : splited) {
				context.write(new Text(word), new LongWritable(1));
			}
		};
	}
	

	
	
	
	public static class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable>{

		protected void reduce(Text key, java.lang.Iterable<LongWritable> values, org.apache.hadoop.mapreduce.Reducer<Text,LongWritable,Text,LongWritable>.Context context) throws java.io.IOException ,InterruptedException {
			long count = 0L;
			for (LongWritable times : values) {
				count += times.get();
			}
			context.write(key, new LongWritable(count));
		};
	}
}
