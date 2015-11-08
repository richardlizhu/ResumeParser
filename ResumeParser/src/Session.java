import java.io.*;

import javax.swing.JFrame;


public class Session {
	public static NeuralNet net;
	public File[] documents;
	public static Resume[] resumes;
	public static int numRated = 0;
	public static JFrame frame = new JFrame("Resume Parser");
	public static Object[][] objects;
	public static String folder;
	public static String[] cols;
	public static double highestPredictedRating = 0;
	public static double lowestPredictedRating = 10;
	public static String[] keywords;
	
	
	
	public Session(String folder) throws IOException, NumberFormatException, IllegalArgumentException, IllegalAccessException
	{
		folder = "./" + folder;
		System.out.println(folder);
		File toFolder = new File(folder);
		File[] listOfFiles = toFolder.listFiles();
		this.documents = toFolder.listFiles();
		resumes = new Resume[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; i++) {
			resumes[i] = new Resume(listOfFiles[i]);
	    }
		this.net = new NeuralNet();
		this.net.load();
	}
	
	public static void incrementRated()
	{
		numRated++;
	}
	
	public static void updateHighLow()
	{
		for (int i = 0; i < Session.resumes.length; i++)
		{
			if (Session.resumes[i].predictedRating < lowestPredictedRating)
			{
				lowestPredictedRating = Session.resumes[i].predictedRating;
			}
			if (Session.resumes[i].predictedRating > highestPredictedRating)
			{
				highestPredictedRating = Session.resumes[i].predictedRating;
			}
			
		}
	}
}
