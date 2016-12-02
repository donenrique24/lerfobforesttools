/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2012 Mathieu Fortin for LERFOB INRA/AgroParisTech, 
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lerfob.carbonbalancetool.CATCompartmentManager;
import lerfob.carbonbalancetool.CATSettings;
import lerfob.carbonbalancetool.productionlines.EndUseWoodProductCarbonUnitFeature.UseClass;
import repicea.simulation.processsystem.AmountMap;
import repicea.simulation.processsystem.ProcessUnit;
import repicea.simulation.processsystem.Processor;


/**
 * A EndUseWoodProductCarbonUnit object results from a TreeLogger job. Each EndProduct object is given its lca member 
 * and its UseClass member by a EndProductFeature object contained in the TreeLogCategory object
 * @author M.Fortin - May 2010
 */
public class EndUseWoodProductCarbonUnit extends CarbonUnit {

	/**
	 * Raw volume in m3 of wood material
	 */
	@Deprecated
	private double rawRoundWoodVolume;	// this member is required in order to calculate the emissions with respect to the input round wood volume
	
	/**
	 * The constructor of this class.
	 * @param initialVolumeBeforeFirstTransformation the volume before the processing (double) (m3)
	 * @param dateIndex the creation date index of the time scale
	 * @param carbonUnitFeature a EndProductFeature instance that defines the end product
	 */
	@Deprecated
	protected EndUseWoodProductCarbonUnit(double initialVolumeBeforeFirstTransformation,
			int dateIndex,
			EndUseWoodProductCarbonUnitFeature carbonUnitFeature,
			AmountMap<Element> amountMap) {
		super(dateIndex, "", carbonUnitFeature, amountMap);
		this.rawRoundWoodVolume = initialVolumeBeforeFirstTransformation;
	}
	
	/**
	 * The constructor of this class.
	 * @param initialVolumeBeforeFirstTransformation the volume before the processing (double) (m3)
	 * @param dateIndex the creation date index of the time scale
	 * @param carbonUnitFeature a EndProductFeature instance that defines the end product
	 */
	protected EndUseWoodProductCarbonUnit(int dateIndex,
			String sampleUnitID,
			EndUseWoodProductCarbonUnitFeature carbonUnitFeature,
			AmountMap<Element> amountMap) {
		super(dateIndex, sampleUnitID, carbonUnitFeature, amountMap);
		addStatus(CarbonUnitStatus.EndUseWoodProduct);
	}

	/**
	 * This method returns the volume of the product as it was created.
	 * @return a double
	 */
	public double getProcessedVolumeAtCreationDate() {return getAmountMap().get(Element.Volume);}
	
	/**
	 * This method returns the dry biomass of the product as it was created.
	 * @return a double
	 */
	public double getBiomassAtCreationDate() {return getAmountMap().get(Element.Biomass);}
	
	/**
	 * This method returns the number of functional units in this carbon unit.
	 * @return a double
	 */
	public double getNumberOfFunctionalUnits() {
		if (getCarbonUnitFeature().getBiomassOfFunctionalUnit() != 0d) {
			return getBiomassAtCreationDate() / getCarbonUnitFeature().getBiomassOfFunctionalUnit();
		} else {
			return 0d;
		}
	}
	
	/**
	 * This method actualizes the EndProduct instance on a basis that is specified through the time scale parameter. Landfill products are retrieved 
	 * through a static collection in the manager.
	 * @param decayFunction the DecayFunction instance that serves to actualize the product
	 * @param timeScale a Array of integer that defines the time span over which the product is actualized
	 */
	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	@Override
	protected void actualizeCarbon(CATCompartmentManager compartmentManager) throws Exception {
		super.actualizeCarbon(compartmentManager);

		if (getCarbonUnitFeature().isDisposed()) {
			double[] releasedCarbonArray = getReleasedCarbonArray();
			double proportion;
			for (int i = getIndexInTimeScale(); i < getTimeTable().size(); i++) {
				proportion = releasedCarbonArray[i] / getInitialCarbon();
				AmountMap<Element> updatedMap = getAmountMap().multiplyByAScalar(proportion * getCarbonUnitFeature().getDisposableProportion());
//				int creationDate = compartmentManager.getTimeScale()[i];
				Processor disposedToProcessor = ((ProductionLineProcessor) getCarbonUnitFeature().getProcessor()).disposedToProcessor;
				if (updatedMap.get(Element.Volume) > 0) {
					if (disposedToProcessor != null) { // new implementation
						List<ProcessUnit> disposedUnits = new ArrayList<ProcessUnit>();
						disposedUnits.add(new CarbonUnit(i, samplingUnitID, null, updatedMap));
						Collection<CarbonUnit> processedUnits = (Collection) disposedToProcessor.doProcess(disposedUnits);
						for (CarbonUnit carbonUnit : processedUnits) {
							if (carbonUnit.getLastStatus().equals(CarbonUnitStatus.EndUseWoodProduct)) {
								carbonUnit.addStatus(CarbonUnitStatus.Recycled);
							}
						}
						compartmentManager.getCarbonToolSettings().getCurrentProductionProcessorManager().getCarbonUnitMap().add(processedUnits);
					} else {	// former implementation
						((ProductionLineProcessor) getCarbonUnitFeature().getProcessor()).getProductionLine().getManager().sendToTheLandfill(i, updatedMap);	
					}
				}
			}
		}
	}
	
	
	private boolean isNewImplementation() {
		return rawRoundWoodVolume == 0d;
	}
	
