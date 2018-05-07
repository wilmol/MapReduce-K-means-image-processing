package nz.ac.auckland.mapreduce.reddit_comments;

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

public class RedditCommentsReducer extends Reducer<Text, IntWritable, Text, DoubleWritable> {

    /**
     * Average the upvote values per sub reddit comment.
     */
    @Override
    public void reduce(Text word, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        ImmutableList<Integer> list = toList(values);
        double mean = list.stream()
                .mapToInt(v -> v)
                .sum()
                / list.size();
        context.write(word, new DoubleWritable(mean));
    }

    private ImmutableList<Integer> toList(Iterable<IntWritable> iterable){
        return StreamSupport.stream(iterable.spliterator(), false)
                .mapToInt(IntWritable::get)
                .boxed()
                .collect(collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }
}
