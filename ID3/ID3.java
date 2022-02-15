import java.util.ArrayList;
import java.util.HashMap;

public class ID3 {
    //stores the vocab that we use in integers. (ex [ 1, 5, 80, 9000, 58441] ). So i know that every attribute i will check are these.
    private static ArrayList<Integer> attributes;
    //stores the training reviews
    private static ArrayList<HashMap<Integer,Integer>> examples = importdata.getTrainreview();
    //stores the development reviews
    private static ArrayList<HashMap<Integer,Integer>> dev = importdata.getDevreview();
    //training score
    private static ArrayList<Integer> score = importdata.getTrainstars();
    //Stores the accuracy of each terminated development leaf
    private static ArrayList<Double> sumfinaldev = new ArrayList<>();
    //Stores the accuracy of each terminated training leaf
    private static ArrayList<Double> sumfinaltrain = new ArrayList<>();
    //Stores the accuracy of each terminated test leaf
    private static ArrayList<Double> sumfinaltest = new ArrayList<>();
    //Stores the accuracy of 10-20-30-...-100% examples for development.
    private static ArrayList<Double> devfinal = new ArrayList<>();
    //Stores the accuracy of test.
    private static double testfinal;
    //Stores the accuracy of 10-20-30-...-100% examples for training.
    private static ArrayList<Double> trainfinal = new ArrayList<>();
    //Stores the F1 percentages. (ex. the first element is the percentage of test leafs that have under of 10% accuracy, the second is for under of 20% accuracy, etc. )
    private static ArrayList<Double> f1test = new ArrayList<>();
    //Stores the precision percentages. (ex. the first element is the percentage of test leafs that have under of 10% accuracy, the second is for under of 20% accuracy, etc. )
    private static ArrayList<Double> precisiontest = new ArrayList<>();
    //Stores the recall percentages. (ex. the first element is the percentage of test leafs that have under of 10% accuracy, the second is for under of 20% accuracy, etc. )
    private static ArrayList<Double> recalltest = new ArrayList<>();
    //Stores the first Leaf of each training example for 10-20-30-...-100% of the reviews. This help me to store every ID3 tree and then use them independently. The leaf, stores the next 2 leafs of it and the other 2 their 2 leafs etc. Finally i have every leaf of each 10-20-...-100% example.
    private static ArrayList<Leaf> AllLeafs = new ArrayList<>();
    //stores temporary the first leaf, then add it to Allleafs. It is redundant.
    private static Leaf first;
    private static Leaf devcur;
    private static int bestig;
    //These sums stores the summary of True positive, False positive and false negative of all terminated leafs for 10-20-...-100% threshold.
    private static int sumfp;
    private static int sumtp;
    private static int sumfn;
    //These array stores all the True positive, False positive and false negative elements of all leafs.
    private static ArrayList<Integer> FN = new ArrayList<>();
    private static ArrayList<Integer> TP = new ArrayList<>();
    private static ArrayList<Integer> FP = new ArrayList<>();
    //Accessors
    public static ArrayList<Double> getDevfinal(){ return devfinal;}
    public static ArrayList<Double> getTrainfinal(){ return trainfinal;}
    public static double getTestfinal(){ return testfinal;}
    public static ArrayList<Double> getF1test(){ return f1test;}
    public static ArrayList<Double> getPrecisiontest(){ return precisiontest;}
    public static ArrayList<Double> getRecalltest(){ return recalltest;}

