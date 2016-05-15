package lerfob.carbonbalancetool;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

import repicea.gui.REpiceaDialog;
import repicea.gui.UIControlManager;
import repicea.gui.UIControlManager.CommonControlID;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;


@SuppressWarnings("serial")
public class CarbonCompareScenarioDialog extends REpiceaDialog implements ActionListener, ItemListener {

	static {
		UIControlManager.setTitle(CarbonCompareScenarioDialog.class, "Scenario comparison", "Comparaison de sc\u00E9narios");
	}
	
	private static enum MessageID implements TextableEnum {
		Baseline("Baseline", "Sc\u00E9nario de r\u00E9f\u00E9rence"),
		AlternativeScenario("Alternative scenario", "Sc\u00E9nario alternatif"),
		Scenario("Scenario", "Sc\u00E9nario"),
		ComparisonMode("Comparison mode", "Mode de comparaison"),
		Date("Date", "Date");
		
		MessageID(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}
		
		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}

		@Override
		public String toString() {
			return REpiceaTranslator.getString(this);
		}
		
	}
	
	private static enum ComparisonMode implements TextableEnum {
		PointEstimate("Between two point estimates", "Entre deux estimations finies"),
		InfiniteSequence("Infinite sequence", "En s\u00E9quence infinie");

		ComparisonMode(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}
		
		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}

		@Override
		public String toString() {return REpiceaTranslator.getString(this);}
		
	}
	
	private final CarbonAccountingToolPanelView panelView;
	
	private final JComboBox<CarbonAccountingToolSingleViewPanel> scenarioToCompareComboBox;
	private final JComboBox<CarbonAccountingToolSingleViewPanel> baselineComboBox;
	private final JComboBox<ComparisonMode> comparisonModeComboBox;
	private final JComboBox<Integer> dateRefComboBox;
	private final JComboBox<Integer> dateAltComboBox;
	
	private final JButton ok;
	private final JButton cancel;
	private final JButton help;
	
	private String selectionCompared;
	
	private final CarbonAccountingToolSingleViewPanel baselinePanel;
	
	
	protected CarbonCompareScenarioDialog(CarbonAccountingToolDialog parent, CarbonAccountingToolPanelView panelView) {
		super(parent);
		this.panelView = panelView;
		ok = UIControlManager.createCommonButton(CommonControlID.Ok);
		cancel = UIControlManager.createCommonButton(CommonControlID.Cancel);
		help = UIControlManager.createCommonButton(CommonControlID.Help);
		comparisonModeComboBox = new JComboBox<ComparisonMode>();
		scenarioToCompareComboBox = new JComboBox<CarbonAccountingToolSingleViewPanel>();
		dateRefComboBox = new JComboBox<Integer>();
		dateAltComboBox = new JComboBox<Integer>();
		baselinePanel = (CarbonAccountingToolSingleViewPanel) panelView.tabbedPane.getSelectedComponent();
		baselineComboBox = new JComboBox<CarbonAccountingToolSingleViewPanel>();
		baselineComboBox.addItem(baselinePanel);
		baselineComboBox.setEnabled(false);
		
		initUI();
		pack();
		setMinimumSize(getSize());
		setVisible(true);
	}


	private void setComparisonModeComboBox() {
		comparisonModeComboBox.removeAllItems();
		comparisonModeComboBox.addItem(ComparisonMode.PointEstimate);
		CarbonAccountingToolSingleViewPanel panel = (CarbonAccountingToolSingleViewPanel) panelView.tabbedPane.getSelectedComponent();
		if (panel.getSummary().isEvenAged()) {
			comparisonModeComboBox.addItem(ComparisonMode.InfiniteSequence);
		}
		comparisonModeComboBox.setSelectedIndex(0);
	}
	
	private void setDateComboBoxes() {
		dateRefComboBox.removeAllItems();
		if (!isComparisonModeInfiniteSequence()) {
			for (Integer date : baselinePanel.getSummary().getTimeTable().getListOfDatesUntilLastStandDate()) {
				dateRefComboBox.addItem(date);
			}
		}

		dateAltComboBox.removeAllItems();
		if (!isComparisonModeInfiniteSequence()) {
			CarbonAccountingToolSingleViewPanel altPanel = (CarbonAccountingToolSingleViewPanel) scenarioToCompareComboBox.getSelectedItem();
			for (Integer date : altPanel.getSummary().getTimeTable().getListOfDatesUntilLastStandDate()) {
				dateAltComboBox.addItem(date);
			}
		}
	}
	
	private void setComboBoxValues() {
		scenarioToCompareComboBox.removeAllItems();
		for (int i = 0; i < panelView.tabbedPane.getTabCount(); i++) {
//			if (i != panelView.tabbedPane.getSelectedIndex()) {
				CarbonAccountingToolSingleViewPanel panel = (CarbonAccountingToolSingleViewPanel) panelView.tabbedPane.getComponentAt(i);
				if (panel != null) {
					if (panel.getSummary() instanceof CarbonAssessmentToolSingleSimulationResult) { // to avoid comparing a difference with a scenario
						if (isComparisonModeInfiniteSequence()) {
							if (panel.getSummary().isEvenAged()) {
								scenarioToCompareComboBox.addItem(panel);
							}
						} else {
							scenarioToCompareComboBox.addItem(panel);
						}
					}
				}
//			}
		}
		checkCorrespondanceWith(scenarioToCompareComboBox, selectionCompared);
	}

	private boolean isComparisonModeInfiniteSequence() {
		return comparisonModeComboBox.getSelectedItem().equals(ComparisonMode.InfiniteSequence);
	}

	@Override
	public void listenTo() {
		ok.addActionListener(this);
		cancel.addActionListener(this);
		help.addActionListener(this);
		comparisonModeComboBox.addItemListener(this);
		scenarioToCompareComboBox.addItemListener(this);
	}

	@Override
	public void doNotListenToAnymore() {
		ok.removeActionListener(this);
		cancel.removeActionListener(this);
		help.removeActionListener(this);
		comparisonModeComboBox.removeItemListener(this);
		scenarioToCompareComboBox.removeItemListener(this);
	}

	
	@Override
	public void refreshInterface() {
		setComparisonModeComboBox();
		setComboBoxValues();
		setDateComboBoxes();
		super.refreshInterface();
	}
 	
	@Override
	public void setVisible(boolean bool) {
		if (!isVisible() && bool) {
			refreshInterface();
		}
		super.setVisible(bool);
	}
	
	@SuppressWarnings("rawtypes")
	private void checkCorrespondanceWith(JComboBox comboBox, String formerSelection) {
		int selectedIndex = 0;
		if (formerSelection != null) {
			for (int i = 0; i < comboBox.getItemCount(); i++) {
				CarbonAccountingToolSingleViewPanel panel = (CarbonAccountingToolSingleViewPanel) comboBox.getItemAt(i);
				if (panel.toString().equals(formerSelection)) {
					selectedIndex = i;
					break;
				}
			}
		}
		comboBox.setSelectedIndex(selectedIndex);
	}


	@Override
	protected void initUI() {
		setTitle(UIControlManager.getTitle(getClass()));
		setLayout(new BorderLayout());
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		controlPanel.add(ok);
		controlPanel.add(cancel);
		controlPanel.add(help);
		add(controlPanel, BorderLayout.SOUTH);

		JPanel comparisonModePanel = UIControlManager.createSimpleHorizontalPanel(MessageID.ComparisonMode, comparisonModeComboBox, 5, true);
		add(Box.createHorizontalStrut(5), BorderLayout.NORTH);
		add(comparisonModePanel, BorderLayout.NORTH);
		add(Box.createHorizontalStrut(5), BorderLayout.NORTH);
				
		JPanel mainPanel = new JPanel(new GridLayout(1,2));
		add(mainPanel, BorderLayout.CENTER);

		GridLayout gridLayout = new GridLayout(2,1);
		
		Border etched = BorderFactory.createEtchedBorder();
		JPanel baselinePane = new JPanel(gridLayout);
		baselinePane.setBorder(BorderFactory.createTitledBorder(etched, MessageID.Baseline.toString()));
		JPanel pane = UIControlManager.createSimpleHorizontalPanel(MessageID.Scenario, baselineComboBox, 5, false);
		baselinePane.add(pane);
		pane = UIControlManager.createSimpleHorizontalPanel(MessageID.Date,	dateRefComboBox, 5, false);
		baselinePane.add(pane);
		mainPanel.add(baselinePane);
		
		JPanel comparisonPane = new JPanel(gridLayout);
		comparisonPane.setBorder(BorderFactory.createTitledBorder(etched, MessageID.AlternativeScenario.toString()));
		pane = UIControlManager.createSimpleHorizontalPanel(MessageID.Scenario,	scenarioToCompareComboBox, 5, false);
		comparisonPane.add(pane);
		pane = UIControlManager.createSimpleHorizontalPanel(MessageID.Date, dateAltComboBox, 5, false);
		comparisonPane.add(pane);
		
		mainPanel.add(comparisonPane);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(cancel)) {
			cancelAction();
		} else if (e.getSource().equals(ok)) {
			okAction();
		} else if (e.getSource().equals(help)) {
			helpAction();
		}
		
	}


	@Override
	public void okAction() {
		CarbonAccountingToolSingleViewPanel scenToCompare = ((CarbonAccountingToolSingleViewPanel) scenarioToCompareComboBox.getSelectedItem());
		String simulationName = scenToCompare.toString() + " - " + baselinePanel.toString();

		CarbonAssessmentToolSingleSimulationResult base = (CarbonAssessmentToolSingleSimulationResult) baselinePanel.getSummary();
		Integer baseDate = (Integer) dateRefComboBox.getSelectedItem();

		CarbonAssessmentToolSingleSimulationResult altScen = (CarbonAssessmentToolSingleSimulationResult) scenToCompare.getSummary();
		Integer altScenDate = (Integer) dateAltComboBox.getSelectedItem();
				
		panelView.addSimulationResult(new CarbonAssessmentToolSimulationDifference(simulationName, base, baseDate, altScen, altScenDate));
		selectionCompared = scenToCompare.toString();
		super.okAction();
	}


	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getSource().equals(scenarioToCompareComboBox)) {
			System.out.println("scenarioToCompareComboBox changed");
			setDateComboBoxes();
		} else if (arg0.getSource().equals(comparisonModeComboBox)) {
			doNotListenToAnymore();
			System.out.println("comparisonModeComboBox changed");
			setComboBoxValues();
			setDateComboBoxes();
			listenTo();
		}
	}

}
