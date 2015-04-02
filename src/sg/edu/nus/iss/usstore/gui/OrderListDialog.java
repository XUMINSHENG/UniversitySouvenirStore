package sg.edu.nus.iss.usstore.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import sg.edu.nus.iss.usstore.domain.Product;
import sg.edu.nus.iss.usstore.domain.Vendor;
import sg.edu.nus.iss.usstore.util.TableColumnAdjuster;

/*
 * cardName: orderList
 * @ Team FT2 - XIE JIABAO 
 */

public class OrderListDialog extends JDialog{

	private JTable table;
	private DefaultTableModel tableModel;
	private final String[] columnNames = {"Vendor Name","Product Id","Product Name","Order Quantity"}; 
	private StoreApplication manager;
	
	public OrderListDialog(StoreApplication manager){
		super(manager.getStoreWindow(),"Purchase Order List");
		this.manager = manager;
		setLayout(new BorderLayout());
		add("North",createNorthPanel());
		add("Center",createCenterPanel());
		add("South",createSouthPanel());
		initGUI();
	}
	
	private void initGUI(){
		//setPreferredSize(new Dimension(600, 400));
		setSize(600, 400);
		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);
	}
	
	public JPanel createNorthPanel(){
		JPanel p = new JPanel(new FlowLayout());
		p.add(new JLabel("Replenish Order List"));
		return p;
	}
	
	public Container createCenterPanel(){
		tableModel = new DefaultTableModel(loadTableData(manager.getPurchaseOrder()),columnNames){
			@Override
			public boolean isCellEditable(int row,int column){
				return false;
			}
			@Override
			public Class getColumnClass(int column){
				Class returnValue;
				if(column>=0 && column<getColumnCount()){
					returnValue = getValueAt(0, column).getClass();
				}else{
					returnValue = Object.class;
				}
				return returnValue;
			}
		};
		
		table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableColumnAdjuster tca = new TableColumnAdjuster(table);
		tca.setColumnHeaderIncluded(true);
		tca.setColumnDataIncluded(true);
		tca.setOnlyAdjustLarger(true);
		tca.adjustColumns();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				if(table.getSelectionModel().isSelectionEmpty()){
					//fire.setEnabled(false);
				}else{
					//fire.setEnabled(true);
				}
			}

		});
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		JScrollPane p = new JScrollPane(table);
		return p;
	}
	
	private JPanel createSouthPanel(){
		JPanel p = new JPanel(new FlowLayout());
		JButton b = new JButton("Print");
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		p.add(b);
		
		return p;
	}
	
	private Object[][] loadTableData(HashMap<Product,Vendor> order){
		int length = order.size();
		Object[][] data;
		if(length==0){
			data = new Object[0][5];
		}else{
			data =  new Object[length][5];
			Iterator i =  order.entrySet().iterator();
			int j = 0;
			Product p;
			Vendor v;
			while(i.hasNext()){
				Entry entry = (Entry) i.next();
				p = (Product) entry.getKey();
				v = (Vendor) entry.getValue();
				if(v==null){
					data[j][0] = "No Vendor";
				}else{
					data[j][0] = v.getName();
				}
				data[j][1] = p.getProductId();
				data[j][2] = p.getName();
				data[j][3] = p.getOrderQuantity();
				j++;
			}
//			for(int i=0;i<length;i++){
//				data[i][0] = order.get(i).getProductId();
//				data[i][1] = order.get(i).getName();
//				data[i][2] = order.get(i).getQuantityAvailable();
//				data[i][3] = order.get(i).getReorderQuantity();
//				data[i][4] = order.get(i).getOrderQuantity();
//			}
		}
		return data;
	}
}
