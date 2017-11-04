import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class Chapter implements WritableComparable<Chapter> {
	
	private Text chapter;
	
	private Text  word;

    public Chapter(Text chapter, Text word) {
        this.chapter = chapter;
        this.word = word;
    }
    public Chapter() {
        this.chapter = new Text();
        this.word = new Text();
    }
	@Override
	public void readFields(DataInput arg0) throws IOException {
        chapter.readFields(arg0);
        word.readFields(arg0);
		
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		chapter.write(arg0);
		word.write(arg0);
		
	}

	@Override
	public int compareTo(Chapter o) {
		 Text thisChapter = o.chapter;
		 Text thisWord = o.word;
		 
		 if(this.word.compareTo(thisWord)==0) {
			 return this.chapter.compareTo(thisChapter);
			 
		 }else {
			 return this.word.compareTo(thisWord);
		 }

	}
	@Override
	public String toString() {
		return "\n"+this.word.toString()+" "+this.chapter.toString();
	}

}
