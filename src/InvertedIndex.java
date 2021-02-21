import java.io.IOException;
import java.util.*;

public class InvertedIndex {

    private TreeMap<Term, Map<Document, ArrayList<Integer>>> list;

    public InvertedIndex(){
        list = new TreeMap<Term, Map<Document, ArrayList<Integer>>>();
    }

    public void addTermDoc(Term term, Document document, int position){
        if (! list.containsKey(term)){
            list.put(term, new HashMap<>());
        }
        if (! list.get(term).containsKey(document)) {
            list.get(term).put(document, new ArrayList<>());
        }
        list.get(term).get(document).add(position);
    }

    public void sortLists(){
        for (Term term: list.keySet()){
            TreeMap<Document, ArrayList<Integer>> sorted = new TreeMap<>(list.get(term));
            list.put(term, sorted);
        }
    }

    public void print() {
        for (Term term: list.keySet()){
            System.out.println(term.getTerm() + ": ");
            for (Document document: list.get(term).keySet()) {
                System.out.print("      " + document.getId() + ": ");
                for (Integer pos: list.get(term).get(document)){
                    System.out.print(pos + ", ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public void printTerms(){
        for (Term term: list.keySet()){
            System.out.println(term.getTerm());
        }
    }

    public Map<Document, ArrayList<Integer>> test(String term) throws IOException {
        Term tmp = new Term(term);
        if (list.keySet().contains(tmp)){
            return list.get(tmp);
        }
        else {
            return null;
        }
    }

    public TreeMap<Term, Map<Document, ArrayList<Integer>>> getList() {
        return list;
    }

    public void printDocs(Document document){
        ArrayList<String> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        for (Term term: list.keySet()){
            for (Document doc: list.get(term).keySet()){
                for (int i: list.get(term).get(doc)){
                    if (doc == document){
                        x.add(term.getTerm());
                        y.add(i);
                    }
                }
            }
        }

        for (int i=0; i<y.size();i++){
            for (int j=i+1;j<y.size();j++){
                if(y.get(i) > y.get(j)){
                    Collections.swap(y, i, j);
                    Collections.swap(x, i, j);
                }
            }
        }

        for (int i=0; i<y.size();i++){
            System.out.println(y.get(i) + ": " + x.get(i));
        }
    }
}
