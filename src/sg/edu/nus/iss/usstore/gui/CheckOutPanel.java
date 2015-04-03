//CheckOutPanel.java
package sg.edu.nus.iss.usstore.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import sg.edu.nus.iss.usstore.domain.Customer;
import sg.edu.nus.iss.usstore.domain.Discount;
import sg.edu.nus.iss.usstore.domain.Member;
import sg.edu.nus.iss.usstore.domain.Product;
import sg.edu.nus.iss.usstore.domain.Transaction;
import sg.edu.nus.iss.usstore.domain.TransactionItem;
import sg.edu.nus.iss.usstore.util.DigitDocument;

public class CheckOutPanel extends JPanel
{
	/**
	 * The CheckOutPanel
	 * 
	 * @author Liu Xinzhuo
	 * @author A0136010A
	 * @version 0.8
	 */
	private static final long serialVersionUID = 1L;

	private JLabel JlgetMemberName;
	private JLabel JlTotalPriceNum;
	private JLabel JlDiscountNum;
	private JLabel JlDiscountedPriceNum;
	private JLabel JlLoyalPointNum;
	private JLabel JlRestNum;
	private JLabel JlChangeNum;
	private JTextField JtBarCodeID;
	private JTextField JtQuantity;
	private JTextField JtMemberID;
	private JTextField JtPaidNum;
	private JTextField JtCashNum;
	private JTable table;
	private JButton JbFinish;
	private JButton JbBack;
	private TableColumn column;

	private DecimalFormat df = new DecimalFormat("0.00");
	private DefaultTableModel defaultModel = null;

	private Customer customer = null;
	private Product product = null;
	private Discount discount;
	private int scrollpanelwidth = 600;
	private int scrollpanelheight = 270;
	private int flag = 0;
	private String tempBarCode;
	private Vector vector = new Vector<Object>();
	private Listener listener = new Listener();
	private StoreApplication sa = null;
	private Transaction transaction;

	private final String ERR_MSG_MEMBER_NOT_EXIST = "Error MemberID!";
	private final String ERR_MSG_PRODCUT_NOT_EXIST = "No product!";
	private final String ERR_MSG_BARCODE_ERROR = "Bar Code Error!";
	private final String ERR_MSG_QUANTITY_FORMAT_ERROR = "Quantity Format Error";
	private final String ERR_MSG_QUANTITY_NOT_ENOUGH = "Quantity is not Enough!";
	private final String ERR_MSG_POINT_FORMAT_ERROR = "Point Format Error!";
	private final String ERR_MSG_POINT_NOT_ENOUGH = "Point is not Enough!";
	private final String ERR_MSG_CASH_FORMAT_ERROR = "Cash Format Error!";
	private final String ERR_MSG_CASH_NOT_ENOUGH = "Cash is not enough!";
	private final String ERR_MSG_SELECT_ROW = "Select a Row!";

	public static JLabel JlError;

	public void setOutputValue()
	{
		JlTotalPriceNum.setText(df.format(transaction.calcTotalPrice()));
		JlDiscountNum.setText(Double.toString(transaction.getDiscount()
				.getPercent()));
		JlDiscountedPriceNum
				.setText(df.format(transaction.calcDiscountPrice()));
		JlRestNum.setText(df.format(transaction.calcRest()));
		if (transaction.getCustomer() instanceof Member)
		{
			Member member = (Member) transaction.getCustomer();
			JlgetMemberName.setText(member.name);
			JlLoyalPointNum.setText(Integer.toString(member.getLoyaltyPoint()));
			JtPaidNum.setEnabled(true);
		} else
		{
			JlgetMemberName.setText("PUBLIC");
			JtPaidNum.setEnabled(false);
		}
		JbFinishControl();
	}

	public void JbFinishControl()
	{
		if (transaction.calcChange() < 0)
		{
			JbFinish.setEnabled(false);
		} else if (JlError.getText() == ERR_MSG_CASH_FORMAT_ERROR
				|| JlError.getText() == ERR_MSG_CASH_NOT_ENOUGH
				|| JlError.getText() == ERR_MSG_POINT_FORMAT_ERROR
				|| JlError.getText() == ERR_MSG_POINT_NOT_ENOUGH)
		{
			JbFinish.setEnabled(false);
		} else if (transaction.getItemList().size() == 0)
		{
			JbFinish.setEnabled(false);
		} else
		{
			JbFinish.setEnabled(true);
		}

	}

