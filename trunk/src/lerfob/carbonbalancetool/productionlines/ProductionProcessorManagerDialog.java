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
package lerfob.carbonbalancetool.productionlines;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import lerfob.carbonbalancetool.productionlines.LandfillProcessor.LandfillProcessorButton;
import lerfob.carbonbalancetool.productionlines.LeftInForestProcessor.LeftInForestProcessorButton;
import lerfob.carbonbalancetool.productionlines.LogCategoryProcessor.LogCategoryProcessorButton;
import lerfob.carbonbalancetool.productionlines.ProductionProcessorToolPanel.CreateEndOfLifeLinkButton;
import lerfob.carbonbalancetool.productionlines.ProductionProcessorToolPanel.CreateLandfillProcessorButton;
import lerfob.carbonbalancetool.productionlines.ProductionProcessorToolPanel.CreateLeftInForestProcessorButton;
import lerfob.carbonbalancetool.productionlines.ProductionProcessorToolPanel.CreateProductionLineProcessorButton;
import repicea.gui.AutomatedHelper;
import repicea.gui.CommonGuiUtility;
import repicea.gui.OwnedWindow;
import repicea.gui.REpiceaAWTProperty;
import repicea.gui.UIControlManager;
import repicea.gui.components.REpiceaComboBoxOpenButton;
import repicea.net.BrowserCaller;
import repicea.simulation.processsystem.SystemLayout;
import repicea.simulation.processsystem.SystemManagerDialog;
import repicea.simulation.processsystem.SystemPanel;
import repicea.simulation.processsystem.UISetup;
import repicea.simulation.treelogger.TreeLoggerParameters;
import repicea.simulation.treelogger.TreeLoggerParametersDialog;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

@SuppressWarnings("serial")
public class ProductionProcessorManagerDialog extends SystemManagerDialog implements ItemListener, OwnedWindow, PropertyChangeListener {
	
	static {
		UISetup.Icons.put(CreateLeftInForestProcessorButton.class.getName(), CommonGuiUtility.retrieveIcon(ProductionProcessorManagerDialog.class, "IconLeftInForest.png"));
		UISetup.Icons.put(LeftInForestProcessorButton.class.getName(), CommonGuiUtility.retrieveIcon(ProductionProcessorManagerDialog.class, "IconLeftInForest.png"));
		UISetup.Icons.put(CreateLandfillProcessorButton.class.getName(), CommonGuiUtility.retrieveIcon(ProductionProcessorManagerDialog.class, "IconLandfillSite.png"));
		UISetup.Icons.put(LandfillProcessorButton.class.getName(), CommonGuiUtility.retrieveIcon(ProductionProcessorManagerDialog.class, "IconLandfillSite.png"));
		UISetup.Icons.put(CreateEndOfLifeLinkButton.class.getName(),CommonGuiUtility.retrieveIcon(ProductionProcessorManagerDialog.class, "endOfLifelinkIcon.png"));
		UISetup.Icons.put(LogCategoryProcessorButton.class.getName(), CommonGuiUtility.retrieveIcon(ProductionProcessorManagerDialog.class, "logIcon.png"));
		UIControlManager.setTitle(ProductionProcessorManagerDialog.class, "Harvested Wood Products", "Bois recolt\u00E9s");
		
		try {
			Method callHelp = BrowserCaller.class.getMethod("openUrl", String.class);
			String url = "http://www.inra.fr/capsis/help_"+ 
					REpiceaTranslator.getCurrentLanguage().getLocale().getLanguage() +
					"/capsis/extension/modeltool/productionprocessormanager";
			AutomatedHelper helper = new AutomatedHelper(callHelp, new Object[]{url});
			UIControlManager.setHelpMethod(ProductionProcessorManagerDialog.class, helper);
		} catch (Exception e) {}

	}


	public static enum MessageID implements TextableEnum {
		LandFillMarketLabel("Landfill", "D\u00E9charge"),
		LeftInForestLabel("Dead organic matter", "Mati\u00E8re organique morte"),
		ProductionLineFileExtension("production lines file (*.prl)", "fichier de lignes de production (*.prl)"),
		Default("Default","D\u00E9faut"),
		BuckingModelLabel("Bucking model", "Mod\u00E8le de billonnage"),
		ProcessorButtonToolTip("Create a processor", "Cr\u00E9er un transformateur"),
		LandfillButtonToolTip("Create a landfill site", "Cr\u00E9er une d\u00E9charge"),
		LeftInForestButtonToolTip("Leave on forest floor", "Laisser en for\u00EAt"),
		EndOfLifeLinkButtonToolTip("End of life destination", "Destination en fin de vie"),
		;
		
