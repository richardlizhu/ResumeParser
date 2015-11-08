import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.icepdf.ri.common.MyAnnotationCallback;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;
public class ResumeParser {

	public static void main(String[] args) throws IOException, IllegalArgumentException, IllegalAccessException
	{
	
		final String[] folder = {""};
		Session.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
		WindowListener exitListener = new WindowAdapter() {

		    @Override
		    public void windowClosing(WindowEvent e) {
		        int confirm = JOptionPane.showOptionDialog(
		             null, "Would you like to save your new Neural Network?", 
		             "Exit Confirmation", JOptionPane.YES_NO_OPTION, 
		             JOptionPane.QUESTION_MESSAGE, null, null, null);
		        if (confirm == 0) {
		           try {
		        	Session.net.normalizeWeights();
					Session.net.save();
				} catch (FileNotFoundException | IllegalArgumentException
						| IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        }
		        System.exit(0);
		    }
		};
		
		
		Session.frame.addWindowListener(exitListener);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JButton enter = new JButton("Score Resumes");
		final JTextField textField = new JTextField("Input Folder Name Here");
		final JTextField keywords = new JTextField("Put keywords here separated by a comma and a space");
		enter.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
			folder[0] = textField.getText();
			Session.keywords = keywords.getText().split(", ");
			
		
			
			
			
			
			
			try {
				continueMethod(folder[0]);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		  }
		});

