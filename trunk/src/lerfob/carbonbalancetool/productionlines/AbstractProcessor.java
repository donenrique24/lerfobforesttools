/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2015 Mathieu Fortin for LERFOB AgroParisTech/INRA, 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package lerfob.carbonbalancetool.productionlines;

import java.util.Collection;
import java.util.List;

import lerfob.carbonbalancetool.productionlines.CarbonUnit.Element;
import repicea.simulation.processsystem.AmountMap;
import repicea.simulation.processsystem.ProcessUnit;
import repicea.simulation.processsystem.Processor;

@SuppressWarnings("serial")
public class AbstractProcessor extends Processor {

	protected double functionUnitBiomass;
	
	protected double emissionsByFunctionalUnit;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<ProcessUnit> doProcess(List<ProcessUnit> inputUnits) {
		for (ProcessUnit processUnit : inputUnits) {
			AbstractProcessor.updateProcessEmissions(processUnit.getAmountMap(), functionUnitBiomass, emissionsByFunctionalUnit);
		}
		return super.doProcess(inputUnits);
	}

	
	protected static void updateProcessEmissions(AmountMap<CarbonUnit.Element> amountMap, double functionalUnitBiomass, double emissionsByFunctionalUnit) {
		Double biomass = (Double) amountMap.get(Element.Biomass);
		if (biomass != null && functionalUnitBiomass > 0) {
			double fonctionalUnits = biomass * 1000 / functionalUnitBiomass;		// 1000 to report the biomass in kg since the functional unit is in kg 
			double emissions = fonctionalUnits * emissionsByFunctionalUnit * .001;	// .001 to report the CO2 emissions in tons and not in kg
			amountMap.add(Element.EmissionsCO2Eq, emissions);
		}

	}
	
}