	public void tableDataBinding()
	{
		flag = 1;
		ArrayList itemList = transaction.getItemList();
		Vector dataVector = defaultModel.getDataVector();
		dataVector.clear();

		for (int i = 0; i < itemList.size(); i++)
		{
			Vector subVector = new Vector();
			subVector.add(i + 1);
			TransactionItem transactionitem = (TransactionItem) itemList.get(i);
			product = transactionitem.getProduct();
			subVector.add(product.getBarCodeNumber());
			subVector.add(product.getName());
			subVector.add(Integer.toString(transactionitem.getQty()));
			subVector.add(product.getPrice());
			subVector.add(transactionitem.calculateAmount());
			defaultModel.addRow(subVector);
		}
		table.validate();
		table.repaint();
		flag = 0;
		setOutputValue();
	}

	public void addProduct(ArrayList<TransactionItem> arrayList, int qty)
	{
		int productAddFlag = -1;
		for (int m = 0; m < arrayList.size(); m++)
		{
			if (arrayList.get(m).getProduct() == product)
			{
				productAddFlag = m;
			}
		}
		if (productAddFlag == -1)
		{
			arrayList
					.add(new TransactionItem(product, product.getPrice(), qty));
		} else
		{
			TransactionItem tempTransactionItem = arrayList.get(productAddFlag);
			tempTransactionItem.setQty(tempTransactionItem.getQty() + qty);
		}
	}

	public Transaction getTransaction()
	{
		return transaction;
	}

	public void setTransaction(Transaction transaction)
	{
		this.transaction = transaction;
	}

	public void cancelAll()
	{
		// refresh data
		{
			transaction = new Transaction();
			sa.setBillCustomer(transaction, null);
			vector = defaultModel.getDataVector();
			vector.clear();
			table.validate();
			table.repaint();
		}
		// refresh para
		{
			flag = 0;
		}
		// refresh UI
		{
			JlgetMemberName.setText(null);
			JlTotalPriceNum.setText(Double.toString(transaction
					.calcTotalPrice()));
			JlDiscountNum.setText(Double.toString(transaction.getDiscount()
					.getPercent()));
			JlDiscountedPriceNum.setText(Double.toString(transaction
					.calcDiscountPrice()));
			JlLoyalPointNum.setText("0");
			JlRestNum.setText(Double.toString(transaction.calcRest()));
			JlChangeNum.setText(Double.toString(transaction.calcChange()));
			JtBarCodeID.setText(null);
			JtQuantity.setText(null);
			JtMemberID.setText(null);
			JtPaidNum.setText(null);
			JtPaidNum.setEnabled(false);
			JtCashNum.setText(null);
			JlError.setText(null);
		}
	}