    //Method ID3 for training and development
    public static void ID3(ArrayList<Integer> vocab, Integer category, Leaf current, Leaf devcur) {
        //If the development leaf frequency of positive or negative reviews is beyond 85% terminate.
        if (devcur!=null && (freq(1, devcur.getScore()) >= 0.85 || freq(0, devcur.getScore()) >= 0.85)) {
            devcur.setValue(category);
            double ac = accuracy(devcur, false);
            if(ac!=0){ sumfinaldev.add(ac);}
            devcur = null;
        }
        //If the training leaf frequency of positive or negative reviews is beyond 85% terminate or if the ig didn't find any word.
        if (current.getEx().isEmpty() || vocab.isEmpty() || freq(1, current.getScore()) >= 0.85 || freq(0, current.getScore()) >= 0.85 || ig(current.getEx(), vocab, current.getScore())==0) {
            double ac = accuracy(current, false);
            if(ac!=0){ sumfinaltrain.add(ac);}
            if(!(devcur==null)){
                devcur.setValue(category);
                double ac2 = accuracy(devcur, false);
                if(ac!=0){ sumfinaldev.add(ac2);}
            }
            return;
        }

        ArrayList<Integer> vocab1 = vocab;
        //find the best word
        bestig = ig(current.getEx(), vocab, current.getScore());
        //remove it from vocab so it will not get checked again.
        for(int i=0; i<vocab1.size(); i++){
            if(vocab.get(i) == bestig){
                vocab1.remove(i);
            }
        }
        //Arrays that help me to store proper elements.
        ArrayList<HashMap<Integer, Integer>> example1 = new ArrayList<>();
        ArrayList<HashMap<Integer, Integer>> example0 = new ArrayList<>();
        ArrayList<HashMap<Integer, Integer>> exampledev1 = new ArrayList<>();
        ArrayList<HashMap<Integer, Integer>> exampledev0 = new ArrayList<>();
        ArrayList<Integer> score1 = new ArrayList<>();
        ArrayList<Integer> score0 = new ArrayList<>();
        ArrayList<Integer> scoredev1 = new ArrayList<>();
        ArrayList<Integer> scoredev0 = new ArrayList<>();
        //Check the best word in the training examples. Separate the reviews and scores.
        for (int j = 0; j < current.getEx().size(); j++) {
            if (current.getEx().get(j).containsKey(bestig)) {
                example1.add(current.getEx().get(j));
                score1.add(current.getScore().get(j));
            }else{
                example0.add(current.getEx().get(j));
                score0.add(current.getScore().get(j));
            }
        }
        //Create 2 Leafs. One for the reviews that have the best word and another for the rest.
        Leaf origin0 = new Leaf(bestig, 0, checkcategory(score0));
        Leaf origin1 = new Leaf(bestig, 1, checkcategory(score1));
        origin0.setScore(score0);
        origin1.setScore(score1);
        origin0.setEx(example0);
        origin1.setEx(example1);
        //Set the next leafs of current training leaf.
        current.setRight(origin0, 0);
        current.setRight(origin1, 1);
        //If the development leaf exists, because maybe development elements finish before the training.
        if(devcur!=null) {
            for (int n = 0; n < devcur.getEx().size(); n++) {
                if (devcur.getEx().get(n).containsKey(bestig)) {
                    exampledev1.add(devcur.getEx().get(n));
                    scoredev1.add(devcur.getScore().get(n));
                }else {
                    exampledev0.add(devcur.getEx().get(n));
                    scoredev0.add(devcur.getScore().get(n));
                }
            }
            //Same as the training method for the leafs
            Leaf origindev0 = new Leaf(bestig, 0, checkcategory(score0));
            Leaf origindev1 = new Leaf(bestig, 1, checkcategory(score1));
            origindev0.setScore(scoredev0);
            origindev1.setScore(scoredev1);
            origindev0.setEx(exampledev0);
            origindev1.setEx(exampledev1);
            devcur.setRight(origindev0,0);
            devcur.setRight(origindev1,1);
            ID3(vocab1, checkcategory(score1), origin1, origindev1);
            ID3(vocab1, checkcategory(score0), origin0, origindev0);
        }else{
            ID3(vocab1, checkcategory(score1), origin1, null);
            ID3(vocab1, checkcategory(score0), origin0, null);
        }
    }
    //ID3 method for testing
    public static void ID3test(int attr, Leaf current, Leaf test) {
        //The only difference from the previous method is the last parameter which checks if the next training leaf is null.
        if (test.getEx().isEmpty() || freq(1, test.getScore()) >= 0.85 || freq(0, test.getScore()) >= 0.85 || (current.getRight(0)==null && current.getRight(1)==null)) {
            double ac = accuracy(test, true);
            if(ac!=0){ sumfinaltest.add(ac);}
            return;
        }
        ArrayList<HashMap<Integer, Integer>> exampledtest1 = new ArrayList<>();
        ArrayList<HashMap<Integer, Integer>> exampledtest0 = new ArrayList<>();
        ArrayList<Integer> scoretest1 = new ArrayList<>();
        ArrayList<Integer> scoretest0 = new ArrayList<>();
        //Same as the training
        for (int n = 0; n < test.getEx().size(); n++) {
            if (test.getEx().get(n).containsKey(attr)) {
                exampledtest1.add(test.getEx().get(n));
                scoretest1.add(test.getScore().get(n));
            } else {
                exampledtest0.add(test.getEx().get(n));
                scoretest0.add(test.getScore().get(n));
            }
        }
        //These are the processes for the reviews that do not have the best word that we was check before.
        Leaf current0 = current.getRight(0);
        int bestig = current0.getTag();
        Leaf origintest0 = new Leaf(attr, 0, current0.getSign());
        origintest0.setScore(scoretest0);
        origintest0.setEx(exampledtest0);
        //recursive call
        ID3test(bestig, current0 , origintest0);


        //These are for the reviews that have the best word.
        Leaf current1 = current.getRight(1);
        bestig = current1.getTag();
        Leaf origintest1 = new Leaf(bestig, 1, current1.getSign());
        origintest1.setScore(scoretest1);
        origintest1.setEx(exampledtest1);
        //recursive call
        ID3test(bestig, current1, origintest1);
    }

