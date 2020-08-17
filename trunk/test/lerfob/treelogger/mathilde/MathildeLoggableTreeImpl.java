package lerfob.treelogger.mathilde;

import lerfob.treelogger.mathilde.MathildeLoggableTree;

class MathildeLoggableTreeImpl implements MathildeLoggableTree {


	@Override
	public double getCommercialUnderbarkVolumeM3() {
		return 1;
	}

	@Override
	public String getSpeciesName() {
		return getMathildeTreeSpecies().name();
	}

	@Override
	public MathildeTreeSpecies getMathildeTreeSpecies() {
		return MathildeTreeSpecies.QUERCUS;
	}

	@Override
	public double getDbhCm() {
		return 50d;
	}

}
