import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.lang.String;
import static java.lang.Integer.parseInt;

public class importdata {
	//keeps the training review
	private static ArrayList<HashMap<Integer,Integer>> trainreview = new ArrayList<>();
	//development review, which is 10% of the training
	private static ArrayList<HashMap<Integer,Integer>> devreview = new ArrayList<>();
	//test review
	private static ArrayList<HashMap<Integer,Integer>> testreview = new ArrayList<>();
	//training scores for each review
	private static ArrayList<Integer> trainstars = new ArrayList<Integer>();
	//development scores for each review
	private static ArrayList<Integer> devstars = new ArrayList<Integer>();
	//test scores for each review
	private static ArrayList<Integer> teststars = new ArrayList<Integer>();
	//this bool help me to understand if the scanner reading data has entered training folder.
	private static boolean intrain = false;
	//respectively as the intrain boolean the intest do the same for test folder.
	private static boolean intest = false;
	//Accessors
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

	//Loadataset method
	public static void Loadataset(File path, int m, int n) {
		int tempstar = 0;
		File[] database = path.listFiles();
		if (database != null) {
			//for each element in to the database
			for (File p : database) {
				//if the file is the labeledBow start reading.
				if (p.isFile() && p.getName().equalsIgnoreCase("labeledBow.feat")) {
					Scanner scan2 = null;
					try {
						scan2 = new Scanner(p);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					//for each line of the labeledbow
					while (scan2.hasNextLine()) {
						String line = scan2.nextLine();
						Scanner scanline = new Scanner(line);
						HashMap<Integer,Integer> temp = new HashMap<>();
						//keeps the first element of the line which is the score of each review
						tempstar = parseInt(scanline.next());
						//for each pair element of the line (ex. 12344:2 )
						while (scanline.hasNext()) {
							//split the ( ex. 12344 ) from the ( ex. 2 )
							String[] word = scanline.next().split(":");
							//if we are in the training folder then...
							if (intrain) {
								//if the first seperated element (ex. 12344 ) is within the limits, then put it and the second seperated too in a temporary hashmap.
								if(parseInt(word[0])<m && parseInt(word[0])>n-1) {
									temp.put(parseInt(word[0]), parseInt(word[1]));
								}
							}
							//if we are in the test folder then...
							if (intest) {
								//if the first seperated element (ex. 12344 ) is within the limit, then put it and the second seperated too in a temporary hashmap.
								if(parseInt(word[0])<m && parseInt(word[0])>n-1) {
									temp.put(parseInt(word[0]), parseInt(word[1]));
								}
							}
						}
						if(intrain) {
							//if the hashmap is not empty and we are in the training folder, essentially if the selected word is within the limits.
							if (!temp.isEmpty()) {
								trainreview.add(temp);
								trainstars.add(tempstar);
							}
						}
						if(intest) {
							//if the hashmap is not empty and we are in the test folder, essentially if the selected word is within the limits.
							if(!temp.isEmpty()){
								testreview.add(temp);
								teststars.add(tempstar);
							}
						}
					}
					if(intrain) {
						//shuffling the array of training review and score.
						random(trainreview, trainstars);
						//seperate 10% of train reviews for development
						int k = trainreview.size()-1;
						int limit = (int)(trainreview.size()*0.9)-1;
						while(k>limit){
							devreview.add(trainreview.get(k));
							devstars.add(trainstars.get(k));
							trainreview.remove(k);
							trainstars.remove(k);
							k--;
						}
						//then invert the data, because the splitter removes the data from the end of the training array and put them in to the development array in reverse order
						Collections.reverse(devreview);
						Collections.reverse(devstars);
					}
					intest = false;
					intrain = false;
				//if the file is directory
				}else if (p.isDirectory()) {
					//change intrain bool to true, because we are in the train folder now.
					if (p.getName().equalsIgnoreCase("train")) {
						intrain = true;
					}
					if (p.getName().equalsIgnoreCase("test")) {
						intest = true;
					}
					//recursive call of Loadateset
					Loadataset(p, m, n);
				}
			}
			//shuffling the test review and score data.
			random(testreview, teststars);
		} else {
			System.out.println("Couldn't find any review.");
		}
	}
	//Random method for shuffling the arrays
	public static void random(ArrayList<HashMap<Integer,Integer>> examples, ArrayList<Integer> score){
		Random rand = new Random();
		for (int i = 0; i < examples.size(); i++) {
			//Store a random integer
			int randomIndexToSwap = rand.nextInt(examples.size());
			//Swap the the training reviews
			HashMap<Integer,Integer> temp = examples.get(randomIndexToSwap);
			examples.set(randomIndexToSwap, examples.get(i));
			examples.set(i, temp);
			//Also swap the score, so in the end in the same position the corresponding data.
			Integer temp2 = score.get(randomIndexToSwap);
			score.set(randomIndexToSwap, score.get(i));
			score.set(i, temp2);

		}
	}
}