    //Test method. Do the necessary processes for the right testing
    public static void test() {
        double max = 0;
        int position = 0;
        //Check and get the best training review percentage, according the best development accuracy.
        for(int i=0; i<devfinal.size(); i++){
            if(devfinal.get(i)>max){
                max = devfinal.get(i);
                position = i;
            }
        }
        //first leaf of test
        Leaf testleaf = new Leaf(AllLeafs.get(position).getTag(), AllLeafs.get(position).getValue(), AllLeafs.get(position).getSign());
        testleaf.setScore(importdata.getTeststars());
        testleaf.setEx(importdata.getTestreview());
        ID3test(AllLeafs.get(position).getTag(), AllLeafs.get(position), testleaf);
        //sums the accuracy of each terminated leaf.
        double sum = 0;
        for(int s=0; s<sumfinaltest.size(); s++){
            sum += sumfinaltest.get(s);
        }
        sum = sum/sumfinaltest.size();
        testfinal = sum;
        //For 10-20-...-100% threshold find recall, precision and f1.
        for(int i=0; i<10; i++) {
            recalltest.add(recall(i+1) * 100);
            precisiontest.add(precision() * 100);
            f1test.add(f1(recalltest.get(i), precisiontest.get(i)));
        }
    }
    //train method
    public static void train() {
        int category;
        int limit;
        ArrayList<HashMap<Integer,Integer>> tempex;
        ArrayList<Integer> tempsc;
        //Runs the same processes for the 10-20-...-100% of the training reviews. So, it can find the accuracy curve for each example
        for(int k=1; k<11; k++) {
            tempex = new ArrayList<>(examples);
            tempsc = new ArrayList<>(score);
            sumfinaltrain.clear();
            sumfinaldev.clear();
            int exsize = examples.size()-1;
            //is the limit of remover. Stops when the tempex and tempsc has the right number of elements. 10-20-...-100% of elements
            limit = (int)(examples.size()*(k*1.0/(10*1.0)))-1;
            while(exsize > limit){
                tempex.remove(exsize);
                tempsc.remove(exsize);

                exsize--;
            }
            //vocab
            attributes = vocabprim(tempex);
            //category of reviews. negative or positive
            category = checkcategory(tempsc);
            //Same as the test, but double because here, we are running for the training and development.
            first = new Leaf(ig(tempex, attributes, tempsc), 0, category);
            devcur = new Leaf(ig(tempex, attributes, tempsc), 0, category);
            devcur.setScore(importdata.getDevstars());
            devcur.setEx(dev);
            first.setEx(tempex);
            first.setScore(tempsc);
            ID3(attributes, category, first, devcur);
            AllLeafs.add(first);
            double sum = 0;
            for(int s=0; s<sumfinaldev.size(); s++){
                sum += sumfinaldev.get(s);
            }
            sum = sum/sumfinaldev.size();
            devfinal.add(sum);
            sum=0;
            for(int s=0; s<sumfinaltrain.size(); s++){
                if(sumfinaltrain!=null)
                sum += sumfinaltrain.get(s);
            }
            sum = sum/(sumfinaltrain.size()*1.0);
            trainfinal.add(sum);
        }
    }
    //Accuracy method.
    public static double accuracy(Leaf fine , boolean flag){
        int pos = 0;
        int neg = 0;
        int TPtemp = 0;
        int FNtemp = 0;
        int FPtemp = 0;

        //for each score
        for (int i=0; i<fine.getScore().size(); i++) {
            if (fine.getScore().get(i) > 4) {
                //if it is positive and we want to calculate the True positive, etc.
                if(flag) {
                    if (fine.getSign() == 1) {
                        TPtemp++;
                    } else {
                        FNtemp++;
                    }
                }
                pos++;
            } else {
                if(flag) {
                    if (fine.getSign() == 1) {
                        FPtemp++;
                    }
                }
                neg++;
            }
        }
        //flag knows if we want to calculate true positive, etc..
        if(flag) {
            TP.add(TPtemp);
            FP.add(FPtemp);
            FN.add(FNtemp);
        }
        //if the sign is positive, essentially if the category is positive.
        if (fine.getSign()==1) {
            if(fine.getScore().size()!=0) {
                return ((pos * 1.0) / (fine.getScore().size() * 1.0)) * 100;
            }else
                return 0;
        } else {
            if(fine.getScore().size()!=0) {
                return ((neg * 1.0) / (fine.getScore().size() * 1.0)) * 100;
            }else{
                return 0;
            }
        }
    }
    //Frequency of positive and negative reviews. We just check the score.
    public static double freq(int who, ArrayList<Integer> score) {
        int pos = 0;
        int neg = 0;

        for (int i=0; i<score.size(); i++) {
            if (score.get(i) > 4) {
                pos++;
            } else {
                neg++;
            }
        }
        if (who == 1) {
            return (pos * 1.0) / (score.size() * 1.0);
        } else {
            return (neg * 1.0) / (score.size() * 1.0);
        }
    }
    //Conditional frequency of a word.
    public static double freqgiven(ArrayList<HashMap<Integer,Integer>> examples, ArrayList<Integer> score, int who, int atr, int given) {
        int pos11 = 0;
        int pos00 = 0;
        int pos01 = 0;
        int pos10 = 0;
        int pos = 0;
        int neg;

        for (int i = 0; i < examples.size(); i++) {
            if (examples.get(i).containsKey(atr)) {
                //sum of reviews who have the word we are looking for.
                pos++;
                if (given == 1) {
                    if (who == 1 && score.get(i) > 4) {
                        //sum of reviews that who the word we are looking for and they are positive
                        pos11++;
                    } else if (who == 0 && score.get(i) < 5) {
                        //sum of reviews that have the word we are looking for and they are negative
                        pos10++;
                    }
                }
            } else {
                if (given == 0) {
                    if (who == 1) {
                        //sum of reviews who don't have the word we are looking for and they are positive
                        pos01++;
                    } else if (who == 0) {
                        //sum of reviews who don't have the word we are looking for and they are negative.
                        pos00++;
                    }
                }
            }
        }
        //sum of reviews who don't have the word we are looking for.
        neg = examples.size() - pos;

        if (who == 1 && given == 1) {
            return (pos11 * 1.0) / (pos * 1.0);
        } else if (who == 0 && given == 1) {
            return (pos10 * 1.0) / (pos * 1.0);
        } else if (who == 1 && given == 0) {
            return (pos01 * 1.0) / (neg * 1.0);
        } else{
            return (pos00 * 1.0) / (neg * 1.0);
        }
    }
    //Frequency of negative or positive reviews who have a word or not.
    public static double freqword(ArrayList<HashMap<Integer,Integer>> examples, int atr, int given) {
        int pos = 0;

        for (int i = 0; i < examples.size(); i++) {
                if (examples.get(i).containsKey(atr)) {
                    pos++;
                }
        }
        if(given == 0){
            pos = examples.size() - pos;
        }
        return (pos * 1.0) / (examples.size() * 1.0);
    }
    //check the category of an example.
    public static int checkcategory(ArrayList<Integer> score){
        if(freq(1, score) > freq(0, score)){
            return 1;
        }else{
            return 0;
        }
    }

