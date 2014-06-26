import java.awt.Font;
import java.awt.Dimension;
import java.util.Date;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


import javax.swing.JPanel;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.date.DateUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
/**
 * A demo for the {@link CombinedDomainCategoryPlot} class.
 */
public class democombine extends ApplicationFrame {

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public democombine(final String title) {

        super(title);

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(createChart());
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Creates a dataset.
     *
     * @return A dataset.
     */
		public static OHLCDataset createDataset1()
    {
        Date adate[] = new Date[27];
        double ad[] = new double[27];
        double ad1[] = new double[27];
        double ad2[] = new double[27];
        double ad3[] = new double[27];
        double ad4[] = new double[27];
        int i = 12;
        int ii = 1;
        //byte byte0 = 1;
        //|hi|lo|open|close|
        adate[0] = DateUtilities.createDate(2001, i, 4, 12, 0);
        ad[0] = 47D;
        ad1[0] = 33D;
        ad2[0] = 35D;
        ad3[0] = 33D;
        ad4[0] = 100D;
        adate[1] = DateUtilities.createDate(2001, i, 5, 12, 0);
        ad[1] = 47D;
        ad1[1] = 32D;
        ad2[1] = 41D;
        ad3[1] = 37D;
        ad4[1] = 150D;
        adate[2] = DateUtilities.createDate(2001, i, 6, 12, 0);
        ad[2] = 49D;
        ad1[2] = 43D;
        ad2[2] = 46D;
        ad3[2] = 48D;
        ad4[2] = 70D;
        adate[3] = DateUtilities.createDate(2001, i, 7, 12, 0);
        ad[3] = 51D;
        ad1[3] = 39D;
        ad2[3] = 40D;
        ad3[3] = 47D;
        ad4[3] = 200D;
        adate[4] = DateUtilities.createDate(2001, i, 8, 12, 0);
        ad[4] = 60D;
        ad1[4] = 40D;
        ad2[4] = 46D;
        ad3[4] = 53D;
        ad4[4] = 120D;
        adate[5] = DateUtilities.createDate(2001, i, 11, 12, 0);
        ad[5] = 62D;
        ad1[5] = 55D;
        ad2[5] = 57D;
        ad3[5] = 61D;
        ad4[5] = 110D;
        adate[6] = DateUtilities.createDate(2001, i, 12, 12, 0);
        ad[6] = 65D;
        ad1[6] = 56D;
        ad2[6] = 62D;
        ad3[6] = 59D;
        ad4[6] = 70D;
        adate[7] = DateUtilities.createDate(2001, i, 13, 12, 0);
        ad[7] = 55D;
        ad1[7] = 43D;
        ad2[7] = 45D;
        ad3[7] = 47D;
        ad4[7] = 20D;
        adate[8] = DateUtilities.createDate(2001, i, 14, 12, 0);
        ad[8] = 54D;
        ad1[8] = 33D;
        ad2[8] = 40D;
        ad3[8] = 51D;
        ad4[8] = 30D;
        adate[9] = DateUtilities.createDate(2001, i, 15, 12, 0);
        ad[9] = 47D;
        ad1[9] = 33D;
        ad2[9] = 35D;
        ad3[9] = 33D;
        ad4[9] = 100D;
        adate[10] = DateUtilities.createDate(2001, i, 18, 12, 0);
        ad[10] = 54D;
        ad1[10] = 38D;
        ad2[10] = 43D;
        ad3[10] = 52D;
        ad4[10] = 50D;
        adate[11] = DateUtilities.createDate(2001, i, 19, 12, 0);
        ad[11] = 48D;
        ad1[11] = 41D;
        ad2[11] = 44D;
        ad3[11] = 41D;
        ad4[11] = 80D;
        adate[12] = DateUtilities.createDate(2001, i, 20, 12, 0);
        ad[12] = 60D;
        ad1[12] = 30D;
        ad2[12] = 34D;
        ad3[12] = 44D;
        ad4[12] = 90D;
        adate[13] = DateUtilities.createDate(2001, i, 21, 12, 0);
        ad[13] = 58D;
        ad1[13] = 44D;
        ad2[13] = 54D;
        ad3[13] = 56D;
        ad4[13] = 20D;
        adate[14] = DateUtilities.createDate(2001, i, 22, 12, 0);
        ad[14] = 54D;
        ad1[14] = 32D;
        ad2[14] = 42D;
        ad3[14] = 53D;
        ad4[14] = 70D;
        adate[15] = DateUtilities.createDate(2001, i, 25, 12, 0);
        ad[15] = 53D;
        ad1[15] = 39D;
        ad2[15] = 50D;
        ad3[15] = 49D;
        ad4[15] = 60D;
        adate[16] = DateUtilities.createDate(2001, i, 26, 12, 0);
        ad[16] = 47D;
        ad1[16] = 33D;
        ad2[16] = 41D;
        ad3[16] = 40D;
        ad4[16] = 30D;
        adate[17] = DateUtilities.createDate(2001, i, 27, 12, 0);
        ad[17] = 55D;
        ad1[17] = 37D;
        ad2[17] = 43D;
        ad3[17] = 45D;
        ad4[17] = 90D;
        adate[18] = DateUtilities.createDate(2001, i, 28, 12, 0);
        ad[18] = 54D;
        ad1[18] = 42D;
        ad2[18] = 50D;
        ad3[18] = 42D;
        ad4[18] = 150D;
        adate[19] = DateUtilities.createDate(2001, i, 29, 12, 0);
        ad[19] = 48D;
        ad1[19] = 37D;
        ad2[19] = 37D;
        ad3[19] = 47D;
        ad4[19] = 120D;
        adate[20] = DateUtilities.createDate(2002, ii, 2, 12, 0);
        ad[20] = 58D;
        ad1[20] = 33D;
        ad2[20] = 39D;
        ad3[20] = 41D;
        ad4[20] = 80D;
        adate[21] = DateUtilities.createDate(2002, ii, 3, 12, 0);
        ad[21] = 47D;
        ad1[21] = 31D;
        ad2[21] = 36D;
        ad3[21] = 41D;
        ad4[21] = 40D;
        adate[22] = DateUtilities.createDate(2002, ii, 4, 12, 0);
        ad[22] = 58D;
        ad1[22] = 44D;
        ad2[22] = 49D;
        ad3[22] = 44D;
        ad4[22] = 20D;
        adate[23] = DateUtilities.createDate(2002, ii, 5, 12, 0);
        ad[23] = 46D;
        ad1[23] = 41D;
        ad2[23] = 43D;
        ad3[23] = 44D;
        ad4[23] = 60D;
        adate[24] = DateUtilities.createDate(2002, ii, 8, 12, 0);
        ad[24] = 56D;
        ad1[24] = 39D;
        ad2[24] = 39D;
        ad3[24] = 51D;
        ad4[24] = 40D;
        adate[25] = DateUtilities.createDate(2002, ii, 9, 12, 0);
        ad[25] = 56D;
        ad1[25] = 39D;
        ad2[25] = 47D;
        ad3[25] = 49D;
        ad4[25] = 70D;
        adate[26] = DateUtilities.createDate(2002, ii, 10, 12, 0);
        ad[26] = 53D;
        ad1[26] = 39D;
        ad2[26] = 52D;
        ad3[26] = 47D;
        ad4[26] = 60D;
     
       
        return new DefaultHighLowDataset("Series 1", adate, ad, ad1, ad2, ad3, ad4);
    }
    /**
     * Creates a dataset.
     *
     * @return A dataset.
     */
    public CategoryDataset createDataset2() {

        final DefaultCategoryDataset result = new DefaultCategoryDataset();

        // row keys...
        final String series1 = "Third";
        final String series2 = "Fourth";

        // column keys...
        final String type1 = "Type 1";
        final String type2 = "Type 2";
        final String type3 = "Type 3";
        final String type4 = "Type 4";
        final String type5 = "Type 5";
        final String type6 = "Type 6";
        final String type7 = "Type 7";
        final String type8 = "Type 8";

        result.addValue(11.0, series1, type1);
        result.addValue(14.0, series1, type2);
        result.addValue(13.0, series1, type3);
        result.addValue(15.0, series1, type4);
        result.addValue(15.0, series1, type5);
        result.addValue(17.0, series1, type6);
        result.addValue(17.0, series1, type7);
        result.addValue(18.0, series1, type8);

        result.addValue(15.0, series2, type1);
        result.addValue(17.0, series2, type2);
        result.addValue(16.0, series2, type3);
        result.addValue(18.0, series2, type4);
        result.addValue(14.0, series2, type5);
        result.addValue(14.0, series2, type6);
        result.addValue(12.0, series2, type7);
        result.addValue(11.0, series2, type8);

        return result;

    }

   
    /**
     * Creates a chart.
     *
     * @return A chart.
     */
    public JFreeChart createChart() {

				// 產生一個子plot
        OHLCDataset dataset1 = createDataset1();
        XYPlot subplot1 = new XYPlot(dataset1, new NumberAxis("Volume"), null);
          
        // 產生第二個子plot
				//CategoryDataset dataset2 = this.createDataset2();
				//XYPlot subplot2 = new XYPlot(dataset2, new NumberAxis("Volume"), null);

        // 產生一個主plot
				CombinedXYPlot plot = new CombinedXYPlot(new DateAxis("Date"));
        plot.add(subplot1, 2);
        //plot.add(subplot2, 1);
        
        JFreeChart result = new JFreeChart(
            "Combined Domain Category Plot Demo",
            new Font("SansSerif", Font.BOLD, 12),
            plot,
            true
        );
  //      result.getLegend().setAnchor(Legend.SOUTH);
        return result;

			
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final String title = "Combined Category Plot Demo 1";
        final democombine demo = new democombine(title);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
