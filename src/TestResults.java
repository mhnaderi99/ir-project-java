import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class TestResults {
    JFrame f;
    public TestResults(Map<Document, ArrayList<Integer>> data, String str) throws IOException {
        f=new JFrame();
        DefaultMutableTreeNode term = new DefaultMutableTreeNode(str);
        for (Document document: data.keySet()){
            DefaultMutableTreeNode doc = new DefaultMutableTreeNode(new File(document.getPath()).getCanonicalPath());
            for (Integer pos: data.get(document)){
                DefaultMutableTreeNode position = new DefaultMutableTreeNode(pos);
                doc.add(position);
            }
            term.add(doc);
        }

        JTree jt=new JTree(term);
        JScrollPane sp=new JScrollPane(jt);
        f.add(sp);
        f.setSize(400,300);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    public void show() {
        f.setVisible(true);
    }

    public void setTitle(String str){
        f.setTitle("Test Results for \"" + str + "\"");
    }

}