package com.example.demo.controller;



import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.example.demo.Domain.Point;
import com.example.demo.Domain.EnergyResponse;
import com.example.demo.Domain.EnergyMeasurement;
import com.example.demo.service.DiskPersistance;
import com.example.demo.service.EnergyService;

@RestController
public class MainController {

	@Autowired
	EnergyService energyService;
	
	@Autowired
	DiskPersistance diskPersistance;
	
	
	@Value( "${fixedDelay.in.milliseconds}" )
	private int FIXED_DELAY;
	
	@Value( "${MAX_POINTS}" )
	private int MAX_POINTS;
	
	@Value( "${POWERCAP_DIRECTORY}" )
	String POWERCAP_DIRECTORY;
	
	// gather this metric = true
	@Value( "${PKG0.enabled}" )
	Boolean PKG0_enabled;
	@Value( "${PKG0.core.enabled}" )
	Boolean PKG0_core_enabled;
	@Value( "${PKG0.uncore.enabled}" )
	Boolean PKG0_uncore_enabled;
	@Value( "${PKG1.enabled}" )
	Boolean PKG1_enabled;
	@Value( "${PKG1.core.enabled}" )
	Boolean PKG1_core_enabled;
	@Value( "${PKG1.uncore.enabled}" )
	Boolean PKG1_uncore_enabled;
	@Value( "${DRAM.enabled}" )
	Boolean DRAM_enabled;
	
	@Value( "${persistance.disk.enabled}" )
	Boolean DISK_enabled;
	@Value( "${log.path}" )
	String log_path;

	HashMap<String,LinkedList<Point> > listMap = new HashMap<String,LinkedList<Point>>();

	int WritingToDiskcounter=0;
	long numberOfPoints=0;

	@PostConstruct
	public void initialize()  {
			WritingToDiskcounter=MAX_POINTS;
			energyService.initialization(MAX_POINTS, POWERCAP_DIRECTORY);
			diskPersistance.initialization(log_path, FIXED_DELAY, energyService.getMaxEnergy());
			
			if(PKG0_enabled) 		listMap.put("pkg0", new LinkedList<Point>());
			if(PKG0_core_enabled) 	listMap.put("pkg0_core", new LinkedList<Point>());
			if(PKG0_uncore_enabled) listMap.put("pkg0_uncore", new LinkedList<Point>());
			
			if(PKG1_enabled) 		listMap.put("pkg1", new LinkedList<Point>());
			if(PKG1_core_enabled) 	listMap.put("pkg1_core", new LinkedList<Point>());
			if(PKG1_uncore_enabled)	listMap.put("pkg1_uncore", new LinkedList<Point>());
			
			if(DRAM_enabled)		listMap.put("dram", new LinkedList<Point>());
			
			if(DISK_enabled)
				for(String key : listMap.keySet())
					diskPersistance.createCSV(key);
			
	}
	
	
	

	@Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
	public void scheduleFixedDelayTask() {
		
		for(String key : listMap.keySet())
			energyService.addNewPoint( key, listMap.get(key));
		
		if(DISK_enabled && ((--WritingToDiskcounter) ==0) ) {
				for(String key : listMap.keySet())
					diskPersistance.appendList(key, listMap.get(key));
				numberOfPoints+= (WritingToDiskcounter=MAX_POINTS);
		}
	}



