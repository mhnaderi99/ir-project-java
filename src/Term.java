import java.util.Objects;

public class Term implements Comparable<Term>{

    private String term;

    public Term(String term){
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term1 = (Term) o;
        return term.equals(term1.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term);
    }

    @Override
    public int compareTo(Term o) {
        return term.compareTo(o.term);
    }
}
