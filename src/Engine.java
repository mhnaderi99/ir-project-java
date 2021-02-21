import javax.print.Doc;
import java.io.*;
import java.security.KeyPair;
import java.util.*;
import java.lang.Math;

public class Engine {

    private static final int CONSTANT_R_FOR_CHAMPION_LISTS = 5;
    private static HashSet<Document> docs = new HashSet<>();
    private static InvertedIndex index = new InvertedIndex();
    private static final String DEFAULT_DOCS_PATH = "../data/sampleDoc/";
    private static final String DEFAULT_STOP_WORDS_PATH = "../data/stop_words.txt";
    private static String docsPath;
    private static String stopWordsPath;
    private static HashSet<String> stopWords = new HashSet<>();
    private static int num = 0;
    private static HashMap<String, Integer> tf = new HashMap<>();
    private static HashMap<Document, ArrayList<Double>> tfidf = new HashMap<>();
    private static HashMap<Term, ArrayList<Integer>> tfs = new HashMap<>();
    private static HashMap<Term, HashSet<Document>> championLists = new HashMap<>();

    public static void start(String docspath, String stopwordspath) throws IOException {
        if (docspath.equals("")) {
            docsPath = new String(DEFAULT_DOCS_PATH);
        } else {
            docsPath = new String(docspath);
        }
        if (stopwordspath.equals("")) {
            stopWordsPath = new String(DEFAULT_STOP_WORDS_PATH);
        } else {
            stopWordsPath = new String(stopwordspath);
        }

        final File folder = new File(docsPath);
        listFilesForFolder(folder);
        //readStopWords(); //read from file
        extractStopWords(folder, 10); // extract from documents
        tokenizeDocs();
        index.sortLists();
        findTfIdf();
        findChampionLists();
    }

    private static void readStopWords() throws IOException {
        File file = new File(stopWordsPath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            stopWords.add(st);
        }
    }

