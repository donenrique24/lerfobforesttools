package lerfob.predictor.thinners.frenchnfithinner2018;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lerfob.predictor.thinners.frenchnfithinner2018.FrenchNFIThinnerStandingPriceProvider.Species;
import lerfob.simulation.covariateproviders.standlevel.FrenchDepartmentProvider.FrenchDepartment;
import repicea.io.javacsv.CSVReader;
import repicea.simulation.covariateproviders.standlevel.SpeciesCompositionProvider.SpeciesComposition;
import repicea.util.ObjectUtility;

public class FrenchNFIThinnerPredictorTests {

	private List<FrenchNFIThinnerPlot> readPlots() {
		List<FrenchNFIThinnerPlot> plots = new ArrayList<FrenchNFIThinnerPlot>();
		
		String filename = ObjectUtility.getPackagePath(getClass()) + "testData.csv";
		
		CSVReader reader = null;
		
		try {
			reader = new CSVReader(filename);
			Object[] record;
			while ((record = reader.nextRecord()) != null) {
				String idp = record[0].toString();
				String targetSpeciesStr = record[1].toString();
				Species targetSpecies = Species.getSpeciesFromFrenchName(targetSpeciesStr);
				String forestType = record[2].toString().trim().toUpperCase();
				SpeciesComposition spComp = null;
				if (forestType.equals("RES")) {
					spComp = SpeciesComposition.ConiferDominated;
				} else if (forestType.equals("MIX")) {
					spComp = SpeciesComposition.Mixed;
				} else if (forestType.equals("FEU")) {
					spComp = SpeciesComposition.BroadleavedDominated;
				}
				if (spComp == null) {
					throw new InvalidParameterException("Impossible to determine the species composition!");
				}
				
				double slopeInclination = Double.parseDouble(record[3].toString());
				double basalAreaM2Ha = Double.parseDouble(record[4].toString());
				double stemDensityHa = Double.parseDouble(record[5].toString());
				String departmentCode = record[6].toString();
				FrenchDepartment department = FrenchDepartment.getDepartment(departmentCode);
				int underManagementInt = Integer.parseInt(record[7].toString());
				boolean underManagement = underManagementInt == 1;
				double pred = Double.parseDouble(record[8].toString());
				int year0 = Integer.parseInt(record[9].toString());
				int year1 = Integer.parseInt(record[10].toString());
				
				FrenchNFIThinnerPlot plot = new FrenchNFIThinnerPlotImpl(idp, 
						department.getFrenchRegion2016(), 
						basalAreaM2Ha, 
						stemDensityHa,	
						slopeInclination, 
						targetSpecies, 
						underManagement,
//						spComp,
						pred,
						year0,
						year1);
				
				plots.add(plot);
			}
			return plots;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
	}
	
	
	
	@Test
	public void testSASPredictions() {
		List<FrenchNFIThinnerPlot> plots = readPlots();
		FrenchNFIThinnerPredictor thinner = new FrenchNFIThinnerPredictor(false);

		int nbPlots = 0;
		for (FrenchNFIThinnerPlot plot : plots) {
//			if (plot.getSubjectId().equals("266830")) {
//				int z = 0;
//			}
			FrenchNFIThinnerPlotImpl p = (FrenchNFIThinnerPlotImpl) plot;
			double actual = thinner.predictEventProbability(plot, null, p.getYear0(), p.getYear1());
			double expected = p.getPredictedProbability();
//			if (Math.abs(actual - expected) > 1E-8) {
//				int u = 0;
//			}
			Assert.assertEquals(expected, actual, 1E-8);
			nbPlots++;
		}
		
		Assert.assertEquals(39404, nbPlots);
		
		System.out.println("Number of plots successfully tested for FrenchNFIThinnertests: " + nbPlots);
		
	}
	
	
	
	
}
