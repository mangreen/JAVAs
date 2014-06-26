/*
 * Created on Oct 11, 2003
 *
 * Part of Stocks! 1.0
 *
 */

package CCAPI;


/**
 * this class contains a chart window, it is extending JFrame and will open up a new Chart. 
 *
 * @author uls
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;

import CCAPI.DataRetrieval.ConsorsHistoryRetriever;

public class ChartWindow extends JFrame {
	String symbol=""; //contains the symbol for this chartwindow
	JPanel toolbar, toolbarleft, mainpanel; //a panel supposed to be a toolbar
	JComboBox jcb; //the dropdown containing the symbols
	JComboBox timerange; //the dropdown for the complete timerange
	int timerangeInMilliseconds;

	JComboBox compression; //the dropdown for the duration
	int timerangems;

	public String code="";
	JLabel statusLine;

	JFreeChart chart;
	//JFreeChart datasets

	CombinedRangeXYPlot parent;

	private TimeSeriesCollection dataset1=new TimeSeriesCollection(); // XYSeriesCollection (see ttp://www.jfree.org/jfreechart/javadoc/org/jfree/data/XYSeriesCollection.html)
	private TimeSeriesCollection dataset2=new TimeSeriesCollection(); // XYSeriesCollection (see ttp://www.jfree.org/jfreechart/javadoc/org/jfree/data/XYSeriesCollection.html)
	private TimeSeriesCollection dataset3=new TimeSeriesCollection(); // XYSeriesCollection (see ttp://www.jfree.org/jfreechart/javadoc/org/jfree/data/XYSeriesCollection.html)
	//public VectorHighLowDataset hld=new VectorHighLowDataset("time");


	TimeSeries x1;
	TimeSeries bollinger_top;
	TimeSeries bollinger_bottom;

	TimeSeries env1, env2;

	TimeSeries average1;
	TimeSeries average2;
	ChartPanel chartPanel;
	Vector components=new Vector(); //contains plugin components

	//helper variables
	int av1=5;


	/**
	 * constructor. Constructs the chart window.
	 * @param s
	 * @param symbol
	 */

	ChartWindow thisp;
	public ChartWindow(String title){

		this.symbol=title;
		thisp=this;
		//initialize the main layout
		setTitle(title);
		mainpanel=new JPanel();
		GridBagLayout gbl=new GridBagLayout();
		GridBagConstraints gbc=new GridBagConstraints();
		mainpanel.setLayout(gbl);

		//build a toolbar
//		add a plain status bar
		this.statusLine=new JLabel("Done");
		this.getContentPane().add(statusLine, BorderLayout.SOUTH);

		statusLine.setBackground(new Color(0,0,0));
		statusLine.setForeground(new Color(255,255,255));
		statusLine.setOpaque(true);

		//
		x1=new TimeSeries("symbol", FixedMillisecond.class);
		dataset1.addSeries(x1);

		System.out.println("Populated.");

		//
		chart = createChart(dataset1);
		System.out.println("constr.");

		//chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);

		chart.setAntiAlias(false);

		chartPanel = new ChartPanel(chart);

		//

		int i=0;
		gbc.anchor=gbc.NORTHWEST;
		gbc.fill=gbc.BOTH;
		gbc.weightx=1;
		gbc.gridx=0;

		gbc.weighty=1;
		gbc.gridy=i;
		gbl.setConstraints(chartPanel, gbc);

		mainpanel.add(chartPanel);
		//System.out.println("add");
		setVisible(true);
		setSize(new Dimension(400,300));

		//chartPanel.setPopupMenu(buildPopupMenu());
		chartPanel.setMouseZoomable(true);
		chartPanel.setHorizontalAxisTrace(true);
		chartPanel.setVerticalAxisTrace(true);
		//chartPanel.setHorizontalZoom(true);

		chartPanel.setOpaque(true);
		chartPanel.setBackground(new Color(0,0,0));



		this.getContentPane().add(mainpanel, BorderLayout.CENTER);

		this.setSize(600,400);
		this.toFront();
		this.show();
	}


	/**
	 *	creates a candle stick chart to a file in jpg format (wrapper function!)
	 *	@param candles holds the candles to draw. 
	 *	@param jpgfilename must contain the file name where to render the jpg to. 
	 *	
	 **/

	public static void createCandleStickChart(Vector candles, String jpgfilename){
		createCandleStickChart(candles, jpgfilename, 600, 300);
		
	}
	
	/**
	 *	creates a candle stick chart to a file in jpg format
	 *	@param candles holds the candles to draw. 
	 *	@param jpgfilename must contain the file name where to render the jpg to. 
	 *	@A?aram width the width of the chart
	 *	@param height the height of the chart
	 *	
	 **/
	public static void createCandleStickChart(Vector candles, String jpgfilename, int width, int height){
		try{
			FinancialLibrary fl = new FinancialLibrary();
	
			OHLCDataItem[] ohlcs = new OHLCDataItem[candles.size()];
			for (int i = 0; i < ohlcs.length; i++) {
				Candle c = (Candle) candles.elementAt(ohlcs.length - i - 1);
				OHLCDataItem item = new OHLCDataItem(c.date, c.open, c.hi, c.low,
							c.close, 0);
				ohlcs[i] = item;
			}

			DefaultOHLCDataset setOHLC = new DefaultOHLCDataset("underlying", ohlcs);
			JFreeChart c1 = ChartFactory.createCandlestickChart("underlying", "time/date",
							"value", setOHLC, true);

			double min = fl.min(candles);
			double max = fl.max(candles);
			c1.getXYPlot().getRangeAxis().setRange(min * 0.99, max * 1.01);	

			persistencify(jpgfilename, c1, width, height);						

		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public static void createCandleStickChart(Vector candles, String filename, int w, int h, double[] vals){
		try{
			
	
			OHLCDataItem[] ohlcs = new OHLCDataItem[candles.size()];
			for (int i = 0; i < ohlcs.length; i++) {
				Candle c = (Candle) candles.elementAt(ohlcs.length - i - 1);
				OHLCDataItem item = new OHLCDataItem(c.date, c.open, c.hi, c.low,
							c.close, 0);
				ohlcs[i] = item;
			}

			DefaultOHLCDataset setOHLC = new DefaultOHLCDataset("underlying", ohlcs);
			
			//CombinedDataset cd = new CombinedDataset();
			//chart=new JFreeChart(null, null, parent, false);

			JFreeChart chart=ChartFactory.createTimeSeriesChart(null, "", "", null, false, false, false);
			XYPlot plot1=chart.getXYPlot();

			//plot1.setDataset(dataset);
			//plot1.setRenderer(new StandardXYItemRenderer());

			
			
			TimeSeriesCollection dataset = new TimeSeriesCollection();
			TimeSeries data=new TimeSeries("symbol", FixedMillisecond.class);
		

			for(int i=0;i<vals.length;i++){
				try{
					Candle c = (Candle) candles.elementAt(i);
					data.add(new FixedMillisecond(c.date.getTime()), vals[i]);
				}
				catch(Exception e){
					System.out.println("Unresolved error");
					//e.printStackTrace();
				}
			}
			dataset.addSeries(data);
			
			CandlestickRenderer c1=new CandlestickRenderer();

			c1.setAutoWidthFactor(1.0);
			c1.setAutoWidthGap(0.1);

			plot1.setDataset(0, setOHLC);
			plot1.setRenderer(0, c1);
			
			
			plot1.setDataset(1, dataset);
			plot1.setRenderer(1, new StandardXYItemRenderer());

			//		c1.setBasePaint(new Color(255,255,255));
//		c1.setBaseOutlinePaint(new Color(255,255,255));
	//
//		c1.setPaint(new Color(255,255,255));
	//
//		c1.setUpPaint(new Color(255,0,0,80));
//		c1.setDownPaint(new Color(0,255,0,80));



			//plot1.setSecondaryRenderer(0, c1);

			//plot1.setSecondaryDataset(0, dataset2);

			//XYDotRenderer xd1=new XYDotRenderer();
			//plot1.setSecondaryRenderer(0, new AreaXYRenderer(AreaXYRenderer.AREA_AND_SHAPES));
			//plot1.setSecondaryRenderer(0, xd1);


			//chart=new JFreeChart("", null, plot1, false);


			
			
//			
//			JFreeChart c1 = ChartFactory.createCandlestickChart("underlying", "time/date",
//							"value", setOHLC, true);
//
//			double min = fl.min(candles);
//			double max = fl.max(candles);
//			c1.getXYPlot().getRangeAxis().setRange(min * 0.99, max * 1.01);	
//			
//			TimeSeriesCollection dataset = new TimeSeriesCollection();
//			TimeSeries data=new TimeSeries("symbol", FixedMillisecond.class);
//			dataset.addSeries(data);
//
//			for(int i=0;i<candles.size();i++){
//				try{
//					Candle c = (Candle) candles.elementAt(ohlcs.length - i - 1);
//					data.add(new FixedMillisecond(c.date.getTime()), Math.random());
//				}
//				catch(Exception e){
//					System.out.println("Unresolved error");
//					//e.printStackTrace();
//				}
//			}
//			
//			/// 
//			
//			XYPlot xy = c1.getXYPlot();
//			xy.setDataset(2, dataset);
//			xy.setRenderer(2, new StandardXYItemRenderer());
//			
//			
			
			
			persistencify(filename, chart, w, h);						

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * persistencifies (writes to disc) as jpg a chart. 
	 * @param f the fileoutputstream where to write to
	 * @param chart the jfreechart
	 * @param w width of the output image
	 * @param h height of the output image
	 */
	public static void persistencify(String filename, JFreeChart chart, int w, int h) throws Exception{
		FileOutputStream f = new FileOutputStream(filename);
		if(filename.endsWith(".jpg")){
			ChartUtilities.writeChartAsJPEG(f, chart, w, h);	
		}
		else if(filename.endsWith(".png")){
			ChartUtilities.writeChartAsPNG(f, chart, w, h);
		}
	}
	
	/**
	 * temporal helper function
	 *
	 * @param dataset
	 * @return
	 */
	private JFreeChart createChart(TimeSeriesCollection dataset) {


		NumberAxis axis=new NumberAxis(null);
		axis.setAutoRangeIncludesZero(false);


		//parent=new CombinedRangeXYPlot(axis);

		//chart = null;

		//XYPlot plot2=new XYPlot(dataset2, new DateAxis(null), null, new StandardXYItemRenderer());
		//XYPlot subplot2=new XYPldt(dataset2, new DateAxis("Date 2"), null, )


		//parent.add(subplot1);
		//parent.add(subplot2);

		//chart=new JFreeChart(null, null, parent, false);

		chart=ChartFactory.createTimeSeriesChart(null, "", "", dataset, false, false, false);

		XYPlot plot1=chart.getXYPlot();

		plot1.setDataset(dataset);
		//plot1.setRenderer(new StandardXYItemRenderer());

		//plot1.setSecondaryDataset(0, hld);
		//CandlestickRenderer c1=new CandlestickRenderer();



	//c1.setAutoWidthFactor(1.0);
	//c1.setAutoWidthGap(0.1);
//	c1.setBasePaint(new Color(255,255,255));
//	c1.setBaseOutlinePaint(new Color(255,255,255));
//
//	c1.setPaint(new Color(255,255,255));
//
//	c1.setUpPaint(new Color(255,0,0,80));
//	c1.setDownPaint(new Color(0,255,0,80));



		//plot1.setSecondaryRenderer(0, c1);

		//plot1.setSecondaryDataset(0, dataset2);

		//XYDotRenderer xd1=new XYDotRenderer();
		//plot1.setSecondaryRenderer(0, new AreaXYRenderer(AreaXYRenderer.AREA_AND_SHAPES));
		//plot1.setSecondaryRenderer(0, xd1);


		//chart=new JFreeChart("", null, plot1, false);



		chart.setBackgroundPaint(new Color(0,0,0));

	return chart;

	}


	// data recipient interfaces
	/**
	 * implements DataRecipients data function
	  * @see jahangir.DataRecipient#data(jahangir.Tick)
	 */

	int i=0;
	long candletime=0;

	Candle current=new Candle();

	Vector ticks=new Vector();
	/*
	public void data(Tick t){

		try{
			//System.out.println(t.date.toString());

			if(candletime==0)candletime=ms;

			//statusLine.setText(""+t.symbol+" - "+t.value+" - "+t.amount+ " - "+t.change);

			try{
				x1.add(new FixedMillisecond(t.date), t.value);
			}
			catch(Exception e){
				System.out.println("Unresolved error");
				//e.printStackTrace();
			}

			//env1.add(new FixedMillisecond(t.date), t.value*1.01);
			//env2.add(new FixedMillisecond(t.date), t.value*0.99);


			if(timerangems!=-1){
				if(ms<candletime+timerangems){
					//check hi, lows, closes
					if(current.open==-1){
						current.open=t.value;
					}
					if(t.value>current.hi)current.hi=t.value;
					if(t.value<current.low)current.low=t.value;

					current.close=t.value;

				}
				else{
					//add current to candle vector
					current.date=new Date(ms+1);
					hld.addCandle(current);
					current=new Candle();
					candletime=0;
				}
			}
			//calcAverage1(0,t.date);


			i++;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	*/


	/**
	 * implements DataRecipient's getSymbol function
	 * @see jahangir.DataRecipient#getSymbol()
	 *
	 */
	public String getSymbol(){
		return symbol;
	}

	public void calcAverage1(int d, Date timestamp){
		try{
			double v=0;

			//List l1=x1.getItems();

			for(int i=d;i<d+av1;i++){
				v+=x1.getValue(x1.getItemCount()-(av1-i)).doubleValue();
			}
			average1.add(new FixedMillisecond(timestamp), v/av1);


		}
		catch(Exception e){
			//e.printStackTrace();
			//System.out.println(timestamp.toString());
		}
	}


	/**
	 * actually recalculates the first average (necessary when we change the average settings)
	 * @param d
	 */
	public void recalculateAverage1(){

		//average1=new TimeSeries("av1", FixedMillisecond.class);
		average1.delete(0, average1.getItemCount()-1);

		for(int z=0;z<x1.getItemCount();z++){
			try{
					double v=0;

					//List l1=x1.getItems();

					for(int i=z;i<z+av1;i++){
						v+=x1.getValue(x1.getItemCount()-(av1-i)).doubleValue();

					}
					average1.add(x1.getTimePeriod(z), v/av1);

				}
				catch(Exception e){
					//e.printStackTrace();
					//System.out.println(timestamp.toString());
				}
		}

	}

	public void draw(double[] v){
		TimeSeries data=new TimeSeries("symbol", FixedMillisecond.class);
		dataset1.addSeries(data);

		for(int i=0;i<v.length;i++){
			try{
				data.add(new FixedMillisecond(i), v[i]);
			}
			catch(Exception e){
				System.out.println("Unresolved error");
				//e.printStackTrace();
			}


		}
	}
	/*
	public void addMiniChart(Plugin p){
		components.add(p);

		rebuild();
	}

	public void removeMiniChart(Plugin p){
		components.remove(p);
		rebuild();

	}
	*/
	public void rebuild(){
		// remove everything from main panel
		mainpanel.removeAll();

		GridBagLayout gbl=new GridBagLayout();
		GridBagConstraints gbc=new GridBagConstraints();
		mainpanel.setLayout(gbl);

		gbc.anchor=gbc.NORTHWEST;
		gbc.fill=gbc.BOTH;
		gbc.weightx=1;
		gbc.gridx=0;

		int i=0;
	//add chart

		gbc.weighty=1;
		gbc.gridy=i;
		gbl.setConstraints(chartPanel, gbc);
		mainpanel.add(chartPanel);

		//add all other plugins/components
		validate();

	}






	/**
	 * converts data into a candlestick chart
	 *
	 */
	public void convert(){


		chart.setAntiAlias(false);
		chartPanel.setChart(chart);

	}


	/**
	 * wipes all data
	 *
	 */
	public void wipeAll(){

		//wipes all

		dataset1.removeAllSeries();
		x1=new TimeSeries("symbol", FixedMillisecond.class);
		dataset1.addSeries(x1);

		current=new Candle();

		candletime=-1;
//		hld.wipe();
	}

	/**
	 * returns if this chart should retrieve realtime data
	 */
	boolean rr=false;
	public boolean retrieveRealtime(){
		return rr;
	}


	public static void main(String[] args){
		/*
		ChartWindow cw=new ChartWindow("test");

		double[] c1=new double[100];
		for(int i=0;i<c1.length;i++){
			c1[i]=Math.random();
		}

		cw.draw(c1);


		double[] c2=new double[100];
		for(int i=0;i<c2.length;i++){
			c2[i]=Math.random();
		}

		cw.draw(c2);

		*/
		
		
		ConsorsHistoryRetriever cqr = new ConsorsHistoryRetriever();
		Vector data = cqr.getHistory(cqr.search("846900"));

		double[] c2=new double[data.size()];
		for(int i=0;i<c2.length;i++){
			Candle c = (Candle)data.elementAt(i);
			c2[i]= c.close - 1000;
		}

		
		ChartWindow.createCandleStickChart(data, "/home/us/test.jpg", 900, 700, c2);
		
		

	}

	
	/**
	 * new function to draw stuff. 
	 * @param candles
	 * @param vals
	 */
	
	/*
	public void drawNew(int bufferWidth, int bufferHeight, Vector<Candle> candles, double ... vals){
		
		int leftspace = 80;
		int rightspace = 50;
		int bottomspace = 10;

		int n;
		int startcandle;

		
		int drawheight, drawwidth;

		FinancialLibrary fl = new FinancialLibrary();
		JPanel panel = new JPanel();
		Graphics g = panel.getGraphics();
        g.setFont(new Font("Sans-Serif", 10, 10));
        int relx = 0;
        int rely = 0;
        String h2 = "";
        Candle fc = null;
        if (candles.size() != 0) {
            fc = (Candle) candles.elementAt(0);

            g.drawRect(leftspace, 30, bufferWidth - (leftspace + rightspace),
                       bufferHeight - (bottomspace + 30));

            //draw Frame border
        }


        //draw axes starts here
        int candlewidth = 10;
        int spacex = 3;

        //calculate hi and low of candles
        drawwidth = bufferWidth - (leftspace + rightspace);
        drawheight = bufferHeight - (bottomspace + 30);


        //general setup

		
        DecimalFormat df1 = new DecimalFormat("#.##");

        int line1y = 12;
        int line2y = 24;

        //draw headline background

        double relclose=0.0;
        String headline = "Symbol: " + symbol + "   " + (new Date());
        if (fc != null) {
            relclose = (fc.close - fl.min(candles)) / (fl.max(candles) - fl.min(candles));
            g.setColor(new Color(0, 0, 0));
            Candle c1 = (Candle) candles.elementAt(candles.size() - 1);
            double r = (c1.close / fc.close - 1) * 100;
            DecimalFormat df = new DecimalFormat("#.###");
            String vald = df.format(r);
            headline += "  change: " + vald + "%";


        }

        g.setColor(new Color(201, 215, 159));
        g.fillRect(0, 0, bufferWidth, line1y + 2);

        g.setColor(new Color(0, 0, 0));
        g.drawString(headline, relx + 10, rely + line1y);


        //draw extended menu background

        g.setColor(new Color(201, 215, 189));
        g.fillRect(0, line1y + 2, bufferWidth, line1y + 2);

        g.setColor(new Color(0, 0, 0));
        
        int t = candlewidth + spacex;

  n = (int) ( (double) drawwidth / (double) t)-1;


        if (n > candles.size() -1) {
            n = candles.size() - 1;
        }

        int cursorcandle=-1;
        
        // draw some sort of header. 
        g.drawString(h2, relx + 10, rely + line2y);

        startcandle = candles.size()-1;
        
        //draw the scala left
        g.setColor(new Color(0, 0, 0));

        double max = fl.max(candles);
        double min = fl.min(candles);
        
        double d = (max - min) / 10;
        double d1 = drawheight / 10;
        for (int k = 0; k < 11; k++) {
            g.drawLine(leftspace - 10, (int) (bufferHeight - bottomspace - k * d1),
                       leftspace, (int) (bufferHeight - bottomspace - k * d1));
            int v1 = (int) ( (min + k * d) * 1000);
            double v2 = ( (double) v1) / 1000;

            g.drawString("" + v2, leftspace - 75,
                         (int) (bufferHeight - bottomspace - k * d1 + 5));
        }
        //draw the right scala

        double pcenter=(d/2)+min;
        double p=(-1)*(1-max/pcenter);
        g.setColor(new Color(0,0,0));
        for (int k = 0; k < 11; k++) {
            g.drawLine(bufferWidth-rightspace, (int) (bufferHeight - bottomspace - k * d1),
                       bufferWidth-rightspace+5, (int) (bufferHeight - bottomspace - k * d1));
            double v1 =  (((-1)*p)*(5-k))*10;
            g.drawString("" + df1.format(v1)+" %", bufferWidth-rightspace+10,
                         (int) (bufferHeight - bottomspace - k * d1 + 5));
        }
        g.setColor(new Color(0,0,0));
        if(candles!=null){
          //draw candles
          for (int j = 0; j < (n + 2); j++) {
              try{



               //general draw preparations
               int posx = leftspace + candlewidth +
               ( (spacex + (candlewidth)) * (j - 1));
               int posx1 = leftspace + candlewidth +
               ( (spacex + (candlewidth)) * (j -2));

               // System.out.println("posx:"+posx);
               int y2=0, y3=0, y4=0;


               //start of candle draw code
               try{
               if(stpc.drawcandles){
                      Candle c = (Candle) candles.elementAt(startcandle - (n - j));

                      g.setColor(new Color(0, 0, 0));
                      double relhi = (c.hi - min) / (max - min);
                      double rellow = (c.low - min) / (max - min);
                      double relopen = (c.open - min) / (max - min);
                      relclose = (c.close - min) / (max - min);

                      y1 = 30 + (int) ( (1 - rellow) * drawheight);
                      y2 = 30 + (int) ( (1 - relhi) * drawheight);

                      y3 = 30 + (int) ( (1 - relopen) * drawheight);
                      y4 = 30 + (int) ( (1 - relclose) * drawheight);
                      if (c.rises()) {
                      if (y4 == y3)
                      y4 -= 1;
                      }
                      else {
                      if (y4 == y3)
                      y4 += 1;
                      }

                      if (c.rises()) {
                      g.setColor(new Color(0, 160, 0));
                      g.drawLine(posx, y1, posx, y2);
                      g.drawRect(posx - (candlewidth / 2), y4, (candlewidth), y3 - y4);
                      g.setColor(new Color(255, 255, 255));
                      g.fillRect(posx - (candlewidth / 2) + 1, y4 + 1, (candlewidth) - 1,
                      y3 - y4 - 1);
                      //g.setColor(new Color(0,0,0));
                      }
                      else {
                      g.setColor(new Color(150, 0, 0));
                      g.drawLine(posx, y1, posx, y2);
                      g.fillRect(posx - (candlewidth / 2), y3, (candlewidth), y4 - y3);
                      //g.setColor(new Color(0,0,0));
                      //g.drawRect(posx-(candlewidth/2), y3, (candlewidth), y4-y3);
                      }
               }
                
                   }catch(Exception e){
                   
              }
              
	}
*/	
}