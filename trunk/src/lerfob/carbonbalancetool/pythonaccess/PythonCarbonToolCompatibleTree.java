/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2014 Mathieu Fortin for LERFOB AgroParisTech/INRA, 
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
package lerfob.carbonbalancetool.pythonaccess;

import lerfob.carbonbalancetool.BasicWoodDensityProvider;
import lerfob.carbonbalancetool.CarbonToolCompatibleTree;

/**
 * This internal class is actually a wrapper for the trees that are sent to the PythonAccessPoint class.
 * @author Mathieu Fortin - May 2014
 */
abstract class PythonCarbonToolCompatibleTree implements CarbonToolCompatibleTree, 
												BasicWoodDensityProvider  {

	final SpeciesType speciesType; 
	final AverageBasicDensity species;
	final TreeStatusPriorToLogging treeStatusPriorToLogging;
	StatusClass statusClass; 
	final double number; 
	final double rootsVolume;
	final double branchesVolume;
	final double trunkVolume;
	final double dbhCm;

	PythonCarbonToolCompatibleTree(SpeciesType speciesType, 
			AverageBasicDensity species, 
			TreeStatusPriorToLogging treeStatusPriorToLogging,
			StatusClass statusClass, 
			double dbhCm,
			double number, 
			double biomassRoots,
			double biomassTrunk,
			double biomassBranches) {
		this.speciesType = speciesType;
		this.species = species;
		this.treeStatusPriorToLogging = treeStatusPriorToLogging;
		setStatusClass(statusClass);
		this.dbhCm = dbhCm;
		this.number = number;
		this.branchesVolume = biomassBranches / species.getBasicDensity();
		this.rootsVolume = biomassRoots / species.getBasicDensity();
		this.trunkVolume = biomassTrunk / species.getBasicDensity();
	}
	
	
	@Override
	public double getNumber() {return number;}

	@Override
	public TreeStatusPriorToLogging getTreeStatusPriorToLogging() {return treeStatusPriorToLogging;}

	@Override
	public double getCommercialVolumeM3() {return trunkVolume;}

	@Override
	public String getSpeciesName() {return species.name();}

	@Override
	public void setStatusClass(StatusClass statusClass) {
		this.statusClass = statusClass;
	}

	@Override
	public StatusClass getStatusClass() {return statusClass;}

	@Override
	public SpeciesType getSpeciesType() {return speciesType;}

	@Override
	public double getBasicWoodDensity() {
		return species.getBasicDensity();
	}

}
