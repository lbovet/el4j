package ch.elca.el4j.demos.gui.fs.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import ch.elca.el4j.gui.swing.GUIApplication;

public class DatensammlungPanel extends JPanel {
	
	public DatensammlungPanel(GUIApplication app) {

		this.setLayout(new GridBagLayout());
		
		GridBagConstraints gbcHH = new GridBagConstraints();
		gbcHH.gridx = 0;
		gbcHH.gridy = 0;
		gbcHH.anchor = GridBagConstraints.NORTHWEST;
		gbcHH.fill = GridBagConstraints.HORIZONTAL;
		gbcHH.weightx = 1.0D;
		gbcHH.insets = new Insets(6, 6, 0, 6);
		add(new HaushaltPanel(), gbcHH);
		
		GridBagConstraints gbcVE = new GridBagConstraints();
		gbcVE.gridx = 0;
		gbcVE.gridy = 1;
		gbcVE.anchor = GridBagConstraints.NORTHWEST;
		gbcVE.fill = GridBagConstraints.HORIZONTAL;
		gbcVE.weightx = 1.0D;
		gbcVE.insets = new Insets(6, 6, 0, 6);
		add(new VEListPanel(), gbcVE);
		
		GridBagConstraints gbcKonto = new GridBagConstraints();
		gbcKonto.gridx = 0;
		gbcKonto.gridy = 2;
		gbcKonto.anchor = GridBagConstraints.NORTHWEST;
		gbcKonto.fill = GridBagConstraints.BOTH;
		gbcKonto.weightx = 1.0D;
		gbcKonto.weighty = 1.0D;
		gbcKonto.insets = new Insets(6, 6, 0, 6);
		add(new KontoListPanel(app), gbcKonto);
	}

}
