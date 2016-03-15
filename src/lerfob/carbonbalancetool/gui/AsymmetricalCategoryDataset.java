/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2016 Mathieu Fortin for LERFOB AgroParisTech/INRA, 
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
package lerfob.carbonbalancetool.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JDialog;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.statistics.StatisticalCategoryDataset;

import repicea.math.Matrix;
import repicea.stats.estimates.MonteCarloEstimate;

/**
 * The AsymmetricalCategoryDataset class is the Dataset class required to draw histograms with
 * asymmetrical error bars.
 * @author Mathieu Fortin - March 2016
 *
 */
@SuppressWarnings("rawtypes")
public class AsymmetricalCategoryDataset implements StatisticalCategoryDataset, IntervalCategoryDataset {

	class MonteCarloEstimateWrapper {
		
		private final MonteCarloEstimate estimate;
		private final Color color;
		
		MonteCarloEstimateWrapper(MonteCarloEstimate estimate, Color color) {
			this.estimate = estimate;
			this.color = color;
		}
	}
	
	private final double percentile;

	private final List<Comparable> rowKeys; 
	private final List<Comparable> columnKeys;
	private final Map<Comparable, Map<Comparable, MonteCarloEstimateWrapper>> estimateMap;
	
	public AsymmetricalCategoryDataset(double percentile) {
		super();
		this.percentile = percentile;
		rowKeys = new ArrayList<Comparable>();
		columnKeys = new ArrayList<Comparable>();
		estimateMap = new HashMap<Comparable, Map<Comparable, MonteCarloEstimateWrapper>>();
	}

	public void add(MonteCarloEstimate estimate, Color color, Comparable category, Comparable group) {
		if (!estimateMap.containsKey(category)) {
			if (!rowKeys.contains(category)) {
				rowKeys.add(category);
			}
			estimateMap.put(category, new HashMap<Comparable, MonteCarloEstimateWrapper>());
		}
		Map<Comparable, MonteCarloEstimateWrapper> innerMap = estimateMap.get(category);
		if (!innerMap.containsKey(group)) {
			if (!columnKeys.contains(group)) {
				columnKeys.add(group);
			}
		}
		innerMap.put(group, new MonteCarloEstimateWrapper(estimate, color));
	}
	
	@Override
	public Number getEndValue(int arg0, int arg1) {
		if (arg0 >= 0 && arg0 < rowKeys.size()) {
			if (arg1 >= 0 && arg1 < columnKeys.size()) {
				Comparable rowComparable = rowKeys.get(arg0);
				Comparable columnComparable = columnKeys.get(arg1);
				return getEndValue(rowComparable, columnComparable);
			}
		}
		return null;
	}

	@Override
	public Number getEndValue(Comparable arg0, Comparable arg1) {
		MonteCarloEstimate estimate = getMonteCarloEstimate(arg0, arg1);
		if (estimate != null) {
			return estimate.getPercentile(1d - .5 * (1d - percentile)).m_afData[0][0];
		}
		return null;
	}

	@Override
	public Number getStartValue(int arg0, int arg1) {
		if (arg0 >= 0 && arg0 < rowKeys.size()) {
			if (arg1 >= 0 && arg1 < columnKeys.size()) {
				Comparable rowComparable = rowKeys.get(arg0);
				Comparable columnComparable = columnKeys.get(arg1);
				return getStartValue(rowComparable, columnComparable);
			}
		}
		return null;
	}

	@Override
	public Number getStartValue(Comparable arg0, Comparable arg1) {
		MonteCarloEstimate estimate = getMonteCarloEstimate(arg0, arg1);
		if (estimate != null) {
			return estimate.getPercentile(.5 * (1d - percentile)).m_afData[0][0];
		}
		return null;
	}

	@Override
	public int getColumnIndex(Comparable arg0) {return columnKeys.indexOf(arg0);}

	@Override
	public Comparable getColumnKey(int arg0) {return columnKeys.get(arg0);}

	@Override
	public List getColumnKeys() {
		List<Comparable> copyList = new ArrayList<Comparable>();
		copyList.addAll(columnKeys);
		return copyList;
	}

	@Override
	public int getRowIndex(Comparable arg0) {return rowKeys.indexOf(arg0);}

