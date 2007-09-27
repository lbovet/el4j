package ch.elca.el4j.demos.gui.fs.panel;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.swingx.JXTable;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.demos.gui.fs.object.ExampleKonto;
import ch.elca.el4j.demos.gui.fs.object.KontoDTO;
import ch.elca.el4j.gui.swing.GUIApplication;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.ReadOnly;
import com.silvermindsoftware.hitch.annotations.BoundComponent;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;
import com.silvermindsoftware.hitch.binding.components.TableBinding;

@Form(autoBind = true)
public class KontoListPanel extends JPanel {
    
    @ModelObject (isDefault = true)
	private ExampleKonto dao;
    
    @BoundComponent(property = "konti", readOnly = ReadOnly.TRUE)
	private JXTable m_tblKontoListe = new JXTable();
    
	private JScrollPane m_sclKontoListe = new JScrollPane();
	private JButton m_btnBeendenVE = new JButton();
	private JButton m_btnAufteilung = new JButton();
	private JButton m_btnLoschen = new JButton();
	private JButton m_btnNew = new JButton();
	
	// setup a final binder instance variable
    private final Binder binder = BinderManager.getBinder(this);

	public KontoListPanel(GUIApplication app) {
		this.setBorder(BorderFactory.createTitledBorder(
			"Kontoliste"));
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints gbcSclKontoListe = new GridBagConstraints();
		gbcSclKontoListe.gridx = 0;
		gbcSclKontoListe.gridy = 0; 
		gbcSclKontoListe.gridheight = 4;
		gbcSclKontoListe.anchor = GridBagConstraints.NORTHWEST;
		gbcSclKontoListe.fill = GridBagConstraints.BOTH;
		gbcSclKontoListe.weightx = 1.0D;
		gbcSclKontoListe.weighty = 1.0D;
		gbcSclKontoListe.insets = new Insets(6, 6, 0, 6);
		this.add(m_sclKontoListe, gbcSclKontoListe);
		
		GridBagConstraints gbcBtnAufteilung = new GridBagConstraints();
		gbcBtnAufteilung.gridx = 1;
		gbcBtnAufteilung.gridy = 0;
		gbcBtnAufteilung.anchor = GridBagConstraints.SOUTHEAST;
		gbcBtnAufteilung.fill = GridBagConstraints.NONE;
		gbcBtnAufteilung.insets = new Insets(6, 6, 0, 6);
		this.add(m_btnAufteilung, gbcBtnAufteilung);
		
		GridBagConstraints gbcBtnNew = new GridBagConstraints();
		gbcBtnNew.gridx = 1;
		gbcBtnNew.gridy = 1;
		gbcBtnNew.anchor = GridBagConstraints.SOUTHEAST;
		gbcBtnNew.fill = GridBagConstraints.NONE;
		gbcBtnNew.insets = new Insets(6, 6, 0, 6);
		this.add(m_btnNew, gbcBtnNew);
		
		GridBagConstraints gbcBtnLoschen = new GridBagConstraints();
		gbcBtnLoschen.gridx = 1;
		gbcBtnLoschen.gridy = 2;
		gbcBtnLoschen.anchor = GridBagConstraints.SOUTHEAST;
		gbcBtnLoschen.fill = GridBagConstraints.NONE;
		gbcBtnLoschen.insets = new Insets(6, 6, 0, 6);
		this.add(m_btnLoschen, gbcBtnLoschen);
		
		GridBagConstraints gbcBtnBeendenVE = new GridBagConstraints();
		gbcBtnBeendenVE.gridx = 1;
		gbcBtnBeendenVE.gridy = 3;
		gbcBtnBeendenVE.anchor = GridBagConstraints.SOUTHEAST;
		gbcBtnBeendenVE.fill = GridBagConstraints.NONE;
		gbcBtnBeendenVE.insets = new Insets(6, 6, 0, 6);
		this.add(m_btnBeendenVE, gbcBtnBeendenVE);
		
		ApplicationContext ctx = app.getSpringContext();
        dao = (ExampleKonto) ctx.getBean("konto");
		
		// TODO final KontolisteTableModel kontoModel = new KontolisteTableModel();
//		ArrayList<ElcaTableColumn> tableFormat = new ArrayList<ElcaTableColumn>();
//		ElcaTableColumn kontoNrCol = new JTableColumn();
//		kontoNrCol.setAttributeName("kontoNr");
//		kontoNrCol.setLabel("Konto-Nr");
//		kontoNrCol.setPosition(0);
//		kontoNrCol.setSortState(ElcaTableColumn.PRIMARY_SORT_ASC);
//		kontoNrCol.setVisibility(ElcaTableColumn.PERMANENT);
//		kontoNrCol.setWidth(20);
//		tableFormat.add(kontoNrCol);
//		
//		ElcaTableColumn zusNrCol = new JTableColumn();
//		zusNrCol.setAttributeName("zusNr");
//		zusNrCol.setLabel("Zus-Nr");
//		zusNrCol.setPosition(1);
//		zusNrCol.setSortState(ElcaTableColumn.PRIMARY_SORT_ASC);
//		zusNrCol.setVisibility(ElcaTableColumn.PERMANENT);
//		zusNrCol.setWidth(20);
//		tableFormat.add(zusNrCol);
//		
//		ElcaTableColumn seqNrCol = new JTableColumn();
//		seqNrCol.setAttributeName("seqNr");
//		seqNrCol.setLabel("Seq-Nr");
//		seqNrCol.setPosition(2);
//		seqNrCol.setSortState(ElcaTableColumn.PRIMARY_SORT_ASC);
//		seqNrCol.setVisibility(ElcaTableColumn.PERMANENT);
//		seqNrCol.setWidth(20);
//		tableFormat.add(seqNrCol);
//		
//		ElcaTableColumn betragCol = new JTableColumn();
//		betragCol.setAttributeName("betrag");
//		betragCol.setLabel("Betrag");
//		betragCol.setPosition(3);
//		betragCol.setSortState(ElcaTableColumn.PRIMARY_SORT_ASC);
//		betragCol.setVisibility(ElcaTableColumn.PERMANENT);
//		betragCol.setWidth(100);
//		betragCol.setEditor(new JTableCellEditor(new JDoubleField()));
//		tableFormat.add(betragCol);
//		
//		ElcaTableColumn funktionCol = new JTableColumn();
//		funktionCol.setAttributeName("funktion");
//		funktionCol.setLabel("Funktion");
//		funktionCol.setPosition(4);
//		funktionCol.setSortState(ElcaTableColumn.PRIMARY_SORT_ASC);
//		funktionCol.setVisibility(ElcaTableColumn.PERMANENT);
//		funktionCol.setWidth(100);
//		betragCol.setEditor(new JTableCellEditor(new JTextField()));
//		tableFormat.add(funktionCol);
//		
//		ElcaTableColumn sachgruppeCol = new JTableColumn();
//		sachgruppeCol.setAttributeName("sachgruppe");
//		sachgruppeCol.setLabel("Sachgruppe");
//		sachgruppeCol.setPosition(5);
//		sachgruppeCol.setSortState(ElcaTableColumn.PRIMARY_SORT_ASC);
//		sachgruppeCol.setVisibility(ElcaTableColumn.PERMANENT);
//		sachgruppeCol.setWidth(100);
//		betragCol.setEditor(new JTableCellEditor(new JTextField()));
//		tableFormat.add(sachgruppeCol);
//		
//		final DefaultElcaTableModel kontoModel = new DefaultElcaTableModel(tableFormat);
//		KontoDTO konto = new KontoDTO();
//		konto.setKontoNr("301");
//		konto.setZusNr(new Integer(1));
//		konto.setSeqNr(new Integer(0));
//		konto.setBetrag(new Double(100.00));
//		konto.setFunktion("");
//		konto.setSachgruppe("");
//		ArrayList<KontoDTO> data =  new ArrayList<KontoDTO>();
//		data.add(konto);
////		kontoModel.setObjects(data);
		
// TODO		m_tblKontoListe.setModel(kontoModel);
		m_tblKontoListe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_tblKontoListe.getSelectionModel().setSelectionInterval(0, 0);
//		m_tblKontoListe.addKeyListener(new KeyAdapter(){
//			public void keyReleased(KeyEvent e) {
//				if(e.getKeyCode() == KeyEvent.VK_TAB) {
//					int selectedRow = m_tblKontoListe.getSelectedRow();
//					int selectedColumn = m_tblKontoListe.getSelectedColumn();
//					if(kontoModel.isFirstCell(selectedRow, selectedColumn)) {
//
//						ArrayList<Object> row5 = new ArrayList<Object>();
//						row5.add("");
//						row5.add(null);
//						row5.add(null);
//						row5.add(null);
//						row5.add("");
//						row5.add("");
//						kontoModel.getDataList().add(row5);
//
//						kontoModel.fireTableChanged(new TableModelEvent(kontoModel));
//						m_tblKontoListe.getSelectionModel().setSelectionInterval(
//								kontoModel.getDataList().size() - 1, 
//								kontoModel.getDataList().size() - 1);
//					}
//					
////					kontoModel.insertObject(new KontoDTO(), selectedRow + 1);
//				}
//			}
//			
//		});
/* TODO		m_tblKontoListe.getColumnModel().getColumn(0).setCellEditor(new ElcaTableCellEditor(new ElcaTextField()));
		m_tblKontoListe.getColumnModel().getColumn(1).setCellEditor(new ElcaTableCellEditor(new ElcaIntegerField()));
		m_tblKontoListe.getColumnModel().getColumn(1).setCellRenderer(new IntegerTableCellRenderer());
		m_tblKontoListe.getColumnModel().getColumn(2).setCellEditor(new ElcaTableCellEditor(new ElcaIntegerField()));
		m_tblKontoListe.getColumnModel().getColumn(2).setCellRenderer(new IntegerTableCellRenderer());
		m_tblKontoListe.getColumnModel().getColumn(3).setCellEditor(new ElcaTableCellEditor(new ElcaDoubleField()));
		m_tblKontoListe.getColumnModel().getColumn(3).setCellRenderer(new DoubleTableCellRenderer());
		m_tblKontoListe.getColumnModel().getColumn(4).setCellEditor(new ElcaTableCellEditor(new ElcaTextField()));
		m_tblKontoListe.getColumnModel().getColumn(5).setCellEditor(new ElcaTableCellEditor(new ElcaTextField()));
		*/
		m_sclKontoListe.setViewportView(m_tblKontoListe);
//		m_sclKontoListe.setRowHeaderView(m_tblKontoListe.getRowHeader());
//		m_sclKontoListe.setCorner(JScrollPane.UPPER_LEFT_CORNER, 
//				m_tblKontoListe.getUpperLeftCornerComp());
		m_sclKontoListe.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		m_sclKontoListe.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		m_sclKontoListe.setPreferredSize(new Dimension(500, 120));

		
		m_btnAufteilung.setText("Aufteilung");
		m_btnLoschen.setText("Löschen");
		m_btnLoschen.setMnemonic(KeyEvent.VK_L);
		m_btnLoschen.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int selectedRow = m_tblKontoListe.getSelectedRow();
				dao.getKonti().remove(selectedRow);
//				kontoModel.getDataList().remove(selectedRow);

//				kontoModel.fireTableChanged(new TableModelEvent(kontoModel));
//				if(kontoModel.getDataList().size() != 0) {
					if (selectedRow != 0) {
						selectedRow--;
					}
					m_tblKontoListe.getSelectionModel().setSelectionInterval(
							selectedRow, selectedRow);
					m_tblKontoListe.requestFocus();
//				}
			}
				
		});
		m_btnBeendenVE.setText("Beenden VE");
		m_btnNew.setText("New");
		m_btnNew.setMnemonic(KeyEvent.VK_N);
		m_btnNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int selectedRow = m_tblKontoListe.getSelectedRow();
				
				dao.getKonti().add(new KontoDTO("305", 1, null, 100.0, "213", "302"));

				ArrayList<Object> row5 = new ArrayList<Object>();
				row5.add("");
				row5.add(null);
				row5.add(null);
				row5.add(null);
				row5.add("");
				row5.add("");
//				kontoModel.getDataList().add(selectedRow + 1, row5);

//				kontoModel.fireTableChanged(new TableModelEvent(kontoModel));
				m_tblKontoListe.getSelectionModel().setSelectionInterval(
						selectedRow + 1, 
						selectedRow + 1);
				m_tblKontoListe.requestFocus();
			}
				
		});
		
		m_tblKontoListe.setColumnControlVisible(true);
		
        String[] propertyNames = new String[] {"kontoNr", "zusNr", "seqNr", "betrag", "funktion", "sachgruppe"};
        String[] columnLabels = new String[] {"Konto-Nr", "Zus-Nr", "Seq-Nr", "Betrag", "Funktion", "Sachgruppe"};
        binder.registerBinding(m_tblKontoListe, new TableBinding(propertyNames, columnLabels));
        
        BindingGroup group = binder.getAutoBinding(this);
        group.bind();
	}
}
