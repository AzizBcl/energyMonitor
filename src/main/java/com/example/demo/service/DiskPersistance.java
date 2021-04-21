package com.example.demo.service;

import java.time.Instant;
import java.util.LinkedList;

import org.springframework.core.io.ByteArrayResource;

import com.example.demo.Domain.EnergyMeasurement;
import com.example.demo.Domain.Point;

public interface DiskPersistance {

	public void createCSV( String metricName) ;
	public void initialization(String ROOT_PATH);
	public void initialization(String ROOT_PATH, int FIXED_DELAY) ;
	public void initialization(String ROOT_PATH, int FIXED_DELAY, long MAX_ENERGY);
	public void appendList( String metricName,LinkedList<Point> linkedList) ;
	public EnergyMeasurement getEnergyConsumption( String metricName, Instant startTime, Instant endTime, long numberOfPoints) ;
	public ByteArrayResource getFile(String metricName);
	public long fileLength(String metricName);
}
