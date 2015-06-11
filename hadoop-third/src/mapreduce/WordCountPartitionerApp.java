package mapreduce;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountPartitionerApp {

	public static class WordCountMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
		/**
		 * 每一行执行一次map函数
		 * @param key 表示字节在源文件中偏移量
		 * @param value 行文本内容
		 */
		
		protected void map(LongWritable key, Text value, org.apache.hadoop.mapreduce.Mapper<LongWritable,Text,Text,LongWritable>.Context context) throws java.io.IOException ,InterruptedException {
			final String[] splited = value.toString().split("\\s");
			for (String word : splited) {
				context.write(new Text(word), new LongWritable(1));
			}
		};
	}
	
	//产生输出：<hello,1><you,1><hello,1><me,1>
	//按照key进行排序：<hello,1><hello,1><me,1><you,1>
	
	//分组：<hello,{1,1}><me,{1}><you,{1}>【把相同key的value放到一起】
	public static class WordCountReducer extends Reducer<Text, LongWritable, Text, LongWritable>{
		/**
		 * 每个组调用一次reduce函数
		 * @param word 表示单词
		 * @param times 表示相同key的value的迭代器
		 */
		protected void reduce(Text word, java.lang.Iterable<LongWritable> times, org.apache.hadoop.mapreduce.Reducer<Text,LongWritable,Text,LongWritable>.Context context) throws java.io.IOException ,InterruptedException {
			long sum = 0L;
			for (LongWritable longWritable : times) {
				sum += longWritable.get();
			}
			
			context.write(word, new LongWritable(sum));
		};
	}
	
	
	public static void main(String[] args) throws Exception {
		final Configuration conf = new Configuration();
		final Job job = new Job(conf);
		job.setJobName(WordCountPartitionerApp.class.getSimpleName());
		job.setJarByClass(WordCountPartitionerApp.class);
		
		job.setMapperClass(WordCountMapper.class);
		
		//默认reduce任务数是1个
		job.setNumReduceTasks(2);
		//需要指定map产生的数据按照什么规则分配到不同的reduce任务中
		job.setPartitionerClass(MyPartitioner.class);
		
		job.setReducerClass(WordCountReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		
		FileInputFormat.addInputPaths(job, "/hello");
		FileOutputFormat.setOutputPath(job, new Path("/out1"));

		job.waitForCompletion(true);
	}
	
	
	public static class MyPartitioner extends Partitioner<Text, LongWritable>{

		@Override
		public int getPartition(Text key, LongWritable value, int numReduceTasks) {
			final String word = key.toString();
			if("hello".equals(word)) {
				return 0;
			}else {
				return 1;
			}
		}
		
	}
}