		MessageID(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}

		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString (this, englishText, frenchText);
		}

		@Override
		public String toString() {
			return REpiceaTranslator.getString(this);
		}
	}

	static {
		UISetup.ToolTips.put(CreateProductionLineProcessorButton.class.getName(), MessageID.ProcessorButtonToolTip.toString());
		UISetup.ToolTips.put(CreateEndOfLifeLinkButton.class.getName(), MessageID.EndOfLifeLinkButtonToolTip.toString());
		UISetup.ToolTips.put(CreateLeftInForestProcessorButton.class.getName(), MessageID.LeftInForestButtonToolTip.toString());
		UISetup.ToolTips.put(CreateLandfillProcessorButton.class.getName(), MessageID.LandfillButtonToolTip.toString());
		
	}

	
	protected REpiceaComboBoxOpenButton<TreeLoggerParameters<?>> comboBoxPanel;
	private JComboBox<TreeLoggerParameters<?>> treeLoggerComboBox;
	
	
	/**
	 * Constructor.
	 * @param parent
	 * @param systemManager
	 */
	protected ProductionProcessorManagerDialog(Window parent, ProductionProcessorManager systemManager) {
		super(parent, systemManager);
	}
	
	@Override
	protected void setToolPanel() {
		toolPanel = new ProductionProcessorToolPanel(systemPanel);
	}
	

	@Override
	protected void init() {
		super.init();
		comboBoxPanel = new REpiceaComboBoxOpenButton<TreeLoggerParameters<?>>(MessageID.BuckingModelLabel.toString(), getCaller().getGUIPermission());
		treeLoggerComboBox = comboBoxPanel.getComboBox();
	}

	@Override
	protected void initUI() {
		super.initUI();
		getContentPane().add(comboBoxPanel, BorderLayout.NORTH);
		initTreeLoggerComboBox();
	}
	
	@Override
	protected ProductionProcessorManager getCaller() {
		return (ProductionProcessorManager) super.getCaller();
	}

	protected void initTreeLoggerComboBox() {
		Vector<TreeLoggerParameters<?>> modelValues = new Vector<TreeLoggerParameters<?>>();
		modelValues.addAll(getCaller().getAvailableTreeLoggerParameters());
		treeLoggerComboBox.setModel(new DefaultComboBoxModel<TreeLoggerParameters<?>>(modelValues));
	}

	@Override
	protected SystemPanel createSystemPanel() {
		return new ExtendedSystemPanel(getCaller(), createSystemLayout());
	}
	
	@Override
	protected SystemLayout createSystemLayout() {
		return new ExtendedSystemLayout();
	}
	
	@Override
	public void listenTo() {
		super.listenTo();
		treeLoggerComboBox.addItemListener(this);
		comboBoxPanel.addComboBoxEntryPropertyListener(this);
	}
	
	@Override
	public void doNotListenToAnymore() {
		super.doNotListenToAnymore();
		treeLoggerComboBox.removeItemListener(this);
		comboBoxPanel.removeComboBoxEntryPropertyListener(this);
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getSource().equals(treeLoggerComboBox) && arg0.getStateChange () == ItemEvent.SELECTED) {
			getCaller().setSelectedTreeLogger((TreeLoggerParameters<?>) treeLoggerComboBox.getSelectedItem());
			firePropertyChange(REpiceaAWTProperty.SynchronizeWithOwner, null, this);
			firePropertyChange(REpiceaAWTProperty.ActionPerformed, null, this);
		}
	}
	
	@Override
	public void synchronizeUIWithOwner() {
		super.synchronizeUIWithOwner();
		doNotListenToAnymore();
		initTreeLoggerComboBox();
		treeLoggerComboBox.setSelectedItem(getCaller().getSelectedTreeLoggerParameters());
		listenTo();
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getSource() != null) {
			if (TreeLoggerParametersDialog.class.isAssignableFrom(arg0.getSource().getClass()) && arg0.getPropertyName().equals(REpiceaAWTProperty.WindowAcceptedConfirmed.name())) {
				if (!getCaller().getSelectedTreeLoggerParameters().isParameterDialogCanceled()) {
					getCaller().setSelectedTreeLogger((TreeLoggerParameters<?>) treeLoggerComboBox.getSelectedItem());
					firePropertyChange(REpiceaAWTProperty.SynchronizeWithOwner, null, this);
					firePropertyChange(REpiceaAWTProperty.ActionPerformed, null, this);
				}
			}
			
		}
	}


}
