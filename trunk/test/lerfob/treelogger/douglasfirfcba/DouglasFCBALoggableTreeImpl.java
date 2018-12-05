package lerfob.treelogger.douglasfirfcba;


class DouglasFCBALoggableTreeImpl implements DouglasFCBALoggableTree {

	final double treeDbhCm;
	
	DouglasFCBALoggableTreeImpl(double treeDbhCm) {
		this.treeDbhCm = treeDbhCm;
	}
	
	@Override
	public double getNumber() {
		return 1;
	}

	@Override
	public double getCommercialVolumeM3() {
		return 1;
	}

	@Override
	public String getSpeciesName() {
		return Species.DouglasFir.name();
	}

	@Override
	public double getDbhCm() {
		return treeDbhCm;
	}

	@Override
	public double getPlotWeight() {
		return 1d;
	}

}
