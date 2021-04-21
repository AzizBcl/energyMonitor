package com.example.demo.service;

import java.time.Instant;
import java.util.LinkedList;
import com.example.demo.Domain.EnergyMeasurement;
import com.example.demo.Domain.Point;


public interface EnergyService {

//	public Point getNewPKG0Point() ;
//	public Point getNewCorePoint() ;
//	public Point getNewUncorePoint();
//	public Point getNewDramPoint();
	public void addNewPoint( String metricName, LinkedList<Point> linkedList);
	public void addNewPkg0Point(LinkedList<Point> linkedList);
	public void addNewPkg0CorePoint(LinkedList<Point> linkedList) ;
	public void addNewPkg0UncorePoint(LinkedList<Point> linkedList);
	
	public void addNewDramPoint(LinkedList<Point> linkedList);
	
	public void addNewPkg1Point(LinkedList<Point> linkedList);
	public void addNewPkg1CorePoint(LinkedList<Point> linkedList);
	public void addNewPkg1UncorePoint(LinkedList<Point> linkedList);
	
	public long getMaxEnergy();
	public long setMaxEnergy();
	public void setPowerCapDirectory(String POWERCAP_DIRECTORY);
	public void initialization(int MAX_POINTS, String POWERCAP_DIRECTORY);

	public LinkedList<Point> filterList(Instant startTime, Instant endTime, LinkedList<Point> linkedList) ;
	public LinkedList<Point> filterList_strict(Instant startTime, Instant endTime, LinkedList<Point> linkedList) ;
	public EnergyMeasurement getEnergyConsumption(LinkedList<Point> linkedList) ;
}
