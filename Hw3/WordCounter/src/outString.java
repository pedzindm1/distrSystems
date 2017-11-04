
public class outString implements Comparable<outString> {

      	 String chapter;
      	 int count;
      	 
   	    	public outString(String chapter,int count){
   	    	 this.chapter=chapter;
   	    	 this.count=count;
   	    }
   	    	
   	    	@Override
   	    	public String toString() {
   				return "<"+chapter+", "+count+">";
   	    		
   	    	}

			@Override
			public int compareTo(outString o) {
				
				if(this.count > o.count) {
					return -1;
				}else if(this.count< o.count) {
					return 1;
				}else {
					return this.chapter.compareTo(o.chapter);
				}
				
				
			}
      	 
}
