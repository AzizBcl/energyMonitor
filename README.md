# energyMonitor
Note : stills in BETA version. 

EnergyMonitor is a spring boot project using RAPL interface to estimate energy Consumption of CPU, Memory...
You'll need to modify application.properties to change the parameters.


                

# you can use it with : https://hub.docker.com/repository/docker/azizbcl/energymonitor

	energymonitor:
 	 image: azizbcl/energymonitor:latest
  	container_name: energymonitor
  	restart: always
  	ports:
    	- "9090:9091"
  	command: --server.port=9090 --log.path=/ --PKG0.enabled=true --PKG0.core.enabled=false --MAX_POINTS= 120 --fixedDelay.in.milliseconds=1000 --PKG0.uncore.enabled=false --PKG1.enabled=false --PKG1.core.enabled=false --PKG1.uncore.enabled=false --DRAM.enabled=true --persistance.disk.enabled=true





# How to call it : 

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
		 					@RequestParam(required = false, defaultValue ="false") boolean dram)  
              
              
#              OR
              

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
		 					@RequestParam(required = false, defaultValue ="false") boolean dram) 


#              OR
              
              
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
			 					@RequestParam(required = false, defaultValue ="false") boolean dram) 
                
                