	@Override
	public Comparable getRowKey(int arg0) {return rowKeys.get(arg0);}

	@Override
	public List getRowKeys() {
		List<Comparable> copyList = new ArrayList<Comparable>();
		copyList.addAll(rowKeys);
		return copyList;
	}

	@Override
	public Number getValue(Comparable arg0, Comparable arg1) {
		MonteCarloEstimate estimate = getMonteCarloEstimate(arg0, arg1);
		if (estimate != null) {
			return estimate.getMean().m_afData[0][0];
		}
		return null;
	}
	
	protected final MonteCarloEstimate getMonteCarloEstimate(Comparable arg0, Comparable arg1) {
		MonteCarloEstimateWrapper wrapper = getWrapper(arg0, arg1);
		if (wrapper != null) { 
			return wrapper.estimate;
		}
		return null;
	}

	private MonteCarloEstimateWrapper getWrapper(Comparable arg0, Comparable arg1) {
		if (estimateMap.containsKey(arg0)) {
			if (estimateMap.get(arg0).containsKey(arg1)) {
				return estimateMap.get(arg0).get(arg1);
			}
		}
		return null;
	}
	
	protected final Color getColor(Comparable arg0, Comparable arg1) {
		MonteCarloEstimateWrapper wrapper = getWrapper(arg0, arg1);
		if (wrapper != null) { 
			return wrapper.color;
		}
		return null;
	}
	
	
	
	@Override
	public int getColumnCount() {return columnKeys.size();}

	@Override
	public int getRowCount() {return rowKeys.size();}

	@Override
	public Number getValue(int arg0, int arg1) {
		if (arg0 >= 0 && arg0 < rowKeys.size()) {
			if (arg1 >= 0 && arg1 < columnKeys.size()) {
				Comparable rowComparable = rowKeys.get(arg0);
				Comparable columnComparable = columnKeys.get(arg1);
				return getValue(rowComparable, columnComparable);
			}
		}
		return null;
	}

	@Override
	public void addChangeListener(DatasetChangeListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DatasetGroup getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeChangeListener(DatasetChangeListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGroup(DatasetGroup arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public Number getMeanValue(int arg0, int arg1) {
		return getValue(arg0, arg1);
	}

	@Override
	public Number getMeanValue(Comparable arg0, Comparable arg1) {
		return getValue(arg0, arg1);
	}

	@Override
	public Number getStdDevValue(int arg0, int arg1) {
		return .1;
	}

	@Override
	public Number getStdDevValue(Comparable arg0, Comparable arg1) {
		return .1;
	}
	
	public static void main(String[] arg) {
		AsymmetricalCategoryDataset dataset = new AsymmetricalCategoryDataset(0.95);
		Random random = new Random();
		
		MonteCarloEstimate estimate1 = new MonteCarloEstimate();
		MonteCarloEstimate estimate2 = new MonteCarloEstimate();
		
		Matrix mat1;
		Matrix mat2;
		for (int i = 0; i < 10000; i++) {
			mat1 = new Matrix(1,1);
			mat1.m_afData[0][0] = random.nextDouble();
			estimate1.addRealization(mat1);
			mat2 = new Matrix(1,1);
			mat2.m_afData[0][0] = random.nextDouble() + 2;
			estimate2.addRealization(mat2);
		}
		
		dataset.add(estimate1, Color.RED, "Estimate1", "group1");		
		dataset.add(estimate2, Color.GREEN, "Estimate2", "group1");		
		dataset.add(estimate2, Color.BLUE, "Estimate2", "group2");		
		
		JFreeChart chart = ChartFactory.createBarChart("My title", 
				"Labels", 
				"Values",
				dataset, 
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
				);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setRenderer(new EnhancedStatisticalBarRenderer());
		EnhancedStatisticalBarRenderer renderer = (EnhancedStatisticalBarRenderer) plot.getRenderer();

		renderer.setShadowVisible(true);
		renderer.setMaximumBarWidth(0.1);
		renderer.setColors(dataset);

		ChartPanel chartPanel = new ChartPanel(chart);
		
		JDialog dialog = new JDialog();
		dialog.setModal(true);
		dialog.getContentPane().add(chartPanel);
		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}


}
