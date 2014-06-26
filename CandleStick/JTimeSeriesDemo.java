import java.awt.*; 
import javax.swing.ImageIcon;      //ImageIcon, jfreechart 0.9.21 
import java.awt.Image; 
import org.jfree.chart.annotations.XYImageAnnotation; 
import org.jfree.data.time.Day; 
import org.jfree.data.time.Month; 
import org.jfree.chart.axis.DateTickMarkPosition; 
import java.io.File; 
import java.io.FileOutputStream; 
import org.jfree.chart.ChartUtilities; 

import java.awt.Color; 
import java.text.SimpleDateFormat; 
import org.jfree.chart.ChartFactory; 
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
//#import org.jfree.chart.Spacer;                     //(Deprecated)jfreechart 0.9.16 
import org.jfree.chart.axis.AxisSpace;                  //(unused class)jfreechart 0.9.21 
//import org.jfree.chart.StandardLegend; 
import org.jfree.chart.axis.DateAxis; 
import org.jfree.chart.axis.ValueAxis; 
import org.jfree.chart.plot.XYPlot; 
//#import org.jfree.chart.renderer.StandardXYItemRenderer;   //jfreechart 0.9.16 
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;   //jfreechart 0.9.21 
//#import org.jfree.chart.renderer.XYItemRenderer;         //jfreechart 0.9.16 
import org.jfree.chart.renderer.xy.XYItemRenderer;         //jfreechart 0.9.21    
//#import org.jfree.data.XYDataset;                     //jfreechart 0.9.16 
import org.jfree.data.xy.XYDataset;                     //jfreechart 0.9.21 
import org.jfree.data.time.Month;             
import org.jfree.data.time.TimeSeries; 
import org.jfree.data.time.TimeSeriesCollection; 
import org.jfree.ui.ApplicationFrame; 
import org.jfree.ui.RefineryUtilities; 

import org.jfree.chart.ChartMouseEvent; 
import org.jfree.chart.ChartMouseListener; 

