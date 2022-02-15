import java.util.ArrayList;
import java.util.HashMap;

public class Leaf {
    //Stores a Leaf for the examples that they do not have the word we are looking for and another leaf for those that have it.
    private Leaf[] right;
    //tag is the word given by IG.
    private int tag;
    //Also we store the score for each leaf and the reviews.
    private ArrayList<Integer> score;
    private ArrayList<HashMap<Integer, Integer>> examples;
    private int value;
    //sign is the category of the leaf
    private int sign;

    //Construct a Leaf
    Leaf(int tag, int value, int sign){
        right = new Leaf[2];
        this.sign = sign;
        this.tag = tag;
        this.value = value;
        this.examples = new ArrayList<>();
        this.score = new ArrayList<>();
    }

    //Accessors
    public Leaf getRight(int np) { return right[np]; }
    public void setRight(Leaf right, int where) { this.right[where] = right;}
    public int getTag() { return tag; }
    public int getSign() { return sign; }
    public void setSign(int s) { this.sign = s; }
    public void setTag(int tag) { this.tag = tag; }
    public void setScore(ArrayList<Integer> score) { this.score = score; }
    public ArrayList<Integer> getScore() { return score; }
    public int getValue() { return value; }
    public void setValue(int val) { this.value = val; }
    public ArrayList<HashMap<Integer, Integer>> getEx() { return examples; }
    public void setEx(ArrayList<HashMap<Integer, Integer>> ex) { examples = ex; }
}