    //entropy
    public static double entropy(ArrayList<Integer> score){
        double fr1 = freq(1, score);
        double fr0 = freq(0, score);
        double entr = -fr1 * (Math.log(fr1)/Math.log(2)) - fr0 * (Math.log(fr0)/Math.log(2));
        return entr;
    }
    //conditional entropy
    public static double entropygiven(ArrayList<HashMap<Integer,Integer>> examples, int given, int attr, ArrayList<Integer> score){
        double fr1 = freqgiven(examples, score, 1, attr, given);
        double fr0 = freqgiven(examples, score, 0, attr, given);
        double entr = -fr1 * (Math.log(fr1)/Math.log(2)) - fr0 * (Math.log(fr0)/Math.log(2));
        return entr;
    }
    //Information gain
    public static int ig(ArrayList<HashMap<Integer,Integer>> examples, ArrayList<Integer> vocab, ArrayList<Integer> score){
        double entropy = entropy(score);
        double entropy1;
        double entropy0;
        double ig;
        //random
        double igmax = -10^8;
        int maxpos = 0;
        double fr1;
        double fr2;
        for(int j=0; j<vocab.size(); j++){
            fr1 = freqword(examples, vocab.get(j), 0);
            fr2 = 1-fr1;
            entropy1 = entropygiven(examples, 1, vocab.get(j), score);
            entropy0 = entropygiven(examples, 0, vocab.get(j), score);
            ig = entropy - (fr1*entropy1 + fr2*entropy0);
            //stores the best ig and the position of it in the array.
            if(ig>igmax) {
                igmax = ig;
                maxpos = vocab.get(j);
            }
        }
        return maxpos;
    }
    //This method find the vocabulary of each example. I created this because all words of the general vocab is redundant. So this method stores only the words that exists in the reviews. One way or another only these words we will be called to consider.
    public static ArrayList<Integer> vocabprim(ArrayList<HashMap<Integer,Integer>> examples){
        ArrayList<Integer> voc = new ArrayList<>();
        for(int j=0; j<examples.size(); j++){
            for(Integer i : examples.get(j).keySet()){
                if(!voc.contains(i)){
                    voc.add(i);
                }
            }
        }
        return voc;
    }
    //recall method. Also finds the sum of False positive, etc. once. So we will not get into for loops again.
    public static double recall(int number){
        double rec = 0;
        sumtp = 0;
        sumfn = 0;
        sumfp = 0;
        for(int i=0; i<sumfinaltest.size(); i++){
            if(sumfinaltest.get(i)*1.0<number*10.0){
                sumtp += TP.get(i);
                sumfp += FP.get(i);
                sumfn += FN.get(i);
            }
        }
        rec = sumtp*1.0 / (sumfn*1.0+sumtp*1.0);
        return rec;
    }
    //Precision method.
    public static double precision(){
        double pre;
        pre = sumtp*1.0 / (sumfp*1.0+sumtp*1.0);
        if(pre>0) {
            return pre;
        }else{
            pre = 0.0;
            return pre;
        }
    }
    //F1 method
    public static double f1(double pre,double rec){
         return 2*((pre*1.0)*(rec*1.0)/((pre*1.0)+(rec*1.0)));
    }
}