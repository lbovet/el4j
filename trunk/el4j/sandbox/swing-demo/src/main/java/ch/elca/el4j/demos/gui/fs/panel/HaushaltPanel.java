package ch.elca.el4j.demos.gui.fs.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ch.elca.el4j.gui.swing.widgets.IntegerField;


public class HaushaltPanel extends javax.swing.JPanel {

    private String[] years = new String[] {
        "2007", "2006", "2005", "2004", "2003", "2002", "2000" 
        };
	private JComboBox m_cboErhebungsjahr = new JComboBox(years);
    private IntegerField m_txtErhebungsjahr = new IntegerField();
	private IntegerField m_txtHHG = new IntegerField();
	private JTextField m_txtHHNr = new JTextField();
	private JTextField m_txtErhebungsart = new JTextField();
	
	private JLabel m_lblErhebungsjahr = new JLabel();
	private JLabel m_lblHHG = new JLabel();
	private JLabel m_lblHHNr = new JLabel();
	private JLabel m_lblErhebungsart = new JLabel();
	
	private JButton m_btnUbernehme = new JButton();
	private JButton m_btnReset = new JButton();

	public HaushaltPanel() {
		this.setBorder(BorderFactory.createTitledBorder(
			"Haushalt"));
		
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints gbcLblErhebungsjahr = new GridBagConstraints();
		gbcLblErhebungsjahr.gridx = 0;
		gbcLblErhebungsjahr.gridy = 0;
		gbcLblErhebungsjahr.anchor = GridBagConstraints.NORTHWEST;
		gbcLblErhebungsjahr.fill = GridBagConstraints.HORIZONTAL;
		gbcLblErhebungsjahr.weightx = 1.0D;
		gbcLblErhebungsjahr.insets = new Insets(6, 6, 0, 6);
		this.add(m_lblErhebungsjahr, gbcLblErhebungsjahr);
		m_lblErhebungsjahr.setText("Erhebungsjahr");
		
		GridBagConstraints gbcTxtErhebungsjahr = new GridBagConstraints();
		gbcTxtErhebungsjahr.gridx = 1;
		gbcTxtErhebungsjahr.gridy = 0;
		gbcTxtErhebungsjahr.anchor = GridBagConstraints.NORTHWEST;
		gbcTxtErhebungsjahr.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtErhebungsjahr.weightx = 1.0D;
		gbcTxtErhebungsjahr.insets = new Insets(6, 6, 0, 6);
		this.add(m_cboErhebungsjahr, gbcTxtErhebungsjahr);
		m_txtErhebungsjahr.setMaxValue(9999);
		m_txtErhebungsjahr.setMinValue(0);
        m_cboErhebungsjahr.setEditable(true);
        // TODO
        //m_cboErhebungsjahr.setEditor(new JComboxEditor(m_txtErhebungsjahr));
		
		GridBagConstraints gbcLblHHG = new GridBagConstraints();
		gbcLblHHG.gridx = 2;
		gbcLblHHG.gridy = 0;
		gbcLblHHG.anchor = GridBagConstraints.NORTHWEST;
		gbcLblHHG.fill = GridBagConstraints.HORIZONTAL;
		gbcLblHHG.weightx = 1.0D;
		gbcLblHHG.insets = new Insets(6, 6, 0, 6);
		this.add(m_lblHHG, gbcLblHHG);
		m_lblHHG.setText("Haushaltsgruppe");
		
		GridBagConstraints gbcTxtHHG = new GridBagConstraints();
		gbcTxtHHG.gridx = 3;
		gbcTxtHHG.gridy = 0;
		gbcTxtHHG.anchor = GridBagConstraints.NORTHWEST;
		gbcTxtHHG.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtHHG.weightx = 1.0D;
		gbcTxtHHG.insets = new Insets(6, 6, 0, 6);
		this.add(m_txtHHG, gbcTxtHHG);
		m_txtHHG.setMaxValue(99);
		m_txtHHG.setMinValue(0);
		
		GridBagConstraints gbcLblHHNr = new GridBagConstraints();
		gbcLblHHNr.gridx = 4;
		gbcLblHHNr.gridy = 0;
		gbcLblHHNr.anchor = GridBagConstraints.NORTHWEST;
		gbcLblHHNr.fill = GridBagConstraints.HORIZONTAL;
		gbcLblHHNr.weightx = 1.0D;
		gbcLblHHNr.insets = new Insets(6, 6, 0, 6);
		this.add(m_lblHHNr, gbcLblHHNr);
		m_lblHHNr.setText("Haushalt-Nr");
		
		GridBagConstraints gbcTxtHHNr = new GridBagConstraints();
		gbcTxtHHNr.gridx = 5;
		gbcTxtHHNr.gridy = 0;
		gbcTxtHHNr.anchor = GridBagConstraints.NORTHWEST;
		gbcTxtHHNr.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtHHNr.weightx = 1.0D;
		gbcTxtHHNr.insets = new Insets(6, 6, 0, 6);
		this.add(m_txtHHNr, gbcTxtHHNr);
		
		GridBagConstraints gbcLblErhebungsart = new GridBagConstraints();
		gbcLblErhebungsart.gridx = 6;
		gbcLblErhebungsart.gridy = 0;
		gbcLblErhebungsart.anchor = GridBagConstraints.NORTHWEST;
		gbcLblErhebungsart.fill = GridBagConstraints.HORIZONTAL;
		gbcLblErhebungsart.weightx = 1.0D;
		gbcLblErhebungsart.insets = new Insets(6, 6, 0, 6);
		this.add(m_lblErhebungsart, gbcLblErhebungsart);
		m_lblErhebungsart.setText("Erhebungsart");
		
		GridBagConstraints gbcTxtErhebungsart = new GridBagConstraints();
		gbcTxtErhebungsart.gridx = 7;
		gbcTxtErhebungsart.gridy = 0;
		gbcTxtErhebungsart.anchor = GridBagConstraints.NORTHWEST;
		gbcTxtErhebungsart.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtErhebungsart.weightx = 1.0D;
		gbcTxtErhebungsart.insets = new Insets(6, 6, 0, 6);
		this.add(m_txtErhebungsart, gbcTxtErhebungsart);
		
		GridBagConstraints gbcBtnUbernehme = new GridBagConstraints();
		gbcBtnUbernehme.gridx = 8;
		gbcBtnUbernehme.gridy = 0;
		gbcBtnUbernehme.anchor = GridBagConstraints.SOUTHEAST;
		gbcBtnUbernehme.fill = GridBagConstraints.NONE;
		gbcBtnUbernehme.weightx = 1.0D;
		gbcBtnUbernehme.insets = new Insets(6, 6, 0, 6);
		this.add(m_btnUbernehme, gbcBtnUbernehme);
		m_btnUbernehme.setText("Ubernehme");
		
		GridBagConstraints gbcBtnReset = new GridBagConstraints();
		gbcBtnReset.gridx = 8;
		gbcBtnReset.gridy = 1;
		gbcBtnReset.anchor = GridBagConstraints.SOUTHEAST;
		gbcBtnReset.fill = GridBagConstraints.NONE;
		gbcBtnReset.weightx = 1.0D;
		gbcBtnReset.insets = new Insets(6, 6, 0, 6);
		this.add(m_btnReset, gbcBtnReset);
		m_btnReset.setText("Zurück setzen");
		

	}

}
