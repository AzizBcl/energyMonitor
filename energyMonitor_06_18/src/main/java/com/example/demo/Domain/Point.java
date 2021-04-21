package com.example.demo.Domain;

import java.time.Instant;





import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

 @NoArgsConstructor @AllArgsConstructor 

public @Data class Point {

	private Instant timestamp ;
	private long energy_uj ;
	
	public Point(long energy_uj) {
		this.energy_uj = energy_uj;
		this.timestamp = Instant.now();
	}
	
}
