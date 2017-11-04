import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.WordCount;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CountingIndex {
	
	public static class CountingIndexMapper extends Mapper<Object, Text, Chapter, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		private Chapter chapterObj = new Chapter();
		private Text chapter = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			
			
			StringTokenizer itr = new StringTokenizer(value.toString().toLowerCase().replaceAll("[^a-z]", " "));
			Text fileName = new Text(((FileSplit) context.getInputSplit()).getPath().getName());
			
			
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				chapter.set(fileName);
				chapterObj = new Chapter(chapter,word);
				context.write(chapterObj, one);
			}
		}
	}
	
	public static class CountingIndexReducer extends Reducer<Chapter, IntWritable, String, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Chapter key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key.toString(), result);
		}
	}

	  public static void main(String[] args) throws Exception {
		    Configuration conf = new Configuration();
		    Job job = Job.getInstance(conf, "Counting Index");
		    job.setJarByClass(WordCount.class);
		    job.setMapperClass(CountingIndexMapper.class);
		    job.setCombinerClass(CountingIndexReducer.class);
		    job.setReducerClass(CountingIndexReducer.class);
		    job.setOutputKeyClass(Chapter.class);
	        job.setOutputValueClass(IntWritable.class);
		    MultipleInputs.addInputPath(job, new Path(args[0]),TextInputFormat.class);
		    FileOutputFormat.setOutputPath(job, new Path(args[1]));
		    System.exit(job.waitForCompletion(true) ? 0 : 1);
		  }

}
