import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class ResumeParser {
	public static void main(String[] args) throws IOException
	{
		final String[] folder = {""};
		final JFrame frame = new JFrame("Resume Parser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JButton enter = new JButton("Score Resumes");
		final JTextField textField = new JTextField("Put Folder Name Here");
		enter.addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			  folder[0] = textField.getText();
			  try {
				continueMethod(folder[0]);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		  }
		});
		panel.add(textField);
		panel.add(enter);
		frame.add(panel);
		frame.setLocationRelativeTo(null);
		frame.setSize(300,100);
		frame.setVisible(true);

		
		

	}
	public static void continueMethod(String folder) throws IOException
	{
		folder = "./" + folder;
		System.out.println(folder);
		File toFolder = new File(folder);
		File[] listOfFiles = toFolder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		    	File f = listOfFiles[i];
		    	OutputStream oos = new FileOutputStream(i + "test.pdf");

		        byte[] buf = new byte[8192];

		        InputStream is = new FileInputStream(f);

		        int c = 0;

		        while ((c = is.read(buf, 0, buf.length)) > 0) {
		            oos.write(buf, 0, c);
		            oos.flush();
		        }

		        oos.close();
		        System.out.println("stop");
		        is.close();
		    }

	        
	}

}