	public CheckOutPanel(StoreApplication sa)
	{ // ʵ�ֹ��췽��
		this.sa = sa;
		// OPeration
		JPanel jpOperation = new JPanel();
		this.add(jpOperation, BorderLayout.NORTH);
		// Title
		JLabel jlTitle = new JLabel("Check Out!");
		jlTitle.setFont(new Font("Bauhaus 93", Font.PLAIN, 30));
		jlTitle.setHorizontalAlignment(SwingConstants.CENTER);
		jpOperation.setLayout(new GridLayout(3, 1));
		jpOperation.add(jlTitle);
		JPanel jpInput = new JPanel();
		JPanel jpOutput = new JPanel();
		jpOperation.add(jpInput);
		jpOperation.add(jpOutput);

		// Input
		jpOperation.add(jpInput);
		jpInput.setLayout(new GridLayout(2, 2));
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JPanel jp3 = new JPanel();
		JPanel jp4 = new JPanel();

		// jp1
		JLabel JlMemberID = new JLabel("MEMBER ID");
		JtMemberID = new JTextField(24);
		jp1.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp1.add(JlMemberID);
		jp1.add(JtMemberID);
		jpInput.add(jp1);

		// jp2
		JLabel JlMemberName = new JLabel("MEMBER  ");
		JlgetMemberName = new JLabel("PUBLIC");
		JButton JbMemberSubmit = new JButton("Submit");
		JbMemberSubmit.setActionCommand("JbMemberSubmit");
		JbMemberSubmit.addActionListener(listener);
		jp2.setLayout(new GridLayout(1, 2));
		JPanel jp2_1 = new JPanel();
		JPanel jp2_2 = new JPanel();
		jp2_1.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp2_2.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp2_1.add(JlMemberName);
		jp2_1.add(JlgetMemberName);
		jp2_2.add(JbMemberSubmit);
		jp2.add(jp2_1);
		jp2.add(jp2_2);
		jpInput.add(jp2);

		// jp3
		JLabel JlBarCodeID = new JLabel("Bar   Code  ");
		JtBarCodeID = new JTextField(24);
		jp3.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp3.add(JlBarCodeID);
		jp3.add(JtBarCodeID);
		jpInput.add(jp3);

		// jp4
		jp4.setLayout(new GridLayout(1, 2));
		JPanel jp4_1 = new JPanel();
		JPanel jp4_2 = new JPanel();
		jp4_1.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp4_2.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel JlQuantity = new JLabel("QUANTITY");
		JtQuantity = new JTextField(6);
		JtQuantity.setDocument(new DigitDocument());
		JButton JbProductSubmit = new JButton("Submit");
		tempBarCode = JtBarCodeID.getText();
		product = sa.getProductByBarCode(tempBarCode);
		JbProductSubmit.setActionCommand("JbProductSubmit");
		JbProductSubmit.addActionListener(listener);
		jp4_1.add(JlQuantity);
		jp4_1.add(JtQuantity);
		jp4_2.add(JbProductSubmit);
		jp4.add(jp4_1);
		jp4.add(jp4_2);
		jpInput.add(jp4);

		// output
		jpOperation.add(jpOutput);
		jpOutput.setLayout(new GridLayout(2, 4));
		JPanel jp5 = new JPanel();
		JPanel jp6 = new JPanel();
		JPanel jp7 = new JPanel();
		JPanel jp8 = new JPanel();
		JPanel jp9 = new JPanel();
		JPanel jp10 = new JPanel();
		JPanel jp11 = new JPanel();
		JPanel jp12 = new JPanel();

		// jp5
		jp5.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel JlTotalPrice = new JLabel("Total Price:");
		JlTotalPriceNum = new JLabel("00.00");
		jp5.add(JlTotalPrice);
		jp5.add(JlTotalPriceNum);
		jpOutput.add(jp5);

		// jp6
		jp6.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel JlDiscount = new JLabel("Discount:");
		JlDiscountNum = new JLabel("00.00");
		jp6.add(JlDiscount);
		jp6.add(JlDiscountNum);
		jpOutput.add(jp6);

		// jp7
		jp7.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel JlDiscountedPrice = new JLabel("DiscountedPrice:");
		JlDiscountedPriceNum = new JLabel("00.00");
		jp7.add(JlDiscountedPrice);
		jp7.add(JlDiscountedPriceNum);
		jpOutput.add(jp7);

		// jp8
		jp8.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel JlLoyalPoint = new JLabel("LOYAL POINT");
		JlLoyalPointNum = new JLabel("0");
		jp8.add(JlLoyalPoint);
		jp8.add(JlLoyalPointNum);
		jpOutput.add(jp8);

		// jp9
		jp9.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel JlPaid = new JLabel("Redeemed Point");
		JtPaidNum = new JTextField(6);
		JtPaidNum.setEnabled(false);
		JtPaidNum.setDocument(new DigitDocument());
		JtPaidNum.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				// ������д��Ӧ�Ĵ������
				String tempLoyalPaid = JtPaidNum.getText();
				int tempLoyalPaidNum = Integer.valueOf(tempLoyalPaid)
						.intValue();
				if (tempLoyalPaidNum < 0)
				{
					JlError.setText(ERR_MSG_POINT_FORMAT_ERROR);
					JlChangeNum.setText("**.**");
				} else
				{
					Member member = (Member) transaction.getCustomer();
					if (member.getLoyaltyPoint() >= tempLoyalPaidNum)
					{
						if (JlError.getText() == ERR_MSG_POINT_NOT_ENOUGH)
							JlError.setText(null);
						transaction.setRedeemedLoyaltyPoint(tempLoyalPaidNum);
						JlRestNum.setText(df.format(transaction.calcRest()));
					} else
					{
						JlError.setText(ERR_MSG_POINT_NOT_ENOUGH);
					}
				}
				setOutputValue();
			}