		panel.add(textField);
		panel.add(enter);
		panel.add(keywords);
		Session.frame.add(panel);
        Session.frame.setSize(360,100);
        Session.frame.pack();
		Session.frame.setLocationRelativeTo(null);
		Session.frame.setVisible(true);

		

	}
	public static void continueMethod(String folder) throws IOException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
        String[] colNames = {"Name", "Predicted Rating", "Your Rating"};

		folder = "./" + folder;
		System.out.println(folder);
		Session thisSession = new Session(folder);
		File toFolder = new File(folder);
		File[] listOfFiles = toFolder.listFiles();
        Object[][] data = new Object[listOfFiles.length][3];
        for (int i = 0; i < listOfFiles.length; i++) {
            File f = listOfFiles[i];
            
            PDFTextStripper pdfStripper = null;
            PDDocument pdDoc = null;
            COSDocument cosDoc = null;
            PDFParser parser = new PDFParser(new FileInputStream(f));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(3);
            String parsedText = pdfStripper.getText(pdDoc);
            Resume newResume = new Resume(f);
            newResume.fullText = parsedText;
            newResume.name = getName(newResume.fullText);
            data[i][0] = newResume.name;
            double length = getLengthMetric(newResume.fullText);
            double newGPA = getGPA(newResume.fullText);
            //double newGPA = getLengthMetric(newResume.fullText);
            double education = getAssociatedSchools(newResume.fullText);
            double keyword = getKeyword(newResume.fullText);
            //double experience = getLengthMetric(newResume.fullText);
            //double duration = getLengthMetric(newResume.fullText);
            double experience = 0;
            double duration = 0;
            double leadership = getLeadership(newResume.fullText);
            newResume.features = new double[7];
            newResume.features = new double[] {newGPA, education, keyword, experience, duration, length, leadership};
            
            
            for (int j = 0; j < 7; j++)
            {
            	System.out.print(newResume.features[j]);
            }
            System.out.println();
            
            
            newResume.predictedRating = Session.net.apply(newResume.features);
           // data[i][1] = newResume.predictedRating;
            //data[i][1] = getAssociatedSchools(newResume.fullText);
          //  data[i][2] = newResume.givenRating;
            Session.resumes[i] = newResume;
            
            //System.out.println(parsedText);
            
            pdDoc.close();
        }
        Arrays.sort(Session.resumes);
        Session.updateHighLow();
        for (int i = 0; i < listOfFiles.length;i++)
        {
        	Session.resumes[i].round(Session.lowestPredictedRating, Session.highestPredictedRating);
        	Session.resumes[i].updateRoundedRating();
        	Resume newResume = Session.resumes[i];
        	System.out.println(newResume.predictedRating);
            data[i][1] = newResume.roundedRating;
       data[i][0] = newResume.name;
          data[i][2] = newResume.givenRating;
        }
        Session.objects = data;
        Session.folder = folder;
        Session.cols = colNames;
        	
        	
        JTable table = createJTable(data, colNames, folder);
        JScrollPane panel = new JScrollPane(table);
        panel.setLayout(new ScrollPaneLayout());

        Session.frame.getContentPane().removeAll();
        Session.frame.setSize(500, 400);
        Session.frame.add(panel);
        Session.frame.revalidate();
        Session.frame.repaint();
	}
	

	
	
	public static JTable createJTable(Object[][] data, String[] colNames, final String path) {
        DefaultTableModel tableModel = new DefaultTableModel(data, colNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(data, colNames);
        table.setModel(tableModel);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    final int row = target.getSelectedRow();
                    //int column = target.getSelectedColumn();


                    // Spawn the pdf Panel.
                    SwingController controller = new SwingController();
                    SwingViewBuilder factory = new SwingViewBuilder(controller);

                    JPanel viewerComponentPanel = factory.buildViewerPanel();
                    controller.getDocumentViewController().setAnnotationCallback(
                            new MyAnnotationCallback( controller.getDocumentViewController()));

                    JFrame applicationFrame = new JFrame();
                    applicationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                   
                    
            		WindowListener exitListener = new WindowAdapter() {

            		    @Override
            		    public void windowClosing(WindowEvent e) {
            		    	Integer feedback = getFeedback();
            		    	try {
								Session.resumes[row].updateGivenRating((double)feedback);
								
								Session.objects[row][2] = (double) feedback;
								System.out.println(Session.objects[row][2]);

						        JTable table = createJTable(Session.objects, Session.cols, Session.folder);
						        JScrollPane panel = new JScrollPane(table);
						        panel.setLayout(new ScrollPaneLayout());

						        Session.frame.getContentPane().removeAll();
						        Session.frame.setSize(500, 400);
						        Session.frame.add(panel);
						        Session.frame.revalidate();
						        Session.frame.repaint();

							} catch (IllegalArgumentException
									| IllegalAccessException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
            		    }
            		};
            		
            		applicationFrame.addWindowListener(exitListener);
                    
                    
                    
                    applicationFrame.getContentPane().add(viewerComponentPanel);

                    System.out.println(path);
                    File toFolder = new File(path);
                    File[] listOfFiles = toFolder.listFiles();

                    // Now that the GUI is all in place, we can try opening a PDF
                    controller.openDocument(Session.resumes[row].file.getAbsolutePath());

                    // show the component
                    applicationFrame.pack();
                    applicationFrame.setVisible(true);
                    
                    
                    System.out.println("Clicked");

                }
            }
        });

        table.setFillsViewportHeight(true);
        return table;
    }
	
	
	public static Integer getFeedback() {
		String[] choices = new String[]{"0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String s = (String) JOptionPane.showInputDialog(
                null,
                "Your rating for this resume.",
                "Feedback",
                JOptionPane.PLAIN_MESSAGE,
                null,
                choices,
                choices[4]);
        return Integer.parseInt(s);
    }
	
	
	
	
	public static double getGPA(String fullText)
	{
		double a = -1;
		boolean needAnswer = true;
		String tempText = fullText.toUpperCase();
		while (needAnswer && tempText.contains("GPA"))
		{

			int gpaPosition = tempText.indexOf("GPA");
			String tempTemp = tempText.substring(gpaPosition-10, gpaPosition+10);
			Matcher matcher = Pattern.compile("[-+]?\\d*\\.\\d+|\\d+").matcher(tempTemp);
			matcher.find();
			try
			{
				double i = Double.valueOf(matcher.group());
				if (a == -1 && 0 <= i && i <= 4.0)
				{
					a = i;
				}
			}
			catch(Exception e)
			{
				
			}
			
			//System.out.println(i);
			tempText = tempText.substring(tempText.indexOf("GPA")+2);
		}
		if (a==-1)
		{
			return 5.0;
		}
		return a*2.5;
	}

		/*while(tempText.contains("GPA") && a==-1)
		{
			int gpaPosition = tempText.indexOf("GPA");
			String tempTemp = tempText.substring(gpaPosition, gpaPosition+10);
			System.out.println(tempTemp);
			Matcher matcher = Pattern.compile("[-+]?\\d*\\.\\d+|\\d+").matcher(tempTemp);
			matcher.find();
			double i = Double.valueOf(matcher.group());
			if (a == -1 && 0 <= i && i <= 4.0)
			{
				a = i;
				break;
			}
			tempText = tempText.substring(tempText.indexOf("GPA")+2);
		}
		return a;
	}*/
	
	public static double getAssociatedSchools(String fullText)
	{
		fullText = fullText.toLowerCase();
		int numOccurence = StringUtils.countMatches(fullText, "college");
		numOccurence += StringUtils.countMatches(fullText, "university");
		numOccurence += StringUtils.countMatches(fullText, "institute");
		numOccurence += StringUtils.countMatches(fullText, "school");
		String[] schools = new String[numOccurence];
		for (int i = 0; i < numOccurence; i++)
		{
			
		}
		return (double) numOccurence;
		
	}
	
	public static double getLengthMetric(String fullText)
	{
		String[] words = fullText.split(" ");
		double numWords = words.length;
		double toAdd = (625-numWords)/150;
		toAdd = Math.pow(toAdd, 2);
		toAdd = 5-toAdd;
		double averageWordLength = ((double)fullText.length())/numWords;
		
		return (averageWordLength-3.0) + toAdd;
	}
	
	public static String getName(String fullText)
	{
		String[] lines = fullText.split("\n");
		int i = 0;
		while(isNumber(lines[i]))
		{
			i++;
		}
		return lines[i];
	}
	
	public static boolean isNumber(String str)
	{
		  try  
		  {  
		    double d = Double.parseDouble(str);  
		  }  
		  catch(NumberFormatException nfe)  
		  {  
			  if (str.length() < 3)
			  {
				  return true;
			  }
		    return false;  
		  }  
		  return true;
	}
	
	public static double getLeadership(String fullText)
	{
		fullText = fullText.toLowerCase();
		int numOccurence = StringUtils.countMatches(fullText, "lead");
		numOccurence += StringUtils.countMatches(fullText, "manage");
		//numOccurence += StringUtils.countMatches(fullText, "");
		//numOccurence += StringUtils.countMatches(fullText, "school");
		if (numOccurence > 10)
		{
			numOccurence = 10;
		}
		return (double) numOccurence;
	}
	
	public static double getKeyword(String fullText)
	{
		int numOccurence = 0;
		fullText = fullText.toLowerCase();
		String[] keywords = Session.keywords;
		if (keywords != null)
		{
			for (int i = 0; i < keywords.length; i++)
			{
				numOccurence += StringUtils.countMatches(fullText, keywords[i]);
			}
			return numOccurence/2.0;
		}
		{
			return 5.0;
		}

	}
}
