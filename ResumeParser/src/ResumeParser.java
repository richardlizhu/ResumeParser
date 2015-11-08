import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ResumeParser {

    private static JFrame frame = new JFrame("Resume Parser");

    public static void main(String[] args) throws IOException {

		final String[] folder = {""};
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JButton enter = new JButton("Score Resumes");

		final JTextField textField = new JTextField("Input Folder Name Here");

		enter.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent e) {
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
        frame.setSize(360,100);
        frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void continueMethod(String folder) throws IOException {
        String[] colNames = {"Name", "Resume", "Score"};

        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;

		folder = "./" + folder;
		System.out.println(folder);
		File toFolder = new File(folder);
		File[] listOfFiles = toFolder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            File f = listOfFiles[i];
            System.out.println(f);

            PDFParser parser = new PDFParser(new FileInputStream(f));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(2);
            String parsedText = pdfStripper.getText(pdDoc);

            System.out.println(parsedText);

            OutputStream oos = new FileOutputStream(i + "out.txt");
            InputStream is = new ByteArrayInputStream(parsedText.getBytes(StandardCharsets.UTF_8));
            byte[] buf = new byte[8192];

            int c = 0;

            while ((c = is.read(buf, 0, buf.length)) > 0) {
                oos.write(buf, 0, c);
                oos.flush();
            }

            oos.close();
            is.close();
            pdDoc.close();
        }

        Object[][] data = {
                {"Kathy", "Smith", "Snowboarding"},
                {"John", "Doe", "Rowing"},
                {"Sue", "Black", "Knitting"},
                {"Jane", "White", "Speed reading"},
                {"Joe", "Brown", "Pool"}
        };
        JTable table = createJTable(data, colNames);
        JScrollPane panel = new JScrollPane(table);
        panel.setLayout(new ScrollPaneLayout());

        frame.getContentPane().removeAll();
        frame.setSize(500, 400);
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
	}

    public static JTable createJTable(Object[][] data, String[] colNames) {
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
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();

                    // Spawn the pdf Panel.
                    System.out.println("Clicked");

                }
            }
        });

        table.setFillsViewportHeight(true);
        return table;
    }
}
