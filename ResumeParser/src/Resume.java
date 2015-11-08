import java.io.File;


public class Resume implements Comparable<Resume>{
	public File file;
	public double predictedRating;
	public double givenRating = -1;
	public int roundedRating;
	public String fullText;
	public double[] features;
	public String name;

	
	Resume()
	{
		
		
	}
	Resume(File file)
	{
		this.file = file;
		this.fullText = extractText(file);
	}
	
	public String extractText(File file)
	{
		String ans = "";
		//ans = get Text
		return ans;
	}
	
	public void updateGivenRating(double givenRating) throws IllegalArgumentException, IllegalAccessException
	{
		this.givenRating = givenRating;
		
		Session.net.update(this);
		System.out.println("Updated rating to" + givenRating);
		Session.incrementRated();
		System.out.println ("Incremented" + Session.numRated);
	}
	
	public void updateRoundedRating()
	{
		this.roundedRating = (int) Math.round(this.predictedRating);
		if (this.roundedRating < 0)
		{
			this.roundedRating = 0;
		}
		if (this.roundedRating > 10)
		{
			this.roundedRating = 10;
		}
	}
	@Override
	public int compareTo(Resume arg0) {
		// TODO Auto-generated method stub
		return -(int) (1000*this.predictedRating-1000*arg0.predictedRating);
	}
	
	public void round(double lowest, double highest)
	{
		this.predictedRating = (this.predictedRating - lowest)*10/(highest-lowest);
	}

}
