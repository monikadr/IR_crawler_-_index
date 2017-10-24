import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;

public class WordCount {

  public static class WordCountMapper
       extends Mapper<LongWritable, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable();
    private Text word = new Text();
    public void map(LongWritable key, Text value, Context context
                    ) throws IOException, InterruptedException {
      String line = value.toString();
      String[] docarr = line.split("\\s");
      int doc_id = Integer.parseInt(docarr[0]);
      one.set(doc_id);
      StringTokenizer tokenizer  = new StringTokenizer(line);
      while (tokenizer.hasMoreTokens()) {
        word.set(tokenizer.nextToken());

        context.write(word, one);
      }
    }
  }

  public static class WordCountReducer
       extends Reducer<Text,IntWritable,Text,Text> {
    // private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      Map<String,Integer> wordcounter = new HashMap<String,Integer>();
      String temp;
      for(IntWritable i : values)
      {
        temp = i.toString();
        if(wordcounter.containsKey(temp))
        {
          sum=wordcounter.get(temp);
          sum++;
          wordcounter.put(temp,sum);
        }
        else
        {
          sum=1;
          wordcounter.put(temp,sum);
        }
      }

      String count = new String();
      for(String w : wordcounter.keySet())
      {
        count += w+":"+wordcounter.get(w)+"\t";
      }
      Text result = new Text();
      result.set(count);
      context.write(key, result);
    }
  }

  public static void main(String[] args)
		  throws IOException, ClassNotFoundException, InterruptedException {
    if (args.length != 2) {
        System.err.println("Usage: WordCount <<<input path>>> <<<output path>>>");
        System.exit(-1);
      }

      Job job = new Job();
      job.setJarByClass(WordCount.class);
      job.setJobName("Word Count");

      FileInputFormat.addInputPath(job, new Path(args[0]));
      FileOutputFormat.setOutputPath(job, new Path(args[1]));

      job.setMapperClass(WordCountMapper.class);
      job.setReducerClass(WordCountReducer.class);

      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(IntWritable.class);

      job.waitForCompletion(true);
  }
}
