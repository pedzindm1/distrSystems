import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Mapper;

public class PPMapper extends Mapper<Object, Text, String, IntWritable> {

	private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		
		
		StringTokenizer itr = new StringTokenizer(value.toString().toLowerCase().replaceAll("[^a-z]", " "));
		String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
		while (itr.hasMoreTokens()) {
			word.set(itr.nextToken());
			context.write(fileName+"_"+word, one);
		}
	}
}
