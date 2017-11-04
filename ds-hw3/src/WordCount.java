import java.io.IOException;
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

public class WordCount {

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, Text>{

        //private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            InputSplit inputSplit = context.getInputSplit();
            String fileName = ((FileSplit) inputSplit).getPath().getName();

            //toLower and replace anything besides a-z with a space
            String newValue = value.toString().toLowerCase().replaceAll("[^a-z]", " ");

            StringTokenizer itr = new StringTokenizer(newValue);
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                //context.write(word, one);
                context.write(word, new Text(fileName));
            }
        }
    }

    public static class IntSumReducer
            extends Reducer<Text,Text,Text,Text> {
        //private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<Text> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            boolean check = false;

            String fileName="";

            String textString="{";

            /*reducing by counting the filename against each key*/

            for (Text val : values)

                {

                    if(!check)

                    {

                        fileName=val.toString();

                        check=true;

                    }

                    if (fileName.equals(val.toString())){

                        sum=sum+1; //for counting the number of occurance in each file

                    }

                    else

                    {

                        textString+=fileName + "="+sum +", ";

                        fileName=val.toString();

                        sum=1;

                    }

                }

                textString+= fileName + "="+sum +"} \n"; //making output pattern

            context.write(key, new Text(textString));

        }

    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}