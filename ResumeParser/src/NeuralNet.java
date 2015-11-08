import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;


public class NeuralNet {
	public double gpaWeight=1;
	public double schoolWeight = 1;
	public double keywordWeight = 1;
	public double experienceWeight = 1;
	public double durationWeight = 1;
	public double lengthWeight = 1;
	public double leadWeight = 1;
	
	public double gpaError = 0;
	public double schoolError = 0;
	public double keywordError =0;
	public double experienceError =0;
	public double durationError =0;
	public double lengthError = 0;
	public double leadError = 0;
	
	
	public NeuralNet()
	{
		
	}
	
	public void normalizeWeights() throws IllegalArgumentException, IllegalAccessException
	{
		Field[] field = (this.getClass()).getDeclaredFields();
		double sum = 0;
		for(int i = 0; i < field.length/2; i++)
		{
			sum += (Double)field[i].get(this);
		}
		for (int i = 0; i < field.length/2; i++)
		{
			field[i].set(this, (Double)field[i].get(this)/sum);
		}
	}
	
	
	
	
	public double apply (double[] features) throws IllegalArgumentException, IllegalAccessException
	{
		int n = features.length;
		double ans = 0;
		Field[] field = (this.getClass()).getDeclaredFields();
		for (int i = 0; i < n/2; i++)
		{
			ans += features[i]*(Double)field[i].get(this);
		}
		return ans;
	}
	
	public void save() throws FileNotFoundException, IllegalArgumentException, IllegalAccessException
	{
		System.out.println("saving");
		PrintWriter out = new PrintWriter("saveData.txt");
		Field[] field = (this.getClass()).getDeclaredFields();
		for (int i = 0; i < field.length; i++)
		{
			out.println(field[i].get(this));
		}
		out.close();
		System.out.println("saved");
		
	}
	
	public void load() throws IOException, NumberFormatException, IllegalArgumentException, IllegalAccessException
	{
		 String fileName = "saveData.txt";
		 //String line = null;
		 FileReader fileReader =  new FileReader(fileName);
		 BufferedReader bufferedReader =   new BufferedReader(fileReader);
		 Field[] field = (this.getClass()).getDeclaredFields();
		 for (int i = 0; i < field.length; i++)
		 {
			 System.out.println(i+ " " + field.length);
			 field[i].set(this, Double.valueOf(bufferedReader.readLine()));
			 System.out.println(field[i].get(this));
		 }
	}
	public void update(Resume resume) throws IllegalArgumentException, IllegalAccessException
	{
		
		double[] features = resume.features;
		double newRating = resume.givenRating;
		int n = features.length;
		Field[] field = (this.getClass()).getDeclaredFields();
		System.out.println("Number Rated" + Session.numRated);
		for(int i = n; i < 2*n; i++)
		{
			field[i].set(this, field[i].getDouble(this) + Math.abs(features[i-n] - newRating));
			field[i-n].set(this, 10-field[i].getDouble(this)/(Session.numRated+1));
		}
		/*double[] features = resume.features;
		double newRating = resume.givenRating;
		Field[] field = (this.getClass()).getDeclaredFields();
		int n = field.length;
		//int numRated = Session.numRated;
		System.out.println("Number Rated" + Session.numRated);
		
		double[] totalErrors = new double[n/2];
		
		for (int i = n/2; i < n; i++)
		{
			totalErrors[i-n/2] = field[i].getDouble(this)*Session.numRated;
			if (newRating != -1)
			{
				totalErrors[i-n/2] += Math.abs(features[i-n/2]-newRating);
				totalErrors[i-n/2] /= (Session.numRated+1);
			}
		}
		for (int i = 0; i < field.length/2;i++)
		{
			field[i].set(this, 10 - (Double)field[i+n/2].get(this));
		}
		this.normalizeWeights(); */
	}
}