	/**
	 * This method returns the total substitution effect of this product. NOTE: substitution is given in terms of tC eq. / raw volume
	 * @param manager the CarbonCompartmentManager instance
	 * @return the substitution in tC (double)
	 */
	public double getTotalCarbonSubstitution(CATCompartmentManager manager) {
		if (isNewImplementation()) {
			return getSubstitutionPerFunctionalUnit(getNumberOfFunctionalUnits(), manager);
		} else {	// former implementation
			return getSubstitutionPerFunctionalUnit(rawRoundWoodVolume, null);
		}
	}

	/**
	 * This method returns an array that contains the substitution for specific lost volumes. NOTE : returns
	 * null if the product is not actualized.
	 * @param manager the CarbonCompartmentManager instance
	 * @return an array of double
	 */
	public double[] getCurrentCarbonSubstitution(CATCompartmentManager manager) {
		if (isActualized()) {
			if (isNewImplementation()) {
				double[] outputArray = new double[getTimeTable().size()];
				outputArray[getIndexInTimeScale()] = getTotalCarbonSubstitution(manager);
				return outputArray;
			} else {
				double ratioToGetRawRoundWoodVolume = rawRoundWoodVolume / getProcessedVolumeAtCreationDate();
				double volumeFactor = getProcessedVolumeAtCreationDate() / getInitialCarbon();
				double[] releasedCarbonArray = getReleasedCarbonArray();
				double[] outputArray = new double[getTimeTable().size()];
				double volume;
				for (int i = 0; i < getTimeTable().size(); i++) {
					volume = releasedCarbonArray[i] * volumeFactor;
					volume *= ratioToGetRawRoundWoodVolume;
					outputArray[i] = getSubstitutionPerFunctionalUnit(volume, null);	// since the substitution is given as a ratio between tC eq : roundWoodVolume in m3.
				}
				return outputArray;
			}
		} else {
			return null;
		}
	}

	

	/**
	 * This method returns the use class of this EndProduct instance.
	 * @return the use class (UseClass)
	 */
	public UseClass getUseClass() {return getCarbonUnitFeature().getUseClass();}
	

	/**
	 * This method computes the carbon emissions related to the processing of 
	 * this end product.
	 * @return the carbon emissions in tC Eq. (double) (emissions denoted by negative values)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public double getTotalNonRenewableCarbonEmissionsMgCO2Eq() {
		if (getAmountMap().containsKey(Element.EmissionsCO2Eq)) {		// new implementation
			return super.getTotalNonRenewableCarbonEmissionsMgCO2Eq();
		} else {			// former implementation
			double emission = 0d;
			if (getCarbonUnitFeature().getLCA() != null) {		// lca is supposed to be instantiated at creation date
				emission = - getCarbonUnitFeature().getLCA().getCarbonEmissionPerM3() * rawRoundWoodVolume;
			}
			return emission;
		}
	}
	

	@Override
	protected void addProcessUnit(ProcessUnit<Element> carbonUnit) {
		super.addProcessUnit(carbonUnit);
		this.rawRoundWoodVolume += ((EndUseWoodProductCarbonUnit) carbonUnit).rawRoundWoodVolume;
	}

	/**
	 * This method returns the substitution in C eq.
	 * @param manager a CATCompartmentManager instance
	 * @return the substitution in C eq. (double)
	 */
	@SuppressWarnings("deprecation")
	private double getSubstitutionPerFunctionalUnit(double nbFunctionalUnits, CATCompartmentManager manager) {
		if (isNewImplementation()) {
			return nbFunctionalUnits * getCarbonUnitFeature().getSubstitutionCO2EqFunctionalUnit(manager) * CATSettings.CO2_C_FACTOR; // Conversion to C eq.
		} else {
			return nbFunctionalUnits * getCarbonUnitFeature().getAverageSubstitution();
		}
	}

	@Override
	protected EndUseWoodProductCarbonUnitFeature getCarbonUnitFeature() {
		return (EndUseWoodProductCarbonUnitFeature) super.getCarbonUnitFeature();
	}
	
	
}