import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CountingIndex {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {

		// private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			InputSplit inputSplit = context.getInputSplit();
			String fileName = ((FileSplit) inputSplit).getPath().getName();

			// toLower and replace anything besides a-z with a space
			String newValue = value.toString().toLowerCase().replaceAll("[^a-z]", " ");

			StringTokenizer itr = new StringTokenizer(newValue);
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				// context.write(word, one);
				context.write(word, new Text(fileName));
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
		// private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			Map<String, Integer> files = new HashMap<String, Integer>();
			for (Text val : values) {
				if (files.containsKey(val.toString())) {
					files.put(val.toString(), files.get(val.toString()) + 1);
				} else {
					files.put(val.toString(), 1);
				}
			}
			ArrayList<outString> outputList = new ArrayList<outString>();
			for (String file : files.keySet()) {
				outString output = new outString(file, files.get(file));
				outputList.add(output);
			}
			Collections.sort(outputList);
			String returnString="\n";
			for(outString outFormatted: outputList) {
				returnString+=outFormatted.toString();
				returnString+="\n";
			}
			context.write(key, new Text(returnString));

		}

	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(CountingIndex.class);
		job.setMapperClass(TokenizerMapper.class);
		//job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}