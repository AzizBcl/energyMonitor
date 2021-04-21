package com.example.demo.Domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor @AllArgsConstructor 
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public @Data class EnergyResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private EnergyMeasurement pkg0;
	private EnergyMeasurement pkg0_core;
	private EnergyMeasurement pkg0_uncore;

	private EnergyMeasurement pkg1;
	private EnergyMeasurement pkg1_core;
	private EnergyMeasurement pkg1_uncore;

	private EnergyMeasurement dram;
	

	public EnergyResponse(EnergyMeasurement pkg0, EnergyMeasurement dram) {
		this.pkg0=pkg0;
		this.dram=dram;
	}
}
