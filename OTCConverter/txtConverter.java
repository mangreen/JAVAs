import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class txtConverter {
	public static String compareDate = null;
	public static void main(String[] args) throws Exception{
		getFile();
		//test();
	}
	
	public static void test() throws Exception{
		FileReader fileStream = null;
		//建立文字檔讀取物件
		File originalFile = new File("..\\OTCConverter\\txtData\\0751.txt");
		String idData="99941801";
		BufferedWriter IDCell = new BufferedWriter(new FileWriter(originalFile,true));
		//寫入
		IDCell.write("B"+idData+"|");
		//換行
		IDCell.newLine();
		IDCell.write("S"+idData+"|");
		IDCell.newLine();
		IDCell.write("|");
		//關閉buffer
		IDCell.close();
	}
	
	public static void getFile() throws Exception{
		
		//建立文字檔讀取物件
		File originalFile = new File("..\\OTCConverter\\Data\\9101-9103.txt");
		gettxtFile(originalFile);
	}
	
	public static void gettxtFile(File originalFile) throws Exception{
		FileReader fileStream = null;
		//判斷檔案是否存在
		try{
			if(originalFile.exists())//如果存在
				//讀入檔案串流
		        fileStream = new FileReader(originalFile); 
		        //透過 BufferedReader 幫我們讀資料
		        BufferedReader BufferedStream = new BufferedReader(fileStream);
			    //讀取資料
		        String dataLine;
		        String[] buyBuffer = new String[5000000]; 
		        String[] sellBuffer = new String[5000000]; 
		        do{
		        	//接著 一行一行 的把資料從檔案中讀出來
		            dataLine = BufferedStream.readLine();
		            String dateData = dataLine.substring(19,25);
		            if(compareDate == null || dateData.equals(compareDate)){
		            	compareDate = dateData;
			            String stockData = dataLine.substring(0,4);
    		            
			            File oldFile = new File("..\\OTCConverter\\txtData\\"+stockData+".txt");
			            if(oldFile.exists()){
			            	System.out.println("=");
			            }else{
			            	File newFile = new File("..\\OTCConverter\\txtData\\"+stockData+".txt");
				            newFile.createNewFile();
				            
			            }
			            
			            
		            }else if(dateData.equals(compareDate) != true){
		            	System.out.println("!=");
		            }
		            

		            
		            //當讀取到最後一行後,跳出
		            if(dataLine == null)
		                break;
		            //印出讀取的行
		            //System.out.println(stockData);
		        }while(true);
		}catch(Exception e){
			//如果不存在
			System.out.println("Files do not exist");
		}	      
	}
	
	public static void writeIDrow(String dataLine, File oldFile) throws IOException{
		String idData=dataLine.substring(43,51);
		FileReader IDfileStream = null;
		//建立文字檔讀取物件

		//讀入檔案串流
	    IDfileStream = new FileReader(oldFile); 
	    //透過 BufferedReader 幫我們讀資料
	    BufferedReader IDrowBufferedStream = new BufferedReader(IDfileStream);
	    String txtTemp="";
	    StringBuffer content   =   new   StringBuffer();
	    //讀取資料
	    String txtdataLine;
	    
	    while((txtTemp=IDrowBufferedStream.readLine())!=null){
	    	//content.append(txtTemp).append("\n");
	    	String idBuyData="B"+idData+"|";
	    	System.out.println(idBuyData);
	    	
	    	if(idBuyData.equals(txtTemp)){
	    		break;
	    	}else if(txtTemp.equals("|")){
	    		//System.out.println(txtTemp);
	    		//txtTemp=txtTemp.replace("|", "");
	    		BufferedWriter IDCell = new BufferedWriter(new FileWriter(oldFile,true));
	    		//寫入
	    		IDCell.write("B"+idData+"|");
	    		//換行
	    		IDCell.newLine();
	    		IDCell.write("S"+idData+"|");
	    		IDCell.newLine();
	    		IDCell.write("|");
	    		//關閉buffer
	    		IDCell.close();
	           	break;
	        }
	        
	    } 	
	}
	
	public static void createIDrow(String dataLine, File newFile) throws IOException{
		try{	
			String idData=dataLine.substring(43,51);
			BufferedWriter IDCell = new BufferedWriter(new FileWriter(newFile,true));
			//寫入
			IDCell.write("B"+idData+"|");
			//換行
			IDCell.newLine();
			IDCell.write("S"+idData+"|");
			IDCell.newLine();
			IDCell.write("|");
			//關閉buffer
			IDCell.close();
			
		}catch(Exception e){
			System.out.println("Cells can't be writed");
		}
	}
	
	public static void getInput() throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		for(;;){
			System.out.print("FQ> ");
			String line = in.readLine();
			if((line == null) || (line.equals("quit"))) 
				break;
			try{
				int x =Integer.parseInt(line);
				System.out.println(x + "!=");
			}catch(Exception e){
				System.out.println("Invalid Input");
			}
		}
	}
}
