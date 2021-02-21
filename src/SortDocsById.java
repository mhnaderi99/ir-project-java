import java.util.Comparator;

public class SortDocsById implements Comparator<Document> {

    @Override
    public int compare(Document o1, Document o2) {
        return o1.getId() - o2.getId();
    }
}