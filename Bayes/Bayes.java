//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
import java.util.*;

public class Bayes {


    private  ArrayList<HashMap<Integer, Integer>> trainreview ;
    private  ArrayList<Integer> trainstars;

    private  ArrayList<HashMap<Integer, Integer>> devreview ;
    private  ArrayList<Integer> devstars;

    private  ArrayList<HashMap<Integer, Integer>> testreview ;
    private  ArrayList<Integer> teststars;



    private HashMap<Integer, Integer> negDictionary;
    private HashMap<Integer, Integer> posDictionary;





    private int negCounter;




    private int total_words_positive;
    private int total_words_negative;

    public Bayes(){

        trainstars=ImportData.getTrainstars();
        trainreview=ImportData.getTrainreview();

        devstars=ImportData.getDevstars();
        devreview=ImportData.getDevreview();

        teststars=ImportData.getTeststars();
        testreview=ImportData.getTestreview();


        negDictionary = new HashMap<Integer, Integer>();
        posDictionary = new HashMap<Integer, Integer>();

        negCounter = 0;


    }




    public double PriorProbability(String classs){
        negCounter=0;
        for(int i=0; i<trainstars.size(); i++){

            if (trainstars.get(i)<5){
                negCounter++;
            }
        }
        double prior=0;
        if (classs == "neg") {
            prior = negCounter / (double)trainstars.size();
        }else if(classs=="pos"){
            prior = (trainstars.size()-negCounter)/(double)trainstars.size();
        }
        return prior;
    }


    public void Train(){

        total_words_negative=0;
        total_words_positive=0;
            for (int i = 0; i < trainreview.size(); i++) {

                if (trainstars.get(i) < 5) {

                    for (Map.Entry<Integer, Integer> entry : trainreview.get(i).entrySet()) {
                        if (negDictionary.containsKey(entry.getKey())) {

                            int n =negDictionary.get(entry.getKey())+entry.getValue();
                            negDictionary.put(entry.getKey(), n);
                        } else {
                            negDictionary.put(entry.getKey(), entry.getValue());
                        }
                        total_words_negative+=entry.getValue();


                    }
                } else {

                    for (Map.Entry<Integer, Integer> entry : trainreview.get(i).entrySet()) {
                        if (posDictionary.containsKey(entry.getKey())) {
                            int n =posDictionary.get(entry.getKey())+entry.getValue();
                            posDictionary.put(entry.getKey(), n);
                        } else {
                            posDictionary.put(entry.getKey(), entry.getValue());
                        }
                        total_words_positive+=entry.getValue();

                    }

                }
            }
    }

public int getNegFrequency(int k){

    int frequency=0;
        for (Map.Entry<Integer, Integer> entry : negDictionary.entrySet()) {
            if(entry.getKey().equals(k)){
                frequency=entry.getValue();
            }
        }

    return frequency;
}

    public int getPosFrequency(int k){

        int frequency=0;
        for (Map.Entry<Integer, Integer> entry : posDictionary.entrySet()) {
            if(entry.getKey().equals(k)){
                frequency=entry.getValue();
            }
        }

        return frequency;
    }


public void Test(ArrayList<HashMap<Integer, Integer>> reviewfile,ArrayList<Integer> starsfile){




        if(starsfile.size()==0){
            System.out.println("No stars files");
        }
        if(reviewfile.size()==0) {
            System.out.println("There are no reviews in this file");
        }else {
            int reviewPivot=reviewfile.size()/10;
            int trReviewsize=reviewPivot;

            for (int k = 1; k < 11; k++) {
                ArrayList<Double> PosP=new ArrayList<>();
                ArrayList<Double> NegP=new ArrayList<>();
                System.out.println(" ");
                System.out.println("Data "+k+"0 %");

                for (int i = 0; i < trReviewsize; i++) {

                    double posProb = 1;
                    double negProb = 1;

                    for (Map.Entry<Integer, Integer> entry : reviewfile.get(i).entrySet()) {

                        if (negDictionary.containsKey(entry.getKey())) {
                            int freq = getNegFrequency(entry.getKey());
                            negProb *= Math.pow((((freq + 1) / ((double) total_words_negative + 89527))), entry.getValue());

                        } else {
                            negProb *= (1 / ((double) total_words_negative + 89527));
                        }

                        if (posDictionary.containsKey(entry.getKey())) {
                            int freq = getPosFrequency(entry.getKey());
                            posProb *= Math.pow((((freq + 1) / ((double) total_words_positive + 89527))), entry.getValue());


                        } else{
                            posProb *= (1 / ((double) total_words_positive + 89527));
                        }
                    }
                    PosP.add(posProb * PriorProbability("pos"));
                    NegP.add(negProb * PriorProbability("neg"));

                }

                int truePositive = 0;
                int trueNegetive = 0;
                int falsePositive = 0;
                int falseNegative = 0;

                for (int i = 0; i < trReviewsize; i++) {
                    if ((starsfile.get(i) < 5) && (NegP.get(i) > PosP.get(i))) {
                        trueNegetive++;
                    } else if ((starsfile.get(i) >= 5) && (NegP.get(i) < PosP.get(i))) {
                        truePositive++;
                    } else if ((starsfile.get(i) < 5) && (NegP.get(i) < PosP.get(i))) {
                        falseNegative++;
                    } else {
                        falsePositive++;
                    }
                }
//                System.out.println("truePositive :" + truePositive);
//                System.out.println("trueNegetive :" + trueNegetive);
//                System.out.println("falseNegative :" + falseNegative);
//                System.out.println("falsePositive :" + falsePositive);
                double accurancy = (truePositive + trueNegetive) / (double) (trueNegetive + truePositive + falseNegative + falsePositive);
                System.out.println("Accurancy: " + accurancy + " % ");
                double precision=truePositive/(double)(truePositive+falsePositive);
                System.out.println("Precision: " + precision + " % ");
                double recall=truePositive/(double)(truePositive+falseNegative);
                System.out.println("Recall: " + recall + " % ");
                double f1Score=2*((precision*recall)/(precision+recall));
                System.out.println("f1 Score: " + f1Score + " % ");

                trReviewsize=trReviewsize+reviewPivot;

                PosP=null;
                NegP=null;
            }

        }
    }

}

