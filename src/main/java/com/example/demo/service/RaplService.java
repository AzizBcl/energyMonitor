package com.example.demo.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Scanner;

import org.springframework.stereotype.Service;

import com.example.demo.Domain.EnergyMeasurement;
import com.example.demo.Domain.Point;



@Service
public class RaplService implements EnergyService {

	String POWERCAP_DIRECTORY="/sys/class/powercap/";
	
	long MAX_ENERGY=-1;
	
	int MAX_POINTS=120;
	
	boolean pkg0_exists= false;
	boolean pkg0_core_exists= false;
	boolean pkg0_uncore_exists= false;
	boolean pkg1_exists= false;
	boolean dram_exists= false;
	boolean pkg1_core_exists= false;
	boolean pkg1_uncore_exists= false;

	public void initialization(int MAX_POINTS, String POWERCAP_DIRECTORY) {
		this.POWERCAP_DIRECTORY=POWERCAP_DIRECTORY;
		this.MAX_POINTS=MAX_POINTS;
		checkingFiles();
		setMaxEnergy();
		System.out.println("Initialization :");
		System.out.println("\t-checking files existance:");

		System.out.println("\t pkg0 :"+pkg0_exists+" | pkg0_core :"+pkg0_core_exists+" | pkg0_uncore :"+pkg0_uncore_exists);
		System.out.println("\t pkg1 :"+pkg1_exists+" | pkg1_core :"+pkg1_core_exists+" | pkg1_uncore :"+pkg1_uncore_exists);
		System.out.println("\t dram :"+dram_exists);
		System.out.println("\t MAX_POINTS :"+MAX_POINTS);
	}
	
	void checkingFiles() {
		pkg0_exists= new File(POWERCAP_DIRECTORY+"intel-rapl:0/energy_uj").exists();
		pkg0_core_exists= new File(POWERCAP_DIRECTORY+"intel-rapl:0:0/energy_uj").exists();
		pkg0_uncore_exists= new File(POWERCAP_DIRECTORY+"intel-rapl:0:1/energy_uj").exists();
		pkg1_exists= new File(POWERCAP_DIRECTORY+"intel-rapl:1/energy_uj").exists();
		pkg1_core_exists= new File(POWERCAP_DIRECTORY+"intel-rapl:1:0/energy_uj").exists();
		pkg1_uncore_exists= new File(POWERCAP_DIRECTORY+"intel-rapl:1:1/energy_uj").exists();
		dram_exists= new File(POWERCAP_DIRECTORY+"intel-rapl:0:2/energy_uj").exists();
	}
	
	public long getLongFromFile(String path) {
		File file = new File(path);
		Scanner scanner = null;
		try {scanner = new Scanner(file);} catch (FileNotFoundException e) { return -1L;}
		long result = scanner.nextLong();
		scanner.close();
		return result;
	}
	
//	public Point getNewPKG0Point() {
//		return new Point(Instant.now(),getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:0/energy_uj"));
//	}
//	public Point getNewCorePoint() {
//		return new Point(Instant.now(),getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:0:0/energy_uj"));
//	}
//	public Point getNewUncorePoint() {
//		return new Point(Instant.now(),getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:0:1/energy_uj"));
//	}
//	public Point getNewDramPoint() {
//		return new Point(Instant.now(),getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:0:2/energy_uj"));
//	}
	
	public void addNewPoint( String metricName, LinkedList<Point> linkedList) {
		switch(metricName) 
        { 
        	case "dram": 
        		addNewDramPoint(linkedList);
        		break; 
            case "pkg0": 
            	addNewPkg0Point(linkedList);
                break; 
            case "pkg0_core": 
            	addNewPkg0CorePoint(linkedList);
                break; 
            case "pkg0_uncore": 
            	addNewPkg0UncorePoint(linkedList);
                break; 
            case "pkg1": 
            	addNewPkg1Point(linkedList);
                break; 
            case "pkg1_core": 
            	addNewPkg1CorePoint(linkedList);
                break; 
            case "pkg1_uncore": 
            	addNewPkg1UncorePoint(linkedList);
                break; 
            default: 
                System.out.println(metricName+" - no match");
        }
	}
	
