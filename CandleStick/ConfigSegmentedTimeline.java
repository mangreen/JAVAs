import java.util.Date;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;

//---segmentedTimeline---
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.SegmentedTimeline;

public class ConfigSegmentedTimeline{
   
   private static long m_lFirstMonday = 0;
   private static ArrayList m_alHDate = new ArrayList();
   private static SegmentedTimeline m_segmentedTimeline = null;
   private static JFreeChart m_chart = null;
   private static Date m_aTrdDate[] = new Date[1];
   private static Date m_dtDateInit = null;
   
   public void configSegmentedTimeline(){      
   }
   
   public static void run(){
     //@@ Segmented Timeline - Exclude Non-Trading Day, Suspended date, Saturday & Sunday
     Date dtInit = getDateInit();
     JFreeChart chart = getChart();
     DateAxis axis = (DateAxis) chart.getXYPlot().getDomainAxis();
     //SegmentedTimeline segmentedTimeline = SegmentedTimeline.newMondayThroughFridayTimeline();
     SegmentedTimeline segmentedTimeline = new SegmentedTimeline(SegmentedTimeline.DAY_SEGMENT_SIZE, 5, 2);

     if(dtInit!=null){      
       SimpleDateFormat sdfYC = new SimpleDateFormat("yyyy");
       SimpleDateFormat sdfMC = new SimpleDateFormat("MM");
       SimpleDateFormat sdfDC = new SimpleDateFormat("dd");
       int iYC = Integer.parseInt(sdfYC.format(dtInit));
       int iMC = Integer.parseInt(sdfMC.format(dtInit));
       int iDC = Integer.parseInt(sdfDC.format(dtInit));
         //-Select your Timezone-
      GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+8")); //use standard GMT + 0
       //calendar.set(2006, Calendar.MARCH, 25, 12, 0, 0);
       Date d1 = calendar.getTime();
      
      //GregorianCalendar calendar = new GregorianCalendar();
      ArrayList alHDate = new ArrayList(); //HDate-Hide Date
      Date aTrdDate[] = getTrdDate();
      Date dtDate = calendar.getTime();
      int iTrdDateLast = aTrdDate.length-1;
      int iIndex = 0;
      if(aTrdDate.length>0){
         Date aTrdDateInit = aTrdDate[0];
         Date aTrdDateLast = aTrdDate[iTrdDateLast];
         calendar.setTime(aTrdDateInit); //set init date      
         double dDays = (aTrdDateLast.getTime()-aTrdDateInit.getTime())/1000/60/60/24; //60 minutes, 60 seconds, 24 hour.
         for(int i=0; i<=dDays; i++){
            dtDate = calendar.getTime();   
            if(dtDate.equals(aTrdDate[iIndex])){
               iIndex++;
            }else{
               alHDate.add(dtDate);      
            }
            calendar.add(Calendar.DATE, 1);//increment 1 day      
          }
       }
      
      int offset = TimeZone.getDefault().getRawOffset();
      TimeZone NO_DST_TIME_ZONE = new SimpleTimeZone(offset, "UTC-" + offset);       
       Calendar cal = new GregorianCalendar(NO_DST_TIME_ZONE);
       cal.set(iYC, 0, 1, 0, 0, 0); //can't use iMC and iDC, because the crosshair have bugs in locking data
       cal.set(Calendar.MILLISECOND, 0);
       while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DATE, 1);
       }
       long lFirstMonday = cal.getTime().getTime();
      
       //Store parameter
       setFirstMonday(lFirstMonday);
       setHDate(alHDate);
       setSegmentedTimeline(segmentedTimeline);
     }
      
   }   
   
   private static void setFirstMonday(long vs_lFirstMonday){ m_lFirstMonday = vs_lFirstMonday; }
   private static void setHDate(ArrayList vs_alHDate){ m_alHDate = vs_alHDate; }
   private static void setSegmentedTimeline(SegmentedTimeline vs_segmentedTimeline){ m_segmentedTimeline = vs_segmentedTimeline; }
   public static void setChart(JFreeChart vs_chart){ m_chart = vs_chart; }
   public static void setTrdDate(Date[] vs_aTrdDate){ m_aTrdDate = vs_aTrdDate; }
   public static void setDateInit(Date vs_dtDateInit){ m_dtDateInit = vs_dtDateInit; }
   
   public static long getFirstMonday(){ return m_lFirstMonday; }
   public static ArrayList getHDate(){ return m_alHDate; }
   public static SegmentedTimeline getSegmentedTimeline(){ return m_segmentedTimeline; }   
   public static JFreeChart getChart(){ return m_chart; }
   public static Date[] getTrdDate(){ return m_aTrdDate; }
   public static Date getDateInit(){ return m_dtDateInit; }
   
   public static void main(String[] args){
      
   }
}