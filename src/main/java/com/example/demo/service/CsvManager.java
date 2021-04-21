package com.example.demo.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.example.demo.Domain.EnergyMeasurement;
import com.example.demo.Domain.Point;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

@Service
public class CsvManager implements DiskPersistance {

	String ROOT_PATH="/";
	int FIXED_DELAY=1000;
	long MAX_ENERGY=-1;
	public void initialization(String ROOT_PATH) {
		this.ROOT_PATH=ROOT_PATH;
	}
	public void initialization(String ROOT_PATH, int FIXED_DELAY) {
		this.ROOT_PATH=ROOT_PATH;
		this.FIXED_DELAY=FIXED_DELAY;
	}
	public void initialization(String ROOT_PATH, int FIXED_DELAY, long MAX_ENERGY) {
		this.ROOT_PATH=ROOT_PATH;
		this.FIXED_DELAY=FIXED_DELAY;
	}
	public void createCSV(String metricName) {
		File file = new File(ROOT_PATH+metricName+".csv"); 
	    try { 
	        CSVWriter writer = new CSVWriter(new FileWriter(file));
	        String[] header = { "timestamp", "energy_uj" }; 
	        writer.writeNext(header); 
	        writer.close(); 
	    } 
	    catch (IOException e) { e.printStackTrace(); } 
	}
	
	public void appendList( String metricName,LinkedList<Point> linkedList) {
		if(linkedList==null || linkedList.size()<2 ) return ;
		File file = new File(ROOT_PATH+"/"+metricName+".csv"); 
	    try { 
	        CSVWriter writer = new CSVWriter(new FileWriter(file, true));
	        List<String[]> data = new ArrayList<String[]>(); 
	        for(Point p : linkedList)
	            data.add(new String[] { p.getTimestamp().toString(), p.getEnergy_uj()+"" }); 		        
	        writer.writeAll(data); 
	        writer.close(); 
	    } 
	    catch (IOException e) { e.printStackTrace(); } 
	}
	
	public EnergyMeasurement getEnergyConsumption( String metricName, Instant startTime, Instant endTime, long numberOfPoints) 
	{
		
		long result=0;
		int overflows=0;
		int i=0;
		long halfPeriod=FIXED_DELAY/2;
		Point previousPoint=null,firstPoint=null,lastPoint=null;

    	int firstLineNumber=getfirstLine(metricName, startTime, endTime, numberOfPoints);
    	
    	if(firstLineNumber== -1)
    		return null;

		File file = new File(ROOT_PATH+"/"+metricName+".csv"); 
	    try { 

	        CSVReader csvReader = new CSVReader(new FileReader(file),CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, firstLineNumber); 
	        String[] nextRecord;
	        //csvReader.readNext(); // ignore header



	        while ((nextRecord = csvReader.readNext()) != null) 
	        { 
	        	Point p=new Point(Instant.parse(nextRecord[0]),Long.parseLong(nextRecord[1]));
        		
        		if( endTime.compareTo(p.getTimestamp()  )<=0 &&  Math.abs(Duration.between(endTime, p.getTimestamp() ).toMillis()) >halfPeriod)	
        			break;
        		else if( startTime.compareTo(p.getTimestamp()  )<=0 ||  Math.abs(Duration.between(startTime, p.getTimestamp() ).toMillis()) <=halfPeriod )
        		{

        			if(i==0) 
        				previousPoint=firstPoint=new Point(Instant.parse(nextRecord[0]),Long.parseLong(nextRecord[1]));
        			else 
        			{
        				if(p.getEnergy_uj() < previousPoint.getEnergy_uj())		
        					overflows++;
        				previousPoint=p;
        			}
        			i++;
        		}
	        }
	        lastPoint=previousPoint;

	        
	        if(overflows>0)
				result=   MAX_ENERGY - firstPoint.getEnergy_uj()
						+ MAX_ENERGY * (overflows-1) 
						+ lastPoint.getEnergy_uj();
			else 
				result =  lastPoint.getEnergy_uj() - firstPoint.getEnergy_uj();
	    } 
	    catch (IOException e) { e.printStackTrace(); } 

		EnergyMeasurement m = new EnergyMeasurement(firstPoint.getTimestamp(),lastPoint.getTimestamp(),result);

	    return m;
	}
	
	// avoid running through the whole file
	private int getfirstLine(String metricName, Instant startTime,Instant endTime, long numberOfPoints) {
		int result=1;
		Instant t0=null;
		File file = new File(ROOT_PATH+"/"+metricName+".csv"); 
	    try { 
	        CSVReader csvReader = new CSVReader(new FileReader(file)); 
	        csvReader.readNext(); // ignore header

	        String[] nextRecord = csvReader.readNext();
	        t0 = Instant.parse(nextRecord[0]);
	    } 
	    catch (IOException e) { e.printStackTrace(); return -1; } 
	    catch (NullPointerException e) { return -1; } 
	    
        result=(int) Math.abs(Duration.between(t0, startTime).toMillis()/(FIXED_DELAY+3));
		
        if(endTime.compareTo(t0)<=0 )
			return -1;
        else if(startTime.compareTo(t0)<=0 || result==0 )
			result=1;
		else if(result>numberOfPoints)
			result=-1;
	


		return result;
		
	}
	
	public ByteArrayResource getFile(String metricName) {
		File file = new File(ROOT_PATH+metricName+".csv"); 
		Path path = Paths.get(file.getAbsolutePath());
	    ByteArrayResource resource;
		try {
			resource = new ByteArrayResource(Files.readAllBytes(path));
			return resource;
		} catch (IOException e) {
			return null;
		}
		
	}
	public long fileLength(String metricName) {
		File file = new File(ROOT_PATH+metricName+".csv"); 
		return file.length();
	}
}