	@GetMapping("/getEnergy")
	 public Object getEnergy (@RequestParam(required = false) Instant startTime, 
			 				  @RequestParam(required = false) Instant endTime,
			 				 @RequestParam(required = false, defaultValue ="10") int interval,
			 				@RequestParam(required = false, defaultValue ="false") boolean pkg0,
		 					@RequestParam(required = false, defaultValue ="false") boolean pkg0_core,
		 					@RequestParam(required = false, defaultValue ="false") boolean pkg0_uncore,
		 					@RequestParam(required = false, defaultValue ="false") boolean pkg1,
		 					@RequestParam(required = false, defaultValue ="false") boolean pkg1_core,
		 					@RequestParam(required = false, defaultValue ="false") boolean pkg1_uncore,
		 					@RequestParam(required = false, defaultValue ="false") boolean dram)   {
		System.out.println("**********");
		System.out.println("st:"+startTime+" - et:"+endTime);
		if(endTime==null && startTime==null) {
			startTime= (endTime = Instant.now()).minusSeconds(interval);
		}else if(startTime==null) startTime= endTime.minusSeconds(interval);
		else endTime= startTime.plusSeconds(interval);
		System.out.println("st:"+startTime+" - et:"+endTime);

		HashMap<String,LinkedList<Point> > filteredListMap = new HashMap<String,LinkedList<Point>>();
		HashMap<String,EnergyMeasurement > mapMeasurement = new HashMap<String,EnergyMeasurement>();
				
		if(pkg0) {
			filteredListMap.put("pkg0", energyService.filterList(startTime, endTime, listMap.get("pkg0")));
			mapMeasurement.put("pkg0", energyService.getEnergyConsumption(filteredListMap.get("pkg0")));
		}if(pkg0_core) {
			filteredListMap.put("pkg0_core", energyService.filterList(startTime, endTime, listMap.get("pkg0_core")));
			mapMeasurement.put("pkg0_core", energyService.getEnergyConsumption(filteredListMap.get("pkg0_core")));
		}if(pkg0_uncore) {
			filteredListMap.put("pkg0_uncore", energyService.filterList(startTime, endTime, listMap.get("pkg0_uncore")));
			mapMeasurement.put("pkg0_uncore", energyService.getEnergyConsumption(filteredListMap.get("pkg0_uncore")));
		}if(pkg1) {
			filteredListMap.put("pkg1", energyService.filterList(startTime, endTime, listMap.get("pkg1")));
			mapMeasurement.put("pkg1", energyService.getEnergyConsumption(filteredListMap.get("pkg1")));
		}if(pkg1_core) {
			filteredListMap.put("pkg1_core", energyService.filterList(startTime, endTime, listMap.get("pkg1_core")));
			mapMeasurement.put("pkg1_core", energyService.getEnergyConsumption(filteredListMap.get("pkg1_core")));
		}if(pkg1_uncore) {
			filteredListMap.put("pkg1_uncore", energyService.filterList(startTime, endTime, listMap.get("pkg1_uncore")));
			mapMeasurement.put("pkg1_uncore", energyService.getEnergyConsumption(filteredListMap.get("pkg1_uncore")));
		}if(dram) {
			filteredListMap.put("dram", energyService.filterList(startTime, endTime, listMap.get("dram")));
			mapMeasurement.put("dram", energyService.getEnergyConsumption(filteredListMap.get("dram")));
		}
		EnergyResponse energyResponse = new EnergyResponse(
				mapMeasurement.get("pkg0"), 
				mapMeasurement.get("pkg0_core"), 
				mapMeasurement.get("pkg0_uncore"), 
				mapMeasurement.get("pkg1"), 
				mapMeasurement.get("pkg1_core"), 
				mapMeasurement.get("pkg1_uncore"), 
				mapMeasurement.get("dram")
				);

		return new ResponseEntity<>(energyResponse, HttpStatus.OK);
	}
	
	@GetMapping("/getEnergy_fromDisk")
	 public Object getEnergy_fromDisk (@RequestParam(required = false) Instant startTime, 
			 				  @RequestParam(required = false) Instant endTime,
			 				 @RequestParam(required = false, defaultValue ="10") int interval,
			 				@RequestParam(required = false, defaultValue ="false") boolean pkg0,
		 					@RequestParam(required = false, defaultValue ="false") boolean pkg0_core,
		 					@RequestParam(required = false, defaultValue ="false") boolean pkg0_uncore,
		 					@RequestParam(required = false, defaultValue ="false") boolean pkg1,
		 					@RequestParam(required = false, defaultValue ="false") boolean pkg1_core,
		 					@RequestParam(required = false, defaultValue ="false") boolean pkg1_uncore,
		 					@RequestParam(required = false, defaultValue ="false") boolean dram)   {
		
		if(endTime==null && startTime==null) {
			startTime= (endTime = Instant.now().minusMillis(FIXED_DELAY*(MAX_POINTS-WritingToDiskcounter))).minusSeconds(interval);
		}else if(startTime==null) startTime= endTime.minusSeconds(interval);
		else endTime= startTime.plusSeconds(interval);

		HashMap<String,EnergyMeasurement > mapMeasurement = new HashMap<String,EnergyMeasurement>();

		if(pkg0) {
			EnergyMeasurement m = diskPersistance.getEnergyConsumption("pkg0", startTime, endTime, numberOfPoints);
			mapMeasurement.put("pkg0", m);
		}if(pkg0_core) {
			EnergyMeasurement m = diskPersistance.getEnergyConsumption("pkg0_core", startTime, endTime, numberOfPoints);
			mapMeasurement.put("pkg0_core", m);
		}if(pkg0_uncore) {
			EnergyMeasurement m = diskPersistance.getEnergyConsumption("pkg0_uncore", startTime, endTime, numberOfPoints);
			mapMeasurement.put("pkg0_uncore", m);
		}if(pkg1) {
			EnergyMeasurement m = diskPersistance.getEnergyConsumption("pkg1", startTime, endTime, numberOfPoints);
			mapMeasurement.put("pkg1", m);
		}if(pkg1_core) {
			EnergyMeasurement m = diskPersistance.getEnergyConsumption("pkg1_core", startTime, endTime, numberOfPoints);
			mapMeasurement.put("pkg1_core", m);
		}if(pkg1_uncore) {
			EnergyMeasurement m = diskPersistance.getEnergyConsumption("pkg1_uncore", startTime, endTime, numberOfPoints);
			mapMeasurement.put("pkg1_uncore", m);
		}if(dram) {
			EnergyMeasurement m = diskPersistance.getEnergyConsumption("dram", startTime, endTime, numberOfPoints);
			mapMeasurement.put("dram", m);
		}
		EnergyResponse energyResponse = new EnergyResponse(
				mapMeasurement.get("pkg0"), 
				mapMeasurement.get("pkg0_core"), 
				mapMeasurement.get("pkg0_uncore"), 
				mapMeasurement.get("pkg1"), 
				mapMeasurement.get("pkg1_core"), 
				mapMeasurement.get("pkg1_uncore"), 
				mapMeasurement.get("dram")
				);

		return new ResponseEntity<>(energyResponse, HttpStatus.OK);
	}
	
