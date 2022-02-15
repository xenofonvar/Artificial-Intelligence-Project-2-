import java.io.File;

import java.io.FileNotFoundException;
import java.util.*;
import java.lang.String;
import static java.lang.Integer.parseInt;

public class ImportData {
    public static void main(String[] args) {
        String dir = "//aclImdb";
        File file = new File(/*"C:\\Users\\User\\Desktop\\ΕΡΓΑΣΙΑ 2\\βαση δεδομενων\\aclImdb"*/dir);
        ImportData.Loadataset(file, 1000, 500);//paralipei tis n prvtes lekseis toy vocab


        Bayes b = new Bayes();

        b.Train();

        System.out.println("Results of Train Data ");
        b.Test(trainreview, trainstars);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Results of Development Data ");
        b.Test(devreview, devstars);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Results of Test Data ");
        b.Test(testreview, teststars);

    }



    private static ArrayList<HashMap<Integer,Integer>> trainreview = new ArrayList<>();
    private static ArrayList<HashMap<Integer,Integer>> devreview = new ArrayList<>();
    private static ArrayList<HashMap<Integer,Integer>> testreview = new ArrayList<>();
    private static ArrayList<Integer> trainstars = new ArrayList<Integer>();
    private static ArrayList<Integer> devstars = new ArrayList<Integer>();
    private static ArrayList<Integer> teststars = new ArrayList<Integer>();
    private static boolean intrain = false;
    private static boolean intest = false;

    public static ArrayList<HashMap<Integer,Integer>> getTrainreview(){
        return trainreview;
    }
    public static ArrayList<Integer> getTrainstars() { return trainstars; }
    public static ArrayList<HashMap<Integer,Integer>> getDevreview(){ return devreview; }
    public static ArrayList<Integer> getDevstars() { return devstars; }
    public static ArrayList<HashMap<Integer,Integer>> getTestreview(){
        return testreview;
    }
    public static ArrayList<Integer> getTeststars() { return teststars; }


    public static void Loadataset(File path, int m, int n) {
        int tempstar = 0;
        File[] database = path.listFiles();
        if (database != null) {
            for (File p : database) {
                if (p.isFile() && p.getName().equalsIgnoreCase("labeledBow.feat")) {
                    Scanner scan2 = null;
                    try {
                        scan2 = new Scanner(p);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    while (scan2.hasNextLine()) {
                        String line = scan2.nextLine();
                        Scanner scanline = new Scanner(line);
                        HashMap<Integer,Integer> temp = new HashMap<>();
                        tempstar = parseInt(scanline.next());
                        while (scanline.hasNext()) {
                            String[] word = scanline.next().split(":");
                            if (intrain) {
                                if(parseInt(word[0])<m && parseInt(word[0])>n-1) {
                                temp.put(parseInt(word[0]), parseInt(word[1]));
                                }
                            }
                            if (intest) {
                                if(parseInt(word[0])<m && parseInt(word[0])>n-1) {
                                temp.put(parseInt(word[0]), parseInt(word[1]));
                                }
                            }
                        }
                        if(intrain) {
                            if (!temp.isEmpty()) {
                                trainreview.add(temp);
                                trainstars.add(tempstar);
                            }
                        }
                        if(intest) {
                            if(!temp.isEmpty()){
                                testreview.add(temp);
                                teststars.add(tempstar);
                            }
                        }
                    }
                    if(intrain) {
                        random(trainreview, trainstars);
                        int k = trainreview.size()-1;
                        int limit = (int)(trainreview.size()*0.9)-1;
                        while(k>limit){
                            devreview.add(trainreview.get(k));
                            devstars.add(trainstars.get(k));
                            trainreview.remove(k);
                            trainstars.remove(k);
                            k--;
                        }
                        Collections.reverse(devreview);
                        Collections.reverse(devstars);
                    }
                    intest = false;
                    intrain = false;

                }else if (p.isDirectory()) {
                    if (p.getName().equalsIgnoreCase("train")) {
                        intrain = true;
                    }
                    if (p.getName().equalsIgnoreCase("test")) {
                        intest = true;
                    }
                    Loadataset(p, m, n);
                }
            }
            random(testreview, teststars);
        } else {
            System.out.println("Couldn't find any review.");
        }
    }

    public static void random(ArrayList<HashMap<Integer,Integer>> examples, ArrayList<Integer> score){
        Random rand = new Random();
        for (int i = 0; i < examples.size(); i++) {
            int randomIndexToSwap = rand.nextInt(examples.size());
            HashMap<Integer,Integer> temp = examples.get(randomIndexToSwap);
            examples.set(randomIndexToSwap, examples.get(i));
            examples.set(i, temp);

            Integer temp2 = score.get(randomIndexToSwap);
            score.set(randomIndexToSwap, score.get(i));
            score.set(i, temp2);

        }
    }
}