    public static void listFilesForFolder(File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                Document doc = new Document(fileEntry.getPath(), fileEntry.getName(), num);
                num++;
                docs.add(doc);
            }
        }
    }

    public static void extractStopWords(File folder, int stopwordsSize) throws IOException {
        for (Document doc : docs) {
            File file = new File(doc.getPath());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                st = st.replaceAll("[^\u200Cءأإؤيةۀًٌٍَُِّآابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهی]", " ");
                StringTokenizer defaultTokenizer = new StringTokenizer(st);
                while (defaultTokenizer.hasMoreTokens()) {
                    String str = defaultTokenizer.nextToken();
                    String stem = stem(str);
                    if (tf.containsKey(stem)) {
                        tf.put(stem, tf.get(stem) + 1);
                    } else {
                        tf.put(stem, 1);
                    }
                }
            }
        }

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        tf.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        int i = 0;
        for (String str : sortedMap.keySet()) {
            if (i >= stopwordsSize) {
                break;
            }
            i++;
            stopWords.add(str);
        }
    }

    public static void tokenizeDocs() throws IOException {
        for (Document doc : docs) {
            int pos = 0;
            File file = new File(doc.getPath());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                st = st.replaceAll("[^\u200Cءأإؤيةۀًٌٍَُِّآابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهی]", " ");
                StringTokenizer defaultTokenizer = new StringTokenizer(st);
                while (defaultTokenizer.hasMoreTokens()) {
                    String str = defaultTokenizer.nextToken();
                    String stem = stem(str);
                    if (!stopWords.contains(stem)) {
                        Term term = new Term(stem);
                        index.addTermDoc(term, doc, pos);
                        pos++;
                    }
                }
            }
        }
    }

    public static InvertedIndex getIndex() {
        return index;
    }

    public static HashMap<Document, Double> search(String query) {
        int stops = 0;
        HashMap<Document, Integer> scores = new HashMap<>();
        String[] words = query.split("\\s+");
        if (words.length == 1) {
            //index.test(query);
            if (stopWords.contains(query)) {
                return null;
            }
            String stem = stem(query);
            if (index.getList().containsKey(new Term(stem))) {
                for (Document doc : index.getList().get(new Term(stem)).keySet()) {
                    scores.put(doc, 1);
                }
            } else {
                return null;
            }
        } else if (words.length > 1) {
            for (String word : words) {
                if (stopWords.contains(word)) {
                    stops++;
                    continue;
                }
                String stem = stem(word);
                if (stopWords.contains(stem)) {
                    stops++;
                    continue;
                }
                if (!index.getList().containsKey(new Term(stem))) {
                    continue;
                }
                for (Document doc : index.getList().get(new Term(stem)).keySet()) {
                    if (scores.containsKey(doc)) {
                        scores.put(doc, scores.get(doc) + 1);
                    } else {
                        scores.put(doc, 1);
                    }
                }
            }
        }
        int len = words.length - stops;
        HashMap<Document, Double> final_scores = new HashMap<>();
        final_scores = sortMap(scores, len);
        return final_scores;
    }

    public static LinkedHashMap<Document, Double> search2(String query, int k) {
        int stops = 0;
        HashMap<Document, Integer> scores = new HashMap<>();
        String[] words = query.split("\\s+");
        String[] stemmed = new String[words.length];
        ArrayList<String> terms = new ArrayList<>();
        for (int i=0; i<words.length;i++){
            stemmed[i] = stem(words[i]);
        }
        for (String term: stemmed) {
            if (! stopWords.contains(term)) {
                terms.add(term);
            }
        }
        HashSet<Document> intersection = new HashSet<>(docs);
        for (String term: terms) {
            Term tmp = new Term(term);
            intersection.retainAll(championLists.get(tmp));
        }
        if (terms.size() == 0) {
            return null;
        } else {
            int n = docs.size();
            ArrayList<Double> queryVec = new ArrayList<>();
            for (Term term: index.getList().keySet()) {
                if (terms.contains(term.getTerm())) {
                    int tfd = Collections.frequency(terms, term.getTerm());
                    int df = index.getList().get(term).size();
                    double w = Math.log10(1 + tfd)*Math.log10(n/df);
                    queryVec.add(w);
                } else {
                    queryVec.add(0.0);
                }
            }

            HashMap<Document, Double> similarity = new HashMap<>();
            for (Document doc: intersection){

                if (dot(tfidf.get(doc), queryVec) != 0) {
                    double sim = cosineSim(tfidf.get(doc), queryVec);
                    similarity.put(doc, sim);
                }
            }

            LinkedHashMap<Document, Double> result = new LinkedHashMap<>();
            MaxHeap heap = new MaxHeap(similarity);
            for (int i=0; i<Math.min(k, similarity.size()); i++){
                DocDouble pop = heap.extractMax();
                result.put(pop.getDoc(), pop.getDoub());
            }
            return result;
        }
    }

    private static HashMap<Document, Double> sortMap(HashMap<Document, Integer> map, int len) {
        // Create a list from elements of HashMap
        List<Map.Entry<Document, Integer>> list = new LinkedList<Map.Entry<Document, Integer>>(map.entrySet());
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Document, Integer>>() {

            @Override
            public int compare(Map.Entry<Document, Integer> o1, Map.Entry<Document, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        // put data from sorted list to hashmap
        HashMap<Document, Double> temp = new LinkedHashMap<Document, Double>();
        for (Map.Entry<Document, Integer> aa : list) {
            temp.put(aa.getKey(), (double) aa.getValue() / (double) len);
        }
        return temp;
    }

    public static String getDefaultDocsPath() {
        return DEFAULT_DOCS_PATH;
    }

    public static String getDefaultStopWordsPath() {
        return DEFAULT_STOP_WORDS_PATH;
    }

    private static String stem(String term) {
        String tmp = term;
        String stemmed = StemmingRules.stem(tmp);
        while (!stemmed.equals(tmp)) {
            tmp = stemmed;
            stemmed = StemmingRules.stem(tmp);
        }
        return tmp;
    }

    private static void findTfIdf(){
        int n = docs.size();
        for (Document document: docs){
            ArrayList<Double> vector = new ArrayList<>();
            for (Term term: index.getList().keySet()) {
                if (index.getList().get(term).containsKey(document)) {
                    int tfd = index.getList().get(term).get(document).size();
                    int df = index.getList().get(term).size();
                    vector.add(Math.log10((1+tfd)) * Math.log10(n/df));
                } else {
                    vector.add(0.0);
                }

            }
            tfidf.put(document, vector);
        }

        for (Term term: index.getList().keySet()) {
            ArrayList<Integer> temp = new ArrayList<>();
            for (Document doc: docs) {
                int tfd = 0;
                if (index.getList().get(term).containsKey(doc)) {
                    tfd = index.getList().get(term).get(doc).size();
                }
                temp.add(tfd);
            }
            tfs.put(term, temp);
        }

    }

    private static void findChampionLists(){
        for (Term term: index.getList().keySet()) {
            int r = CONSTANT_R_FOR_CHAMPION_LISTS;
            HashMap<Document, Integer> ttf = new HashMap<>();
            int i = 0;
            for (Document doc: docs) {
                ttf.put(doc, tfs.get(term).get(i));
                i++;
            }
            HashSet<Document> topR = new HashSet<>();
            for (int j=0; j<r; j++){
                int max = -1;
                Document maxDoc = null;
                for (Document doc: ttf.keySet()){
                    if (ttf.get(doc) > max) {
                        max = ttf.get(doc);
                        maxDoc = doc;
                    }
                }
                if (max == 0){
                    break;
                }
                topR.add(maxDoc);
                ttf.remove(maxDoc);
            }
            championLists.put(term, topR);
        }
    }

    private static double dot(ArrayList<Double> a, ArrayList<Double> b){
        int l = a.size();
        if (l != b.size()){
            return 0;
        }
        double sum = 0;
        for (int i=0;i<l;i++){
            sum += (a.get(i) * b.get(i));
        }
        return sum;
    }

    private static double sizeOfVec(ArrayList<Double> a) {
        return Math.sqrt(dot(a,a));
    }

    private static double cosineSim(ArrayList<Double> a, ArrayList<Double> b){
        return (dot(a,b))/(sizeOfVec(a)*sizeOfVec(b));
    }

}
