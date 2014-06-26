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
		//�إߤ�r��Ū������
		File originalFile = new File("..\\OTCConverter\\txtData\\0751.txt");
		String idData="99941801";
		BufferedWriter IDCell = new BufferedWriter(new FileWriter(originalFile,true));
		//�g�J
		IDCell.write("B"+idData+"|");
		//����
		IDCell.newLine();
		IDCell.write("S"+idData+"|");
		IDCell.newLine();
		IDCell.write("|");
		//����buffer
		IDCell.close();
	}
	
	public static void getFile() throws Exception{
		
		//�إߤ�r��Ū������
		File originalFile = new File("..\\OTCConverter\\Data\\9101-9103.txt");
		gettxtFile(originalFile);
	}
	
	public static void gettxtFile(File originalFile) throws Exception{
		FileReader fileStream = null;
		//�P�_�ɮ׬O�_�s�b
		try{
			if(originalFile.exists())//�p�G�s�b
				//Ū�J�ɮצ�y
		        fileStream = new FileReader(originalFile); 
		        //�z�L BufferedReader ���ڭ�Ū���
		        BufferedReader BufferedStream = new BufferedReader(fileStream);
			    //Ū�����
		        String dataLine;
		        String[] buyBuffer = new String[5000000]; 
		        String[] sellBuffer = new String[5000000]; 
		        do{
		        	//���� �@��@�� �����Ʊq�ɮפ�Ū�X��
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
		            

		            
		            //��Ū����̫�@���,���X
		            if(dataLine == null)
		                break;
		            //�L�XŪ������
		            //System.out.println(stockData);
		        }while(true);
		}catch(Exception e){
			//�p�G���s�b
			System.out.println("Files do not exist");
		}	      
	}
	
	public static void writeIDrow(String dataLine, File oldFile) throws IOException{
		String idData=dataLine.substring(43,51);
		FileReader IDfileStream = null;
		//�إߤ�r��Ū������

		//Ū�J�ɮצ�y
	    IDfileStream = new FileReader(oldFile); 
	    //�z�L BufferedReader ���ڭ�Ū���
	    BufferedReader IDrowBufferedStream = new BufferedReader(IDfileStream);
	    String txtTemp="";
	    StringBuffer content   =   new   StringBuffer();
	    //Ū�����
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
	    		//�g�J
	    		IDCell.write("B"+idData+"|");
	    		//����
	    		IDCell.newLine();
	    		IDCell.write("S"+idData+"|");
	    		IDCell.newLine();
	    		IDCell.write("|");
	    		//����buffer
	    		IDCell.close();
	           	break;
	        }
	        
	    } 	
	}
	
	public static void createIDrow(String dataLine, File newFile) throws IOException{
		try{	
			String idData=dataLine.substring(43,51);
			BufferedWriter IDCell = new BufferedWriter(new FileWriter(newFile,true));
			//�g�J
			IDCell.write("B"+idData+"|");
			//����
			IDCell.newLine();
			IDCell.write("S"+idData+"|");
			IDCell.newLine();
			IDCell.write("|");
			//����buffer
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
