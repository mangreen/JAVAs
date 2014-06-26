import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;


public class Converter {
	public static int CellRow, CellColumn, rowSum=5, stockSum=1000;
	public static boolean IDflag;
	public static String[] indexStock = new String[10000];
	public static int[]stockFileSum = new int[100000];
	
	public static void main(String[] args)throws Exception{
		getFile();

	}
	public static void getFile() throws Exception{
		//�إߤ�r��Ū������
		File originalFile = new File("..\\OTCConverter\\Data\\9101-9103.txt");
		getxlsFile(originalFile);
	}
	public static void getxlsFile(File originalFile) throws Exception{
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
		        do{
		        	//���� �@��@�� �����Ʊq�ɮפ�Ū�X��
		            dataLine = BufferedStream.readLine();
		            String stockData=dataLine.substring(0,4);
		            
		            //�P�_stock�ؿ��U���ɮ׼�
		            int fileSum = 0;
		            
		            for(int i=0; i<stockSum; i++){
		            	if(null == indexStock[i] || indexStock[i].length()==0){
		            		indexStock[i]=stockData;
		            		stockFileSum[i]=1;
		            		fileSum = stockFileSum[i];
		            		break;
		            	}else if(stockData.equals(indexStock[i])){
		            		fileSum = stockFileSum[i];
		            		break;
		            	}
		            }
		            //System.out.println(fileSum);
		            File oldDir = new File("..\\OTCConverter\\xlsData\\"+stockData);
		            File oldFile;
		            File newFile = new File("..\\OTCConverter\\xlsData\\"+stockData+"\\"+stockData+".part"+fileSum+".xls");
		            if(oldDir.exists()){	            	
		            	
		            	for(int i=1; i<=fileSum; i++){
		            		oldFile = new File("..\\OTCConverter\\xlsData\\"+stockData+"\\"+stockData+".part"+i+".xls");
		            		if(i<fileSum){            			
		            			if(oldFile.exists()){
		            				
		            				//�Ыذ�Ū��Excel�u�@��������,�Q�Υi�g�J��Excel�u�@��������ӧ�sExcel�u�@��
		            	        	jxl.Workbook readWorkbook = Workbook.getWorkbook(oldFile);
		            	        	jxl.write.WritableWorkbook writeWorkbook = Workbook.createWorkbook(oldFile, readWorkbook);
		            	        	//Ū���Ĥ@�i�u�@��
		            	        	jxl.write.WritableSheet wSheet = writeWorkbook.getSheet(0);
		            	        	CellRow=1;
		            	        	CellColumn=1;
		            	        	IDflag=false;
		            	        	compareIDrow(wSheet, dataLine, stockData);
		            	        	if(IDflag==true){
		            	        		
		            	        		writeDatecolumn(wSheet, dataLine);
				            	    	writeVolumeCell(wSheet, dataLine);
				            	    	//System.out.println("CellRow:"+CellRow);
				            	    	writeWorkbook.write();
				            	        writeWorkbook.close();
				            	        break;
		            	        	}
		            	        	writeWorkbook.write();
			            	        writeWorkbook.close();   	
		            	        	
		            			}else {
		            				
		            				createStockFile(oldFile, dataLine, stockData);
			            			break;
		            			}	  
		            		}else if(i==fileSum){
		            			
		            				
		            				createStockFile(oldFile, dataLine, stockData);
		            				break;
		            			
		            		}
		            		
		            	}
		            	//createStockFile(newFile, dataLine, stockData);
		            }else{
		            	oldDir.mkdir();
		            	createStockFile(newFile, dataLine, stockData);
		            }
		            
		            //��Ū����̫�@���,���X
		            if(dataLine == null)
		                break;
		            //�L�XŪ������
		            //System.out.println(stockData);
		        }while(true);
		}catch(Exception e){
			//�p�G���s�b
			System.out.println("originalFiles do not exist");
		}
	}
	
	public static void createStockFile(File newFile, String dataLine, String stockData)throws Exception{
		
		if(newFile.exists()){
        	//�Ыذ�Ū��Excel�u�@��������,�Q�Υi�g�J��Excel�u�@��������ӧ�sExcel�u�@��
        	jxl.Workbook readWorkbook = Workbook.getWorkbook(newFile);
        	jxl.write.WritableWorkbook writeWorkbook = Workbook.createWorkbook(newFile, readWorkbook);
        	//Ū���Ĥ@�i�u�@��
        	jxl.write.WritableSheet wSheet = writeWorkbook.getSheet(0);
        	CellRow=1;
        	CellColumn=1;
        	writeIDrow(wSheet, dataLine, stockData);
    		writeDatecolumn(wSheet, dataLine);
    		writeVolumeCell(wSheet, dataLine);
    		//System.out.println("CellRow:"+CellRow);
        	writeWorkbook.write();
        	writeWorkbook.close();
        }else{
        	
        	//�c��Workbook��H, �uŪWorkbook��H
        	//Method 1�G�Ыإi�g�J��Excel�u�@��
        	jxl.write.WritableWorkbook workbook = Workbook.createWorkbook(newFile);
        	//Method 2�G�NWritableWorkbook�����g�J���X�y
        	/*
        	OutputStream os = new FileOutputStream(targetfile);
        	jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(os);
        	 */

        	//�Ы�Excel�u�@��
        	jxl.write.WritableSheet	wSheet = workbook.createSheet("Test Sheet 1", 0);
        	createIDrow(wSheet, dataLine);
        	createDatecolumn(wSheet, dataLine);
        	createVolumeCell(wSheet, dataLine);
        		
        	workbook.write();
			workbook.close();
        }
	}
	
	public static void writeVolumeCell(WritableSheet wSheet, String dataLine){
		
		try{
			
			String BorS=dataLine.substring(4,5);
			String volumnData=dataLine.substring(5,13);
			//�����x�s��Cell���榡
			WritableCellFormat cellFormat1 = new WritableCellFormat();
			Label label = null;
			if(BorS.equals("B")){
				jxl.write.WritableCell wCell = wSheet.getWritableCell(CellColumn, CellRow);
				//System.out.println(CellColumn+", "+CellRow);
				if(null == wCell.getContents() || wCell.getContents().length()==0){
					//�W�[�@�Ӥ�r�x�s��Cell,�[�Jsheet��
					label = new Label(CellColumn, CellRow, BorS+"/"+volumnData+"/"+"1/0/00000000/0", cellFormat1);
				}else{
					String[] strCell = wCell.getContents().split("/");
					int sVolumn = Integer.parseInt(strCell[1])+Integer.parseInt(volumnData);
					String stockVolumn="";
					//�[����Ʀr�e���ɹs
					if(sVolumn<1000){stockVolumn=("00000"+sVolumn);} 
					if(sVolumn>=1000&&sVolumn<=9999){stockVolumn=("0000"+sVolumn);} 
					if(sVolumn>=10000&&sVolumn<=99999){stockVolumn=("000"+sVolumn);} 
					if(sVolumn>=100000&&sVolumn<=999999){stockVolumn=("00"+sVolumn);}
					if(sVolumn>=1000000&&sVolumn<=9999999){stockVolumn=("0"+sVolumn);}
					if(sVolumn>=10000000){stockVolumn=String.valueOf(sVolumn);}
					
					int buyTimes = Integer.parseInt(strCell[2])+1;
					label = new Label(CellColumn, CellRow, BorS+"/"+stockVolumn+"/"+buyTimes+"/"+strCell[3]+"/"+strCell[4]+"/"+strCell[5], cellFormat1);
				}
			}else{
				jxl.write.WritableCell wCell = wSheet.getWritableCell(CellColumn, CellRow);
				if(null == wCell.getContents() || wCell.getContents().length()==0){
					//�W�[�@�Ӥ�r�x�s��Cell,�[�Jsheet��
					label = new Label(CellColumn, CellRow, "0/00000000/0/"+BorS+"/"+volumnData+"/"+"1", cellFormat1);
				}else{
					String[] strCell = wCell.getContents().split("/");
					int sVolumn = Integer.parseInt(strCell[4])+Integer.parseInt(volumnData);
					String stockVolumn="";
					if(sVolumn<1000){stockVolumn=("00000"+sVolumn);} 
					if(sVolumn>=1000&&sVolumn<=9999){stockVolumn=("0000"+sVolumn);}  
					if(sVolumn>=10000&&sVolumn<=99999){stockVolumn=("000"+sVolumn);} 
					if(sVolumn>=100000&&sVolumn<=999999){stockVolumn=("00"+sVolumn);}
					if(sVolumn>=1000000&&sVolumn<=9999999){stockVolumn=("0"+sVolumn);}
					if(sVolumn>=10000000){stockVolumn=String.valueOf(sVolumn);}
					
					int buyTimes = Integer.parseInt(strCell[5])+1;
					label = new Label(CellColumn, CellRow, strCell[0]+"/"+strCell[1]+"/"+strCell[2]+"/"+BorS+"/"+stockVolumn+"/"+buyTimes, cellFormat1);
				}
			}
			wSheet.addCell(label);
			
		}catch(Exception e){
			System.out.println("Cells do not exist");
		}
		
	}
	
	public static void createVolumeCell(WritableSheet wSheet, String dataLine){
		try{
			
			String BorS=dataLine.substring(4,5);
			String volumnData=dataLine.substring(5,13);
			
			//�����x�s��Cell���榡
			WritableCellFormat cellFormat1 = new WritableCellFormat();
			Label label=null;
			if(BorS.equals("B")){
				//�W�[�@�Ӥ�r�x�s��Cell,�[�Jsheet��
				label = new Label(1, 1, BorS+"/"+volumnData+"/"+"1/0/00000000/0", cellFormat1);	
			}else{
				label = new Label(1, 1, "0/00000000/0/"+BorS+"/"+volumnData+"/"+"1", cellFormat1);	
			}
			
			wSheet.addCell(label);
			

		}catch(Exception e){
			System.out.println("Cells do not exist");
		}
	}
	public static void writeDatecolumn(WritableSheet wSheet, String dataLine){
		try{
			
			String dateData=dataLine.substring(19,25);

			int wsColumns = wSheet.getColumns()+1;
			for(int i = 1; i<wsColumns; i++){	
				jxl.write.WritableCell wCell = wSheet.getWritableCell(i, 0);
				//System.out.println(wCell);
				if(null == wCell.getContents() || wCell.getContents().length()==0){
					//System.out.println(idData);
					//�����x�s��Cell���榡
					WritableCellFormat cellFormat1 = new WritableCellFormat();
					//�W�[�@�Ӥ�r�x�s��Cell,�[�Jsheet��
					Label label = new Label(i, 0, dateData, cellFormat1);
					wSheet.addCell(label);
					CellColumn=i;
					break;
				}else if(dateData.equals(wCell.getContents())){
					CellColumn=i;
					break;
				}
			}
		}catch(Exception e){
			System.out.println("Cells do not exist");
		}
	}
	
	public static void createDatecolumn(WritableSheet wSheet, String dataLine){
		try{
			
			String dateData=dataLine.substring(19,25);

			//�����x�s��Cell���榡
			WritableCellFormat cellFormat1 = new WritableCellFormat();
			//�W�[�@�Ӥ�r�x�s��Cell,�[�Jsheet��
			Label label = new Label(1, 0, dateData, cellFormat1);
			wSheet.addCell(label);

		}catch(Exception e){
			System.out.println("Cells do not exist");
		}
	}
	
	public static void compareIDrow(WritableSheet wSheet, String dataLine, String stockData){
		try{
			
			String idData=dataLine.substring(43,51);

			int wsRows = wSheet.getRows()+1;
			if(wSheet.getRows()<(rowSum+1)){
				for(int i = 1; i<wsRows; i++){	
					jxl.write.WritableCell wCell = wSheet.getWritableCell(0, i);
					//System.out.println(wCell);
					if(null == wCell.getContents() || wCell.getContents().length()==0){
						//System.out.println(idData);
						//�����x�s��Cell���榡
						WritableCellFormat cellFormat1 = new WritableCellFormat();
						//�W�[�@�Ӥ�r�x�s��Cell,�[�Jsheet��
						Label label = new Label(0, i, idData, cellFormat1);
						wSheet.addCell(label);
						CellRow=i;
						if(CellRow == rowSum){
							for(int j=0; j<stockSum; j++){
				            	if(null == indexStock[j] || indexStock[j].length()==0){
				            		indexStock[j]=stockData;
				            		stockFileSum[j]=1;
				            		//fileSum = stockFileSum[i];
				            		break;
				            	}else if(stockData.equals(indexStock[j])){
				            		stockFileSum[j]++;
				            		break;
				            	}
				            }
						}
						IDflag = true;
						//System.out.println("funtion CellRow:"+CellRow);
						break;
					}else if(idData.equals(wCell.getContents())){
						CellRow=i;
						IDflag = true;
						break;
					}
				}
			}else if(wSheet.getRows()==(rowSum+1)){
				for(int i = 1; i<wsRows; i++){
					jxl.write.WritableCell wCell = wSheet.getWritableCell(0, i);
					//System.out.println(wCell);
					if(null == wCell.getContents() || wCell.getContents().length()==0){
						IDflag = false;
						break;
					}else if(idData.equals(wCell.getContents())){
						CellRow=i;
						IDflag = true;
						break;
					}
				}
			}
		}catch(Exception e){
			System.out.println("Cells do not exist");
		}
		
	}
	
	public static void writeIDrow(WritableSheet wSheet, String dataLine, String stockData){
		try{
			
			String idData=dataLine.substring(43,51);

			int wsRows = wSheet.getRows()+1;
			
			for(int i = 1; i<wsRows; i++){	
				jxl.write.WritableCell wCell = wSheet.getWritableCell(0, i);
				//System.out.println(wCell);
				if(null == wCell.getContents() || wCell.getContents().length()==0){
					//System.out.println(idData);
					//�����x�s��Cell���榡
					WritableCellFormat cellFormat1 = new WritableCellFormat();
					//�W�[�@�Ӥ�r�x�s��Cell,�[�Jsheet��
					Label label = new Label(0, i, idData, cellFormat1);
					wSheet.addCell(label);
					CellRow=i;
					if(CellRow == rowSum){
						for(int j=0; j<stockSum; j++){
			            	if(null == indexStock[j] || indexStock[j].length()==0){
			            		indexStock[j]=stockData;
			            		stockFileSum[j]=1;
			            		//fileSum = stockFileSum[i];
			            		break;
			            	}else if(stockData.equals(indexStock[j])){
			            		stockFileSum[j]++;
			            		break;
			            	}
			            }
					}
					//System.out.println("funtion CellRow:"+CellRow);
					break;
				}else if(idData.equals(wCell.getContents())){
					CellRow=i;
					break;
				}
			}
		}catch(Exception e){
			System.out.println("Cells do not exist");
		}
	}
	
	public static void createIDrow(WritableSheet wSheet, String dataLine){
		try{
			
			String idData=dataLine.substring(43,51);

			//�����x�s��Cell���榡
			WritableCellFormat cellFormat1 = new WritableCellFormat();
			//�W�[�@�Ӥ�r�x�s��Cell,�[�Jsheet��
			Label label = new Label(0, 1, idData, cellFormat1);
			wSheet.addCell(label);

		}catch(Exception e){
			System.out.println("Cells do not exist");
		}
		
	}
	
	public static void gettxtFile() throws Exception{
		FileReader fileStream = null;
		//�إߤ�r��Ū������
		File originalFile = new File("..\\OTCConverter\\Data\\9101-9103.txt");
		//�P�_�ɮ׬O�_�s�b
		try{
			if(originalFile.exists())//�p�G�s�b
				//Ū�J�ɮצ�y
		        fileStream = new FileReader(originalFile); 
		        //�z�L BufferedReader ���ڭ�Ū���
		        BufferedReader BufferedStream = new BufferedReader(fileStream);
			    //Ū�����
		        String dataLine;
		        do{
		        	//���� �@��@�� �����Ʊq�ɮפ�Ū�X��
		            dataLine = BufferedStream.readLine();
		            
		            String stockData=dataLine.substring(0,4);
		            File oldFile = new File("..\\OTCConverter\\CData\\"+stockData+".txt");
		            if(oldFile.exists()){
		            	//System.out.println("Files exist");
		            }else{
		            	//File newFile = new File("..\\OTCConverter\\CData\\"+stockData+".txt");
			            //newFile.createNewFile();
		            }
		            
		            //��Ū����̫�@���,���X
		            if(dataLine == null)
		                break;
		            //�L�XŪ������
		           // System.out.println(stockData);
		        }while(true);
		}catch(Exception e){
			//�p�G���s�b
			//System.out.println("Files do not exist");
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