	public void addNewPkg0Point(LinkedList<Point> linkedList) {
		if(linkedList != null && pkg0_exists) {
			linkedList.add(new Point(getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:0/energy_uj")));
			if(linkedList.size()>MAX_POINTS) linkedList.remove();
		}
	}
	public void addNewPkg1Point(LinkedList<Point> linkedList) {
		if(linkedList != null && pkg1_exists) {
			linkedList.add(new Point(getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:1/energy_uj")));
			if(linkedList.size()>MAX_POINTS) linkedList.remove();
		}
	}
	
	public void addNewPkg0CorePoint(LinkedList<Point> linkedList) {
		if(linkedList != null && pkg0_core_exists) {
			linkedList.add(new Point(getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:0:0/energy_uj")));
			if(linkedList.size()>MAX_POINTS) linkedList.remove();
		}
	}
	public void addNewPkg1CorePoint(LinkedList<Point> linkedList) {
		if(linkedList != null && pkg1_core_exists) {
			linkedList.add(new Point(getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:1:0/energy_uj")));
			if(linkedList.size()>MAX_POINTS) linkedList.remove();
		}
	}
	public void addNewPkg0UncorePoint(LinkedList<Point> linkedList) {
		if(linkedList != null && pkg0_uncore_exists ) {
			linkedList.add(new Point(getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:0:1/energy_uj")));
			if(linkedList.size()>MAX_POINTS) linkedList.remove();
		}
	}
	public void addNewPkg1UncorePoint(LinkedList<Point> linkedList) {
		if(linkedList != null && pkg1_uncore_exists ) {
			linkedList.add(new Point(getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:1:1/energy_uj")));
			if(linkedList.size()>MAX_POINTS) linkedList.remove();
		}
	}
	public void addNewDramPoint(LinkedList<Point> linkedList) {
		if(linkedList != null && dram_exists){	
			linkedList.add(new Point(getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:0:2/energy_uj")));
			if(linkedList.size()>MAX_POINTS) linkedList.remove();
		}
	}
	
	public long getMaxEnergy() {
		return getLongFromFile(POWERCAP_DIRECTORY+"intel-rapl:0/max_energy_range_uj");
	}
	public long setMaxEnergy() {
		 return MAX_ENERGY=getMaxEnergy();
	}
	public void setPowerCapDirectory(String POWERCAP_DIRECTORY) {
		 this.POWERCAP_DIRECTORY=POWERCAP_DIRECTORY;
	}
	public LinkedList<Point> filterList_strict(Instant startTime, Instant endTime, LinkedList<Point> linkedList) {
		if(linkedList==null || linkedList.size()<2 ) return null;
		LinkedList<Point> list = new LinkedList<Point>();
		for (Point p: linkedList)
		{	
			if( endTime.compareTo(p.getTimestamp()  )<=0 )		break;
			if( startTime.compareTo(p.getTimestamp()  )<=0 )	list.add(p);
		}
		return list;
	}
	public LinkedList<Point> filterList(Instant startTime, Instant endTime, LinkedList<Point> linkedList) {
		if(linkedList==null || linkedList.size()<2 ) return null;
		LinkedList<Point> list = new LinkedList<Point>();
		long halfPeriod =  Math.abs(Duration.between(linkedList.get(0).getTimestamp(), linkedList.get(1).getTimestamp()).toMillis()/2);
		for (Point p: linkedList)
		{	
			Instant t = p.getTimestamp();

			if( endTime.compareTo( t )<=0 &&  Math.abs(Duration.between(endTime, t ).toMillis()) >halfPeriod)		
				break;
			if( startTime.compareTo( t )<=0 ||  Math.abs(Duration.between(startTime, t ).toMillis()) <halfPeriod)	
				list.add(p);
			
		}
		return list;
	}
	public EnergyMeasurement getEnergyConsumption(LinkedList<Point> linkedList) {
		if(linkedList==null || linkedList.size()<2 ) return null;
		long result=0;
		int overflows=0;
		Point previousPoint=linkedList.getFirst();
		
		for (Point p: linkedList) {
			if(p.getEnergy_uj() < previousPoint.getEnergy_uj())
				overflows++;
			previousPoint=p;
		}
		if(overflows>0)
			result=   MAX_ENERGY - linkedList.getFirst().getEnergy_uj()
					+ MAX_ENERGY * (overflows-1) 
					+ linkedList.getLast().getEnergy_uj();
		else 
			result =  linkedList.getLast().getEnergy_uj() - linkedList.getFirst().getEnergy_uj();
		EnergyMeasurement m = new EnergyMeasurement(linkedList.getFirst().getTimestamp(),linkedList.getLast().getTimestamp(),result);
		return m;
	}

	

}
