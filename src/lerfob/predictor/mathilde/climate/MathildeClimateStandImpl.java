/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2016 Mathieu Fortin for LERFOB INRA/AgroParisTech, 
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
package lerfob.predictor.mathilde.climate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import repicea.simulation.HierarchicalLevel;

class MathildeClimateStandImpl implements MathildeClimateStand {

	final String name;
	final double meanAnnualTempAbove6C;
	final int dateYr;
	final double xCoord;
	final double yCoord;
	final double pred;
	
	MathildeClimateStandImpl(String name, double xCoord, double yCoord, int dateYr, double meanAnnualTempAbove6C, double pred) {
		this.name = name;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.dateYr = dateYr;
		this.meanAnnualTempAbove6C = meanAnnualTempAbove6C;
		this.pred = pred;
	}
	
	@Override
	public String getSubjectId() {
		return name;
	}

	@Override
	public HierarchicalLevel getHierarchicalLevel() {return HierarchicalLevel.PLOT;}

	@Override
	public int getDateYr() {
		return dateYr;
	}


	protected double getPrediction() {return pred;}

	@Override
	public int getMonteCarloRealizationId() {return 0;}

	
	@Override
	public double getLatitudeDeg() {return yCoord;}

	@Override
	public double getLongitudeDeg() {return xCoord;}

	@Override
	public double getElevationM() {
		return 0;
	}

	@Override
	public List<MathildeClimateStand> getAllMathildeClimateStands() {
		Map<String, MathildeClimateStand> standMap = new TreeMap<String, MathildeClimateStand>(); 
		for (MathildeClimateStand s : MathildeClimatePredictor.getReferenceStands()) {
			MathildeClimateStandImpl stand = (MathildeClimateStandImpl) s;
			if (!standMap.containsKey(stand.name)) {
				standMap.put(stand.name, stand);
			}
		}
		List<MathildeClimateStand> stands = new ArrayList<MathildeClimateStand>();
		stands.addAll(standMap.values());
		return stands;
	}


}
