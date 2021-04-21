package com.example.demo.Domain;

import java.io.Serializable;
import java.time.Instant;



import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor @AllArgsConstructor 

@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public @Data class EnergyMeasurement implements Serializable {

	private static final long serialVersionUID = 1L;
		

	private int id ;

	private Instant startTime ;
	private Instant endTime ;
	private long energy_uj ;

	public EnergyMeasurement(Instant startTime, Instant endTime, long energy_uj) {
		this.startTime=startTime;
		this.endTime=endTime;
		this.energy_uj=energy_uj;
	}

}
