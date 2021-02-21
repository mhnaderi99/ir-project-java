import javax.print.Doc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MaxHeap {

    private HashMap<Document, Double> map;
    private Double[] Heap;
    private Document[] docs;
    private int size;
    private int maxsize;

    // Constructor to initialize an 
    // empty max heap with given maximum 
    // capacity. 
    public MaxHeap(int maxsize)
    {
        this.maxsize = maxsize;
        this.size = 0;
        Heap = new Double[this.maxsize + 1];
        Heap[0] = Double.MAX_VALUE;
    }

    public MaxHeap(HashMap<Document, Double> map) {
        maxsize = map.size();
        size = 0;
        Heap = new Double[maxsize + 1];
        docs = new Document[maxsize + 1];
        Heap[0] = Double.MAX_VALUE;
        docs[0] = null;
        for (Document doc: map.keySet()) {
            insert(new DocDouble(doc, map.get(doc)));
        }
    }

    // Returns position of parent 
    private int parent(int pos)
    {
        return pos / 2;
    }

    // Below two functions return left and 
    // right children. 
    private int leftChild(int pos)
    {
        return (2 * pos);
    }
    private int rightChild(int pos)
    {
        return (2 * pos) + 1;
    }

    // Returns true of given node is leaf 
    private boolean isLeaf(int pos)
    {
        if (pos > (size / 2)) {
            return true;
        }
        return false;
    }

    private void swap(int fpos, int spos)
    {
        double tmp;
        tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;

        Document temp;
        temp = docs[fpos];
        docs[fpos] = docs[spos];
        docs[spos] = temp;
    }

    // A recursive function to max heapify the given 
    // subtree. This function assumes that the left and 
    // right subtrees are already heapified, we only need 
    // to fix the root. 
    private void maxHeapify(int pos)
    {
        if (isLeaf(pos))
            return;

        if (rightChild(pos) <= size) {
            if (Heap[pos] < Heap[leftChild(pos)] ||
                    Heap[pos] < Heap[rightChild(pos)]) {

                if (Heap[leftChild(pos)] > Heap[rightChild(pos)]) {
                    swap(pos, leftChild(pos));
                    maxHeapify(leftChild(pos));
                } else {
                    swap(pos, rightChild(pos));
                    maxHeapify(rightChild(pos));
                }
            }
        } else {
            if (Heap[leftChild(pos)] > Heap[pos]) {
                swap(pos, leftChild(pos));
                maxHeapify(leftChild(pos));
            }
        }
    }

    // Inserts a new element to max heap
    public void insert(DocDouble element)
    {
        size++;
        Heap[size] = element.getDoub();
        docs[size] = element.getDoc();

        // Traverse up and fix violated property
        int current = size;
        while (Heap[current] > Heap[parent(current)]) {
            swap(current, parent(current));
            current = parent(current);
        }
    }

    // Remove an element from max heap
    public DocDouble extractMax()
    {
        double double_popped = Heap[1];
        Document doc_popped = docs[1];
        Heap[1] = Heap[size];
        docs[1] = docs[size];
        size--;
        maxHeapify(1);
        return new DocDouble(doc_popped, double_popped);
    }

} 