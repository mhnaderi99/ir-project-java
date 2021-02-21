import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GUI extends JFrame {

    private final int NUMBER_OF_SHOWN_RESULTS = 20;
    private String docsPath;
    private String stopWordsPath;
    private String query;
    public GUI() throws IOException {
        super("Search Engine");
        setLocationRelativeTo(null);
        docsPath = new String();
        stopWordsPath = new String();
        query = new String();
        //setIconImage(new ImageIcon("src/icons/icon.png").getImage());
        //setBackground(BACKGROUND_COLOR);
        setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Border border = BorderFactory.createEmptyBorder(5,5,5,5);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(border);

        JTextField stopWordsPathP = new JTextField(new File(Engine.getDefaultStopWordsPath()).getCanonicalPath());
        stopWordsPathP.setEnabled(false);
        JTextField docsPathButton = new JTextField(new File(Engine.getDefaultDocsPath()).getCanonicalPath());

        JTextField queryField = new JTextField();
        queryField.setOpaque(true);
        queryField.setEnabled(false);
        queryField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JButton searchButton = new JButton("Search ");
        searchButton.setEnabled(false);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                query = queryField.getText();
                LinkedHashMap<Document, Double> result = Engine.search2(query, NUMBER_OF_SHOWN_RESULTS);
                if (result == null || result.size() == 0){
                    JOptionPane.showMessageDialog(GUI.this, "Nothing Was Found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String data[][] = new String[result.size()][Results.getResultsColumns()];
                int i = 0;
                for (Document document: result.keySet()) {
                    data[i][0] = document.getName();
                    try {
                        data[i][1] = new File(document.getPath()).getCanonicalPath();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    data[i][2] = String.valueOf(result.get(document));
                    //System.out.println(document.getName() + ": " + result.get(document));
                    i++;
                }
                //System.out.println();
                Results r = new Results(data);
                r.setTitle(query);
                r.show();
            }
        });
        searchButton.setOpaque(true);
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(border);
        searchPanel.setOpaque(true);
        JPanel p1 = new JPanel(new BorderLayout());
        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(searchButton, BorderLayout.EAST);
        p1.add(queryField, BorderLayout.CENTER);
        p1.add(p2, BorderLayout.EAST);
        p1.setBorder(BorderFactory.createTitledBorder("Search Query"));

        stopWordsPathP.setOpaque(true);
        JButton changeStopWordsPath = new JButton("Change");
        changeStopWordsPath.setEnabled(false);
        changeStopWordsPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser2 = new JFileChooser("../");
                int result = chooser2.showOpenDialog(GUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser2.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    stopWordsPath = new String(selectedFile.getAbsolutePath());
                    stopWordsPathP.setText(stopWordsPath);
                }
            }
        });
        changeStopWordsPath.setOpaque(true);
        JPanel stopWordsPanel = new JPanel(new BorderLayout());
        stopWordsPanel.setBorder(border);
        stopWordsPanel.setOpaque(true);
        JPanel p3 = new JPanel(new BorderLayout());
        JPanel p4 = new JPanel(new BorderLayout());
        p4.add(changeStopWordsPath, BorderLayout.EAST);
        p3.add(stopWordsPathP, BorderLayout.CENTER);
        p3.add(p4, BorderLayout.EAST);
        p3.setBorder(BorderFactory.createTitledBorder("Stop Words Path"));

        docsPathButton.setOpaque(true);
        JButton changeDocsPath = new JButton("Change");
        changeDocsPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser("../");
                chooser.setDialogTitle("Select Documents Path");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(GUI.this) == JFileChooser.APPROVE_OPTION) {
                    docsPath = new String(chooser.getSelectedFile().getAbsolutePath());
                    System.out.println(docsPath);
                    docsPathButton.setText(docsPath);
                }
                else {
                    System.out.println("No Selection ");
                }
            }
        });
        changeDocsPath.setOpaque(true);
        JPanel docsPanel = new JPanel(new BorderLayout());
        docsPanel.setBorder(border);
        docsPanel.setOpaque(true);
        JPanel p5 = new JPanel(new BorderLayout());
        JPanel p6 = new JPanel(new BorderLayout());
        p6.add(changeDocsPath, BorderLayout.EAST);
        p5.add(docsPathButton, BorderLayout.CENTER);
        p5.add(p6, BorderLayout.EAST);
        p5.setBorder(BorderFactory.createTitledBorder("Documents Path"));

        JTextField testField = new JTextField();
        testField.setOpaque(true);
        testField.setEnabled(false);
        testField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JButton testButton = new JButton("  Test   ");
        testButton.setEnabled(false);
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String testString = testField.getText();
                String [] terms = testString.split("\\s+");
                if (terms.length != 1){
                    JOptionPane.showMessageDialog(GUI.this, "You Can Only Test One Single Word!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (! testString.equals("")) {
                    try {
                        Map<Document, ArrayList<Integer>> testResults = Engine.getIndex().test(testString);
                        if (testResults == null || testResults.size() == 0) {
                            JOptionPane.showMessageDialog(GUI.this, "Nothing Was Found!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        TestResults r = new TestResults(testResults, testString);
                        r.setTitle(testString);
                        r.show();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        testButton.setOpaque(true);
        JPanel testPanel = new JPanel(new BorderLayout());
        testPanel.setBorder(border);
        testPanel.setOpaque(true);
        JPanel p7 = new JPanel(new BorderLayout());
        JPanel p8 = new JPanel(new BorderLayout());
        p8.add(testButton, BorderLayout.EAST);
        p7.add(testField, BorderLayout.CENTER);
        p7.add(p8, BorderLayout.EAST);
        p7.setBorder(BorderFactory.createTitledBorder("Test Inverted Index"));



        JPanel panel = new JPanel(new GridLayout(5,1,5,5));
        panel.add(p5);
        panel.add(p3);
        JButton start = new JButton("Create Inverted Index");
        start.setOpaque(true);
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Engine.start(docsPath, stopWordsPath);
                    searchButton.setEnabled(true);
                    queryField.setEnabled(true);
                    testButton.setEnabled(true);
                    testField.setEnabled(true);
                    changeDocsPath.setEnabled(false);
                    changeStopWordsPath.setEnabled(false);
                    docsPathButton.setEnabled(false);
                    stopWordsPathP.setEnabled(false);
                    start.setEnabled(false);
                    JOptionPane.showMessageDialog(GUI.this, "The Inverted Index has been created successfully!", "Complete", JOptionPane.PLAIN_MESSAGE);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });





        panel.add(start);
        panel.add(p7);
        panel.add(p1);
        mainPanel.add(panel);
        add(mainPanel);

    }

    public String getDocsPath() {
        return docsPath;
    }

    public String getStopWordsPath() {
        return stopWordsPath;
    }

    public String getQuery() {
        return query;
    }

    public void showGUI(){
        show();
    }
}
