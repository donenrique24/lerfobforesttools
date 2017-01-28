/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2017 Mathieu Fortin for LERFOB INRA/AgroParisTech, 
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
package lerfob.predictor.dopalep;

import repicea.simulation.MonteCarloSimulationCompliantObject;
import repicea.simulation.covariateproviders.treelevel.BasalAreaLargerThanSubjectM2Provider;
import repicea.simulation.covariateproviders.treelevel.DbhCmProvider;

public interface DopalepTree extends DbhCmProvider, MonteCarloSimulationCompliantObject, BasalAreaLargerThanSubjectM2Provider {
	
	/**
	 * This method returns the basal area of the other trees.
	 * @return a double
	 */
	public double getBasalAreaM2HaOtherTrees();
}