			public void removeUpdate(DocumentEvent e)
			{

				if (JtPaidNum.getText().length() != 0)
				{
					String tempLoyalPaid = JtPaidNum.getText();

					int tempLoyalPaidNum = Integer.valueOf(tempLoyalPaid)
							.intValue();
					if (tempLoyalPaidNum < 0)
					{
						JlError.setText(ERR_MSG_POINT_FORMAT_ERROR);
						JlChangeNum.setText("**.**");
					} else
					{
						Member member = (Member) transaction.getCustomer();
						if (member.getLoyaltyPoint() >= tempLoyalPaidNum)
						{
							if (JlError.getText() == ERR_MSG_POINT_NOT_ENOUGH)
								JlError.setText(null);
							transaction
									.setRedeemedLoyaltyPoint(tempLoyalPaidNum);
							JlRestNum.setText(df.format(transaction.calcRest()));
						} else
						{
							JlError.setText(ERR_MSG_POINT_NOT_ENOUGH);
						}
					}
				} else
				{
					transaction.setRedeemedLoyaltyPoint(0);
				}
				setOutputValue();
			}

			public void changedUpdate(DocumentEvent e)
			{
				// TODO Auto-generated method stub

			}
		});
		jp9.add(JlPaid);
		jp9.add(JtPaidNum);
		jpOutput.add(jp9);

		// jp10
		jp10.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel JlRest = new JLabel("REST:");
		JlRestNum = new JLabel("00.00");
		jp10.add(JlRest);
		jp10.add(JlRestNum);
		jpOutput.add(jp10);

		// jp11
		jp11.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel JlCash = new JLabel("Cash:");
		JtCashNum = new JTextField(6);
		JtCashNum.setDocument(new DigitDocument());
		JtCashNum.getDocument().addDocumentListener(new DocumentListener()
		{

			public void insertUpdate(DocumentEvent e)
			{
				String ScashNum = JtCashNum.getText();
				double DcashNum = Double.valueOf(ScashNum).doubleValue();
				transaction.setCashAmount(DcashNum);
				if (DcashNum > 0)
				{
					double tempChange = transaction.calcChange();
					if (tempChange > 0)
					{
						JlChangeNum.setText(df.format(tempChange));
						if (JlError.getText() == ERR_MSG_CASH_NOT_ENOUGH
								|| JlError.getText() == ERR_MSG_CASH_FORMAT_ERROR)
						{
							JlError.setText(null);
						}
					} else
					{
						JlError.setText(ERR_MSG_CASH_NOT_ENOUGH);
						JlChangeNum.setText("**.**");
					}
				} else
				{
					JlError.setText(ERR_MSG_CASH_FORMAT_ERROR);
					JlChangeNum.setText("**.**");
				}
				setOutputValue();
			}

			public void removeUpdate(DocumentEvent e)
			{
				if (JtCashNum.getText().length() != 0)
				{
					String ScashNum = JtCashNum.getText();
					double DcashNum = Double.valueOf(ScashNum).doubleValue();
					transaction.setCashAmount(DcashNum);
					if (DcashNum > 0)
					{
						double tempChange = transaction.calcChange();
						if (tempChange > 0)
						{
							JlChangeNum.setText(df.format(tempChange));
							if (JlError.getText() == ERR_MSG_CASH_NOT_ENOUGH
									|| JlError.getText() == ERR_MSG_CASH_FORMAT_ERROR)
							{
								JlError.setText(null);
								JbFinish.setEnabled(true);
							}
						} else
						{
							JlError.setText(ERR_MSG_CASH_NOT_ENOUGH);
							JlChangeNum.setText("**.**");
						}
					} else
					{
						JlError.setText(ERR_MSG_CASH_FORMAT_ERROR);
						JlChangeNum.setText("**.**");
					}
				} else
				{
					transaction.setCashAmount(0);
				}
				setOutputValue();
			}

			public void changedUpdate(DocumentEvent e)
			{
			}
		});
		jp11.add(JlCash);
		jp11.add(JtCashNum);
		jpOutput.add(jp11);

		// jp12
		jp12.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel JlChange = new JLabel("CHANGE:");
		JlChangeNum = new JLabel("00.00");
		jp12.add(JlChange);
		jp12.add(JlChangeNum);
		jpOutput.add(jp12);

		// Table
		String[] tableTitle = { "Num", "Bar Code", "Product", "Quantity(Editable)",
				"Price", "Total" };
		defaultModel = new DefaultTableModel(null, tableTitle)
		{
			public boolean isCellEditable(int row, int column)
			{
				if (column == 3)
					return true;
				else
					return false;
			}
		};
		table = new JTable(defaultModel);
		for (int i = 0; i < table.getColumnCount(); i++)
		{
			column = table.getColumnModel().getColumn(i);
			if (i == 0)
			{
				column.setPreferredWidth(scrollpanelwidth/16);
			}
			if (i == 1 || i == 2 )
			{
				column.setPreferredWidth(scrollpanelwidth/4);
			}
			if (i==3)
			{
				column.setPreferredWidth(scrollpanelwidth*3/16);
			} 
			if ( i==4 || i==5 )
			{
				column.setPreferredWidth(scrollpanelheight/8);
			}
		}
		defaultModel.addTableModelListener(new TableModelListener()
		{

			@Override
			public void tableChanged(TableModelEvent e)
			{
				int row = e.getFirstRow();
				if (flag == 0)
				{
					int num = Integer.valueOf(
							(String) defaultModel.getValueAt(row, 3))
							.intValue();
					transaction.getItemList().get(row).setQty(num);
					tableDataBinding();
				}
				setOutputValue();
			}
		});

		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column)
			{

				if (row % 2 == 0)
					setBackground(Color.WHITE);
				else if (row % 2 == 1)
					setBackground(new Color(206, 231, 255));
				if (column==3)
					setBackground(new Color(160, 255, 160));
				return super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);

			}	
		};

		for (int i = 0; i <= 5; i++)
		{
			table.getColumn(tableTitle[i]).setCellRenderer(tcr);
		}

		table.setPreferredScrollableViewportSize(new Dimension(
				scrollpanelwidth, scrollpanelheight));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setBackground(Color.white);
		scrollPane.setViewportView(table);

		this.add(scrollPane, BorderLayout.CENTER);

		JPanel jpButton = new JPanel();
		jpButton.setLayout(new GridLayout(7, 1));
		JButton JbDelete = new JButton("Delete");
		JLabel JlBlank1 = new JLabel(" ");
		JbDelete.setActionCommand("JbDelete");
		JbDelete.addActionListener(listener);
		JButton JbCancel = new JButton("Cancel");
		JbCancel.setActionCommand("JbCancel");
		JbCancel.addActionListener(listener);
		JLabel JlBlank2 = new JLabel(" ");
		JlError = new JLabel();
		JbFinish = new JButton("Finish");
		JbFinish.setActionCommand("JbFinish");
		JbFinish.addActionListener(listener);
		JbFinish.setEnabled(false);
		JbBack = new JButton("Back");
		JbBack.setActionCommand("JbBack");
		JbBack.addActionListener(listener);
		JlError.setText("");
		JlError.setForeground(Color.RED);
		jpButton.add(JlError);
		jpButton.add(JlBlank1);
		jpButton.add(JbBack);
		jpButton.add(JlBlank2);
		jpButton.add(JbDelete);
		jpButton.add(JbCancel);
		jpButton.add(JbFinish);
		this.add(jpButton, BorderLayout.EAST);
	}

	class Listener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getActionCommand().equals("JbMemberSubmit"))
			{
				String MemberID = JtMemberID.getText();
				if (JtMemberID.getText().length() == 0)
				{
					JlError.setText(ERR_MSG_MEMBER_NOT_EXIST);
				} else
				{
					transaction = sa.setBillCustomer(transaction, MemberID);
					if (transaction.getCustomer() == null)
					{
						transaction = sa.setBillCustomer(transaction, null);
						JlError.setText(ERR_MSG_MEMBER_NOT_EXIST);
					} else
					{
						if (JlError.getText() == ERR_MSG_MEMBER_NOT_EXIST)
							JlError.setText(null);
					}
					setOutputValue();
				}
			}
			if (e.getActionCommand().equals("JbProductSubmit"))
			{
				flag = 1;
				tempBarCode = JtBarCodeID.getText();
				product = sa.getProductByBarCode(tempBarCode);
				String tempqty = JtQuantity.getText();
				int intqty = Integer.parseInt(tempqty);
				if (tempBarCode.length() == 0)
				{
					JlError.setText(ERR_MSG_BARCODE_ERROR);
				} else if (tempqty.length() == 0)
				{
					JlError.setText(ERR_MSG_QUANTITY_FORMAT_ERROR);
				} else if (Integer.valueOf(JtQuantity.getText()).intValue() < 1)
				{
					JlError.setText(ERR_MSG_QUANTITY_FORMAT_ERROR);
				} else if(product.getQuantityAvailable()<intqty)
				{
					JlError.setText(ERR_MSG_QUANTITY_NOT_ENOUGH);
				}
				else
				{

					if (JlError.getText() == ERR_MSG_PRODCUT_NOT_EXIST
							|| JlError.getText() == ERR_MSG_BARCODE_ERROR
							|| JlError.getText() == ERR_MSG_QUANTITY_NOT_ENOUGH
							|| JlError.getText() == ERR_MSG_QUANTITY_FORMAT_ERROR)
					{
						JlError.setText(null);
					}
					if (product == null)
					{
						JlError.setText(ERR_MSG_PRODCUT_NOT_EXIST);
						return;
					}
					ArrayList<TransactionItem> tempTransactionList = transaction
							.getItemList();
					addProduct(tempTransactionList, intqty);
					tableDataBinding();
					JtBarCodeID.setText(null);
					JtQuantity.setText(null);
				}
				flag = 0;
				setOutputValue();
			}
			if (e.getActionCommand().equals("JbDelete"))
			{
				if (table.getSelectedRow() == -1)
				{
					JlError.setText("Select a row");
				} else
				{
					if (JlError.getText() == ERR_MSG_SELECT_ROW)
					{
						JlError.setText(null);
					}
					int rowcount = defaultModel.getRowCount();
					if (rowcount > 0)
					{
						transaction.getItemList()
								.remove(table.getSelectedRow());
						tableDataBinding();
					}
					table.revalidate();
					setOutputValue();
				}
			}
			if (e.getActionCommand().equals("JbCancel"))
			{
				cancelAll();
			}
			if (e.getActionCommand().equals("JbFinish"))
			{
				JFrame confirm = new CheckOutConfirmFrame();
				confirm.setVisible(true);
				sa.confirmPayment(transaction);
			/**
			 * 	writes on the cansole the transaction in the below given format
			 * after finishing of the transaction
			 */
				System.out.println("##############################################################################");
				System.out.print(String.format("%1$-" + 30 + "s", "Tran ID : "+transaction.getId()));
				System.out.println(String.format("%0$"+10+"s", "Date : "+transaction.getDate()));
				System.out.print(String.format("%1$-" + 30 + "s", "Member ID : "+transaction.getCustomer().getID()));
				System.out.println(String.format("%0$"+10+"s", "Loyalty Points : "+transaction.getRedeemedLoyaltyPoint()));
				System.out.println("==========================================================================");
				System.out.print(String.format("%1$-" + 50 + "s", "Product"));
				System.out.print(String.format("%0$"+10+"s", String.format("%1$-" + 5 + "s", "Quantity")));
				System.out.println(String.format("%0$"+10+"s", String.format("%1$-" + 5 + "s", "Price")));
				System.out.println("===========================================================================");
				for(TransactionItem item:transaction.getItemList()){
				System.out.print(String.format("%1$-" + 50 + "s", item.getProduct().getBriefDescription()).substring(0, 50));
				System.out.print(String.format("%0$"+7+"s", String.format("%1$-" + 5 + "s", item.getQty())));
				System.out.println(String.format("%0$"+12+"s", String.format("%1$-" + 5 + "s", item.getPrice())));
				System.out.println("-------------------------------------------------------------------------");
				}
				System.out.print(String.format("%0$"+70+"s", "Total Price:"));
				System.out.println(String.format("%0$"+5+"s", String.format("%1$-" + 5 + "s", Math.round(transaction.calcTotalPrice()))));
				System.out.print(String.format("%0$"+70+"s", "Discount:"));
				System.out.println(String.format("%0$"+5+"s", String.format("%1$-" + 5 + "s", Math.round((transaction.calcTotalPrice()-transaction.calcDiscountPrice())))));
				System.out.print(String.format("%0$"+70+"s", "Final Price:"));
				System.out.println(String.format("%0$"+5+"s", String.format("%1$-" + 5 + "s",  Math.round( transaction.calcDiscountPrice()))));
				System.out.println("##############################################################################");
				 	
				
				cancelAll();
			}
			if (e.getActionCommand().equals("JbBack"))
			{

			}
		}
	}

}