	@GetMapping("/getEnergyPoints")
	 public Object getEnergyPoints (@RequestParam(required = false) Instant startTime, 
			 					@RequestParam(required = false) Instant endTime,
			 					 @RequestParam(required = false, defaultValue ="10") int interval,
			 					@RequestParam(required = false, defaultValue ="false") boolean pkg0,
			 					@RequestParam(required = false, defaultValue ="false") boolean pkg0_core,
			 					@RequestParam(required = false, defaultValue ="false") boolean pkg0_uncore,
			 					@RequestParam(required = false, defaultValue ="false") boolean pkg1,
			 					@RequestParam(required = false, defaultValue ="false") boolean pkg1_core,
			 					@RequestParam(required = false, defaultValue ="false") boolean pkg1_uncore,
			 					@RequestParam(required = false, defaultValue ="false") boolean dram)   {
		
		if(endTime==null && startTime==null) {
			startTime= (endTime = Instant.now()).minusSeconds(interval);
		}else if(startTime==null) startTime= endTime.minusSeconds(interval);
		else endTime= startTime.plusSeconds(interval);
	
		HashMap<String,LinkedList<Point> > filteredListMap = new HashMap<String,LinkedList<Point>>();
		if(pkg0)
			filteredListMap.put("pkg0", energyService.filterList(startTime, endTime, listMap.get("pkg0")));
		if(pkg0_core)
			filteredListMap.put("pkg0_core", energyService.filterList(startTime, endTime, listMap.get("pkg0_core")));
		if(pkg0_uncore)
			filteredListMap.put("pkg0_uncore", energyService.filterList(startTime, endTime, listMap.get("pkg0_uncore")));
		if(pkg1)
			filteredListMap.put("pkg1", energyService.filterList(startTime, endTime, listMap.get("pkg1")));
		if(pkg1_core)
			filteredListMap.put("pkg1_core", energyService.filterList(startTime, endTime, listMap.get("pkg1_core")));
		if(pkg1_uncore)
			filteredListMap.put("pkg1_uncore", energyService.filterList(startTime, endTime, listMap.get("pkg1_uncore")));
		if(dram)
			filteredListMap.put("dram", energyService.filterList(startTime, endTime, listMap.get("dram")));
		
		return new ResponseEntity<>(filteredListMap, HttpStatus.OK);
	}	

