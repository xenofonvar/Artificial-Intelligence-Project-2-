import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;

import static java.lang.Integer.parseInt;
public class Main extends ID3 {

    public static void main(String[] args) throws IOException {
        //parameters n,m
        int m = parseInt(args[1]);
        int n = parseInt(args[2]);
        //checks if input is allowed.
        if (args.length!=3 ) {
            System.out.print("ERROR: Variable is missing.");
            return;
        }
        //creates proper file and call Loadataset.
        File file = new File(args[0]);
        importdata.Loadataset(file, m, n);

        //after Loadataset we have every element we want, ready to call train() to train our tree.
        train();
        System.out.print("Train Accuracy: ");
        System.out.println(getTrainfinal());
        System.out.print("Develop Accuracy: ");
        System.out.println(getDevfinal());

        //Our tree is trained and we test it.
        test();
        System.out.print("Test Accuracy: ");
        System.out.println(getTestfinal());
        System.out.print("F1: ");
        System.out.println(getF1test());
        System.out.print("Precision: ");
        System.out.println(getPrecisiontest());
        System.out.print("Recall: ");
        System.out.println(getRecalltest());

        double[] yf1Data = ArrayUtils.toPrimitive(getF1test().toArray(new Double[getF1test().size()]));
        double[] ypreData = ArrayUtils.toPrimitive(getPrecisiontest().toArray(new Double[getPrecisiontest().size()]));
        double[] yrecallData = ArrayUtils.toPrimitive(getRecalltest().toArray(new Double[getRecalltest().size()]));

        double[] ytrainData = ArrayUtils.toPrimitive(getTrainfinal().toArray(new Double[getTrainfinal().size()]));
        double[] ydevData = ArrayUtils.toPrimitive(getDevfinal().toArray(new Double[getDevfinal().size()]));
        double[] xData = new double[] { 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0 };
        XYChart chart = QuickChart.getChart("Train-Dev Accuracy", "Examples Percentage", "Accuracy", "Develop", xData, ydevData);
        chart.addSeries("Train", xData, ytrainData);

        XYChart chart2 = QuickChart.getChart("F1-Precision-Recall", "Threshold", "Percentage", "F1", xData, yf1Data);
        chart2.addSeries("Precision", xData, ypreData);
        chart2.addSeries("Recall", xData, yrecallData);
        // Show charts
        new SwingWrapper(chart).displayChart();
        new SwingWrapper(chart2).displayChart();
        //save charts in project folder in high-res
        BitmapEncoder.saveBitmapWithDPI(chart, "./Train-Dev Accuracy", BitmapEncoder.BitmapFormat.PNG, 300);
        BitmapEncoder.saveBitmapWithDPI(chart2, "./F1-Precision-Recall", BitmapEncoder.BitmapFormat.PNG, 300);
    }
}