public class JTimeSeriesDemo extends ApplicationFrame 
                     implements ChartMouseListener{ 
                         
   private int m_iChartWidth = 800; 
   private int m_iChartHeight = 400; 
    
   public static ChartPanel chartPanel; 
   private JFreeChart chart; 
   private XYPlot plot; 
   private XYPlot xyplot; 

   /** 
     * Receives chart mouse click events. 
     * 
     * @param event  the event. 
     */ 
     public void chartMouseClicked(ChartMouseEvent event){ 
       } 
    
   /** 
     * Receives chart mouse Pressed events. 
     * 
     * @param event  the event. 
     */ 
     public void chartMousePressed(ChartMouseEvent event){ 
     } 
    
    /** 
     * Receives chart mouse dragged events. 
     * 
     * @param event  the event. 
     */ 
     public void chartMouseDragged(ChartMouseEvent event){ 
     } 
      
    /** 
     * Receives chart mouse Released events. 
     * 
     * @param event  the event. 
     */ 
     public void chartMouseReleased(ChartMouseEvent event){ 
     } 
    
    /** 
     * Receives chart mouse moved events. 
     * 
     * @param event  the event. 
     */ 
     public void chartMouseMoved(ChartMouseEvent event){ 
        //ignore 
        System.out.println("moved..."); 
     } 
      
    /** 
     * Delete all annotation 
     * 
     * @param  
     */ 
     public void clearAnnotations(){ 
     } 
    
   public JTimeSeriesDemo(String title) { 
      super(title); 
      XYDataset dataset = createDataset(); 
      JFreeChart chart = createChart(dataset); 
      ChartPanel chartPanel = new ChartPanel(chart); 
      chartPanel.addChartMouseListener(this); //wei wei 
      chartPanel.setMouseZoomable(true,false); 
      chartPanel.setPreferredSize(new java.awt.Dimension(m_iChartWidth, m_iChartHeight)); 
      chartPanel.setMouseZoomable(true, false); 
          
      setContentPane(chartPanel); 
      genPNG(chart);   //generate PNG file 
   } 
    
   private void genPNG(JFreeChart chart){ 
      String sChartName = "IntradayChart"; 
      String sChartType = ".PNG"; 
      File fFile = new File("C:\\ant\\img\\ArrowInChart.PNG"); 
      try{          
         FileOutputStream fout = new FileOutputStream(fFile); 
         ChartUtilities.writeChartAsPNG (fout, chart, m_iChartWidth, m_iChartHeight); 
         fout.close(); 
      }catch(Exception exc){ 
         System.out.println("ErrorCode[ABT001]:exc=["+exc+"]"); 
      }       
   } 
    
private JFreeChart createChart(XYDataset dataset) { 
   JFreeChart chart = ChartFactory.createTimeSeriesChart( 
   "Bursa Malaysia Index Comparison", 
   "Date", "Percentage %", 
   dataset, 
   true, 
   true, 
   false 
   ); 
    
   chart.setBackgroundPaint(Color.white); 
       
//   StandardLegend sl = (StandardLegend) chart.getLegend(); 
//   sl.setDisplaySeriesShapes(true); 
   XYPlot plot = chart.getXYPlot(); 
   //#plot.setBackgroundPaint(Color.lightGray); 
   plot.setBackgroundPaint(Color.black); 
   plot.setDomainGridlinePaint(Color.white); 
   plot.setRangeGridlinePaint(Color.white); 
    
   //===================================================================== 
   //@@ Modify as Spacer class had deprecated.Version(0.9.16->0.9.21) 
   //#plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0)); 
   //===================================================================== 
       
   //================================================= 
   //@@ set Watermark Background Image 
   //@@ Bind the watermark for the background of white space. 
//$   ImageIcon icon = new ImageIcon("img/arrowUp2.PNG"); 
//$   Image image = icon.getImage(); 
//$   plot.setBackgroundImage(image); 
//------------------------------------------------------------------------- 
//@@ Plz prepare an image (any thing and store in img folder on the same level with classes folder) 
   double dArrowTime = 0; 
   double dArrowTime2 = 0; 
   double dArrowTime3 = 0; 
   XYImageAnnotation xyimageannotation,xyimageannotation2,xyimageannotation3; 
   ImageIcon icArrowUp = new ImageIcon("img/arrowUp.PNG"); 
   ImageIcon icArrowDw = new ImageIcon("img/arrowDw.PNG"); 
   ImageIcon icArrowSt = new ImageIcon("img/arrowSt.PNG"); 
   Image imgArrowUp = icArrowUp.getImage(); 
   Image imgArrowDw = icArrowDw.getImage(); 
   Image imgArrowSt = icArrowSt.getImage(); 
   //double dArrowTime = (new Day(9, 9, 2004)).getMiddleMillisecond(); 
   dArrowTime = (new Month(7, 2004)).getMiddleMillisecond(); 
   xyimageannotation = new XYImageAnnotation(dArrowTime, -5.0, imgArrowUp); 
    plot.addAnnotation(xyimageannotation); 
        
   dArrowTime2 = (new Month(11, 2004)).getMiddleMillisecond(); 
   xyimageannotation2 = new XYImageAnnotation(dArrowTime2, 9.0, imgArrowDw); 
    plot.addAnnotation(xyimageannotation2); 
    
   dArrowTime3= (new Month(5, 2004)).getMiddleMillisecond(); 
   xyimageannotation3 = new XYImageAnnotation(dArrowTime3, 2.5, imgArrowSt); 
    plot.addAnnotation(xyimageannotation3); 
   //=================================================== 
       
   plot.setDomainCrosshairVisible(true); 
   plot.setRangeCrosshairVisible(true); 
   XYItemRenderer renderer = plot.getRenderer(); 
    
   if (renderer instanceof StandardXYItemRenderer) { 
   StandardXYItemRenderer rr = (StandardXYItemRenderer) renderer; 
   rr.setPlotShapes(true); 
   rr.setShapesFilled(true); 
   } 
       
//================================================    
   final double dRangeMin = 2.0;    
   final double dRangeHalf = dRangeMin/2;    
   double dUpperRange = 0; 
   double dLowerRange = 0; 
   double dCenterPoint = 0; 
   double dHighest = 8.8; 
   double dLowest = -13.6;    
       
   DateAxis axis = (DateAxis) plot.getDomainAxis(); 
   axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy")); 
   axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);   //set bar chart to middle 
       
   dUpperRange = dHighest + dRangeHalf; 
   dLowerRange = dLowest - dRangeHalf; 
   System.out.println("dUpperRange=["+dUpperRange+"],dLowerRange=["+dLowerRange+"]"); 
   ValueAxis rangeAxis = (ValueAxis) plot.getRangeAxis(); 
   rangeAxis.setRangeWithMargins(dLowerRange,dUpperRange); 
       
   //@@ Crosshair 
   Stroke m_srkCrosshair = new BasicStroke(1); 
    Color m_clrCrosshair = new Color(0xFFFF00); 
     plot.setDomainCrosshairStroke(m_srkCrosshair); 
   plot.setDomainCrosshairPaint(m_clrCrosshair); 
   plot.setDomainCrosshairVisible(true); 
    plot.setDomainCrosshairLockedOnData(true); 
    plot.setRangeCrosshairStroke(m_srkCrosshair); 
    plot.setRangeCrosshairPaint(m_clrCrosshair); 
    plot.setRangeCrosshairVisible(true); 
    plot.setRangeCrosshairLockedOnData(true); 
       
   return chart; 
} 

