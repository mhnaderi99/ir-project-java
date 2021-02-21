import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class Results {
    private JFrame f;
    private String data[][];
    private static final String COLUMNS[] = {"Document Name","Document Path","Cosine Similarity"};

    Results(String data[][]){
        f = new JFrame();
        f.setLocationRelativeTo(null);
        this.data = data;
        JTable jt = new JTable(data,COLUMNS);
        jt.setBounds(30,40,200,300);
        JScrollPane sp=new JScrollPane(jt);
        f.add(sp);
        f.setSize(600,250);

    }
    public void show() {
        f.setVisible(true);
    }

    public static int getResultsColumns() {
        return COLUMNS.length;
    }

    public void setTitle(String query){
        f.setTitle("Search Results for \"" + query + "\"");
    }

}