	@GetMapping("/disable")
	 public Object disable( @RequestParam(required = false, defaultValue ="false") boolean pkg0,
			@RequestParam(required = false, defaultValue ="false") boolean pkg0_core,
			@RequestParam(required = false, defaultValue ="false") boolean pkg0_uncore,
			@RequestParam(required = false, defaultValue ="false") boolean pkg1,
			@RequestParam(required = false, defaultValue ="false") boolean pkg1_core,
			@RequestParam(required = false, defaultValue ="false") boolean pkg1_uncore,
			@RequestParam(required = false, defaultValue ="false") boolean dram,
			@RequestParam(required = false, defaultValue ="false") boolean DISK_enabled) {
		if(pkg0)	PKG0_enabled=false;
		if(pkg0_core) 	PKG0_core_enabled=false;
		if(pkg0_uncore) 	PKG0_uncore_enabled=false;
		if(pkg1) 	PKG1_enabled=false;
		if(pkg1_core) 	PKG1_core_enabled=false;
		if(pkg1_uncore) 	PKG1_core_enabled=false;
		if(dram) 	DRAM_enabled=false;
		if(DISK_enabled)
			this.DISK_enabled=false;
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@GetMapping("/activate")
	 public Object activate( @RequestParam(required = false, defaultValue ="false") boolean pkg0,
			@RequestParam(required = false, defaultValue ="false") boolean pkg0_core,
			@RequestParam(required = false, defaultValue ="false") boolean pkg0_uncore,
			@RequestParam(required = false, defaultValue ="false") boolean pkg1,
			@RequestParam(required = false, defaultValue ="false") boolean pkg1_core,
			@RequestParam(required = false, defaultValue ="false") boolean pkg1_uncore,
			@RequestParam(required = false, defaultValue ="false") boolean dram,
			@RequestParam(required = false, defaultValue ="false") boolean DISK_enabled) {
		if(pkg0)	PKG0_enabled=true;
		if(pkg0_core) 	PKG0_core_enabled=true;
		if(pkg0_uncore) 	PKG0_uncore_enabled=true;
		if(pkg1) 	PKG1_enabled=true;
		if(pkg1_core) 	PKG1_core_enabled=true;
		if(pkg1_uncore) 	PKG1_core_enabled=true;
		if(dram) 	DRAM_enabled=true;
		if(DISK_enabled)
			this.DISK_enabled=true;

		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	


	@GetMapping("/edit")
	 public Object edit( @RequestParam(required = false) int MAX_POINTS,
			 @RequestParam(required = false) String POWERCAP_DIRECTORY,
			 @RequestParam(required = false) boolean reset) {
		
		if(MAX_POINTS>0)
			this.MAX_POINTS=MAX_POINTS;
		if(POWERCAP_DIRECTORY!= null && !POWERCAP_DIRECTORY.isEmpty() )
			this.POWERCAP_DIRECTORY=POWERCAP_DIRECTORY;

		if(reset)
			//energyService.initialization(MAX_POINTS, POWERCAP_DIRECTORY);
			initialize();
		return HttpStatus.OK;
	}
	
	@GetMapping("/getFile")
	 public Object getFile(@RequestParam(required = false, defaultValue ="false") boolean pkg0,
				@RequestParam(required = false, defaultValue ="false") boolean pkg0_core,
				@RequestParam(required = false, defaultValue ="false") boolean pkg0_uncore,
				@RequestParam(required = false, defaultValue ="false") boolean pkg1,
				@RequestParam(required = false, defaultValue ="false") boolean pkg1_core,
				@RequestParam(required = false, defaultValue ="false") boolean pkg1_uncore,
				@RequestParam(required = false, defaultValue ="false") boolean dram
				)throws IOException 
	{
	
	    HttpHeaders headers = new HttpHeaders();
	    boolean ok = false;
	    if(pkg0) {
		    headers.set("Content-Disposition", "attachment; filename=" +"pkg0"+".csv");
		    ok=true;
		}else if(pkg0_core) {
		    headers.set("Content-Disposition", "attachment; filename=" +"pkg0_core"+".csv");
		    ok=true;
		}else if(pkg0_uncore) {
		    headers.set("Content-Disposition", "attachment; filename=" +"pkg0_uncore"+".csv");
		    ok=true;
		}else if(pkg1) {
		    headers.set("Content-Disposition", "attachment; filename=" +"pkg1"+".csv");
		    ok=true;
		}else if(pkg1_core) {
		    headers.set("Content-Disposition", "attachment; filename=" +"pkg1_core"+".csv");
		    ok=true;
		}else if(pkg1_uncore) {
		    headers.set("Content-Disposition", "attachment; filename=" +"pkg1_uncore"+".csv");
		    ok=true;
		}else if(dram) {
		    headers.set("Content-Disposition", "attachment; filename=" +"dram"+".csv");
		    ok=true;
		}

	    if(ok)
	    	return ResponseEntity.ok()
	            .headers(headers)
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(diskPersistance.getFile("dram"));
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		//return listMap.keySet();
	}
}
