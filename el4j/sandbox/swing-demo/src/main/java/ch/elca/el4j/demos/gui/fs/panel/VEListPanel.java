package ch.elca.el4j.demos.gui.fs.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class VEListPanel extends JPanel {
	
	//private JTable m_tblVEListe = new JTable();
	private JScrollPane m_sclVEListe = new JScrollPane();
	private JButton m_btnBeendenHH = new JButton();


	public VEListPanel() {
	    this.setBorder(BorderFactory.createTitledBorder(
			"Verwaltungseinheitliste"));

		this.setLayout(new GridBagLayout());
		
		GridBagConstraints gbcSclVEListe = new GridBagConstraints();
		gbcSclVEListe.gridx = 0;
		gbcSclVEListe.gridy = 0;
		gbcSclVEListe.anchor = GridBagConstraints.NORTHWEST;
		gbcSclVEListe.fill = GridBagConstraints.HORIZONTAL;
		gbcSclVEListe.weightx = 1.0D;
		gbcSclVEListe.insets = new Insets(6, 6, 0, 6);
		this.add(m_sclVEListe, gbcSclVEListe);
		
		GridBagConstraints gbcBtnBeendenHH = new GridBagConstraints();
		gbcBtnBeendenHH.gridx = 1;
		gbcBtnBeendenHH.gridy = 0;
		gbcBtnBeendenHH.anchor = GridBagConstraints.SOUTHEAST;
		gbcBtnBeendenHH.fill = GridBagConstraints.NONE;
		gbcBtnBeendenHH.insets = new Insets(6, 6, 0, 6);
		this.add(m_btnBeendenHH, gbcBtnBeendenHH);
		/* TODO
		m_tblVEListe.setModel(new VEListeTableModel());
		m_tblVEListe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_tblVEListe.getSelectionModel().setSelectionInterval(0, 0);
		
		m_sclVEListe.setViewportView(m_tblVEListe);
		m_sclVEListe.setRowHeaderView(m_tblVEListe.getRowHeader());
		m_sclVEListe.setCorner(JScrollPane.UPPER_LEFT_CORNER, 
				m_tblVEListe.getUpperLeftCornerComp());
		m_sclVEListe.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		m_sclVEListe.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		m_sclVEListe.setPreferredSize(new Dimension(500, 42));
		
		*/
		m_btnBeendenHH.setText("Beenden HH");
	}

}