private XYDataset createDataset() { 
    
   //#TimeSeries s1 = new TimeSeries("L&G European Index Trust", Month.class); 
   TimeSeries s1 = new TimeSeries("Composite Index", Month.class); 
    
   s1.add(new Month(3, 2004), 3.3); 
   s1.add(new Month(4, 2004), 1.8); 
   s1.add(new Month(5, 2004), 0.6); 
   s1.add(new Month(6, 2004), -0.8); 
   s1.add(new Month(7, 2004), -2.3); 
   s1.add(new Month(8, 2004), 0.0); 
   s1.add(new Month(9, 2004), 1.7); 
   s1.add(new Month(10, 2004), 5.2); 
   s1.add(new Month(11, 2004), 8.8); 
   s1.add(new Month(12, 2004), 2.6); 
   s1.add(new Month(1, 2005), -4.9); 
   s1.add(new Month(2, 2005), -5.7); 
   s1.add(new Month(3, 2005), -11.3); 
       
   //#TimeSeries s2 = new TimeSeries("L&G UK Index Trust", Month.class); 
   TimeSeries s2 = new TimeSeries("Finance Index", Month.class); 

   s2.add(new Month(3, 2004), 4.2); 
   s2.add(new Month(4, 2004), 2.2); 
   s2.add(new Month(5, 2004), 1.1); 
   s2.add(new Month(6, 2004), -1.6); 
   s2.add(new Month(7, 2004), -4.2); 
   s2.add(new Month(8, 2004), 1.5); 
   s2.add(new Month(9, 2004), 0.4); 
   s2.add(new Month(10, 2004), 0.0); 
   s2.add(new Month(11, 2004), 6.1); 
   s2.add(new Month(12, 2004), 8.3); 
   s2.add(new Month(1, 2005), 2.7); 
   s2.add(new Month(2, 2005), 0.7); 
   s2.add(new Month(3, 2005), -4.6); 
       
   TimeSeries s3 = new TimeSeries("Industrial Products Index", Month.class); 
    
   s3.add(new Month(3, 2004), 3.2); 
   s3.add(new Month(4, 2004), -0.2); 
   s3.add(new Month(5, 2004), -2.1); 
   s3.add(new Month(6, 2004), 0.6); 
   s3.add(new Month(7, 2004), -1.2); 
   s3.add(new Month(8, 2004), -2.5); 
   s3.add(new Month(9, 2004), 0.7); 
   s3.add(new Month(10, 2004), -1.5); 
   s3.add(new Month(11, 2004), 4.1); 
   s3.add(new Month(12, 2004), 1.3); 
   s3.add(new Month(1, 2005), -2.7); 
   s3.add(new Month(2, 2005), -8.0); 
   s3.add(new Month(3, 2005), -13.6); 
    
   TimeSeriesCollection dataset = new TimeSeriesCollection(); 
   dataset.addSeries(s1); 
   dataset.addSeries(s2); 
   dataset.addSeries(s3); 
   dataset.setDomainIsPointsInTime(true); 
   return dataset; 
} 

   public static void main(String[] args) { 
      JTimeSeriesDemo demo = new JTimeSeriesDemo("Time Series Demo 1"); 
      demo.pack(); 
      RefineryUtilities.centerFrameOnScreen(demo); 
      demo.setVisible(true); 
   } 
    
}    
