import java.util.Objects;

public class Document implements Comparable<Document>{

    private int id;
    private String path;
    private String name;

    public Document(String path, String name, int id){
        this.path = path;
        this.name = name;
        this.id = id;
    }

    public Document(Document doc) {
        path = doc.path;
        id = doc.id;
        name = doc.name;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Document o) {
        return this.id - o.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return id == document.id &&
                Objects.equals(path, document.path) &&
                Objects.equals(name, document.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path, name);
    }
}