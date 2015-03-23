package sg.edu.nus.iss.usstore.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sg.edu.nus.iss.usstore.exception.DataFileException;
import sg.edu.nus.iss.usstore.exception.DataNotFoundException;

/**
 * 
 * @author Xu Minsheng
 *
 */
public class Store {

	private StoreKeeperMgr storekeeperMgr;	
	private MemberMgr memberMgr;
	private TransactionMgr transactionMgr;
	private ProductMgr productMgr;
	private CategoryMgr categoryMgr;
	private DiscountMgr discountMgr;
	
	/**
	 * 
	 * @throws IOException
	 * @throws DataFileException
	 */
	public Store() throws IOException, DataFileException {
		storekeeperMgr = new StoreKeeperMgr();
		categoryMgr = new CategoryMgr();
		memberMgr = new MemberMgr();
		discountMgr = new DiscountMgr();
		productMgr = new ProductMgr(this);
		transactionMgr = new TransactionMgr(this);
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void saveData() throws IOException{
		//
		memberMgr.writeFile();
		//transactionMgr.
		productMgr.saveData();
		categoryMgr.saveData();
		discountMgr.saveData();
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean login(String username, String password){
		return storekeeperMgr.checkAuthority(username, password);
	}
	
//  -------------------- transaction related methods	-------------------

	/**
	 * 
	 */
	public Transaction checkout(){
		return new Transaction();
	}
	
	/**
	 * set Customer info. and according to it, get Highest Discount 
	 * 
	 * @param transaction
	 * @param memberId
	 * @return
	 * @throws DataNotFoundException 
	 */
	public Transaction setBillCustomer(Transaction transaction, String memberId) throws DataNotFoundException{
		
		Customer customer;
		Discount discount;
				
		if (memberId==null){
			customer = new Public("");
		}else{
			customer = memberMgr.getMemberByID(memberId);
			if (customer == null) {
				throw new DataNotFoundException("Member",memberId);
			}
			// invoke transaction.setCustomer();
			// transaction.setCostomerID(costomerID);
			
		}
		
		
		// discount = customer.getDiscount(discountMgr.getDiscountList());
		// invoke transaction.setDiscount();
		
		return transaction;
	}
	
	/**
	 * 
	 * @param transaction
	 * @param productId
	 * @param quantity
	 * @return
	 * @throws DataNotFoundException
	 */
	public Transaction addBillItem(Transaction transaction, String productId, int quantity) throws DataNotFoundException{
		
		Product product = productMgr.getProductById(productId);
		
		if (product==null){
			throw new DataNotFoundException("Product",productId);
		}
		
		transaction.addItem(product, quantity);
	
		return transaction;
	}
	
	/**
	 * 
	 * @param transaction
	 * @param productId
	 * @return
	 */
	public Transaction removeBillItem(Transaction transaction, String productId){
		
		Product product = productMgr.getProductById(productId);
		
		transaction.removeItem(product);
	
		return transaction;
	}
	
	/**
	 * 
	 * @param transaction
	 * @param cash
	 * @param redeemLoyaltyPoint
	 * @return
	 */
	public Transaction setPayment(Transaction transaction, double cash, int redeemLoyaltyPoint){
		transaction.setRedeemedLoyaltyPoint(redeemLoyaltyPoint);
		transaction.setCashAmount(cash);
		return transaction;
	}
	
	/**
	 * 
	 */
	public Transaction confirmPayment(Transaction transaction){
		
		// verification product
		
		// invoke TransactionMgr.addTransaction()
		transactionMgr.addTransaction(transaction);
		
		// update product's quantity
		ArrayList<TransactionItem> itemList = transaction.getItemList();
		for(TransactionItem item : itemList){
			productMgr.changeProductQty(item.getProduct(), item.getProduct().getQuantityAvailable() - item.getQty());
		}
		
		// update Member's loyalty point
		if (transaction.getCustomerID() != "public"){
			
		}
		
		// check inventory
		
		return transaction;
	}
	

//  -------------------- member related methods	-------------------
	/**
	 * 
	 * @param name
	 * @param memberId
	 */	
	public void registerMember(String name, String memberId){
		memberMgr.registerMember(name, memberId, -1);
	}
	
	/**
	 * 
	 * @param memberId
	 * @return
	 */
	public Member getMemberById(String memberId){
		return memberMgr.getMemberByID(memberId);
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Member> getMemberList(){
		return memberMgr.getMemberList();
	}
	
//  -------------------- product related methods	-------------------
	
	/**
	 * 
	 * @param name
	 * @param categoryCode
	 * @param briefDescription
	 * @param quantityAvailable
	 * @param price
	 * @param barCode
	 * @param threshold
	 * @param orderQuantity
	 */
	public void addProduct(String name, String categoryCode, String briefDescription, 
			int quantityAvailable, double price, String barCode, int threshold, int orderQuantity){
		
		// invoke categoryMgr.getCategoryByCode() to get category
		// if () throw new DataNotFoundException("Category",categoryCode);
		
//		Product product = new Product(categoryCode + "\3", categoryCode, name, 
//				briefDescription, quantityAvailable, price, barCode, threshold, orderQuantity);
//		
		productMgr.addProduct(name, categoryMgr.getCategoryByCode(categoryCode), briefDescription, quantityAvailable, price, barCode, threshold, orderQuantity);
	}
	
	/**
	 * 
	 * @param product
	 */
	public void addProduct(Product product){
		productMgr.addProduct(product);
	}
	
	/**
	 * 
	 * @param product
	 * @param indenx
	 */
	
	public void modifyProduct(String name, String categoryCode, String briefDescription, 
			int quantityAvailable, double price, String barCode, int threshold, int orderQuantity,int index){
		// 
		productMgr.modifyProduct(name, categoryMgr.getCategoryByCode(categoryCode), briefDescription
				, quantityAvailable, price, barCode, threshold, orderQuantity,index);
		
		//
		
	}
	/**
	 * 
	 * @param index
	 */
	public void deleteProduct(int index){
		productMgr.deleteProduct(index);
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Product> getProductList(){
		return productMgr.getProductList();
	}
	
	/**
	 * 
	 * @return
	 */
	public Product getProductById(String productId){
		return productMgr.getProductById(productId);
	}
	
	/**
	 * 
	 * @param productId
	 * @return
	 */
	public Product getProductByBarCode(String productId){
		return productMgr.getProductById(productId);
	}
	
	public PurchaseOrder getPurchaseOrder(){
		
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		
		ArrayList<Product> productList = null;
		productList = productMgr.checkInventory();

		
		HashMap<Product,Vendor> purchaseList = new HashMap<Product,Vendor>();
		
		// foreach product in productList, 
		// purchaseList.add(product, product.getCategory().getPreferenceVendor())
		
		// purchaseOrder.
		
		return purchaseOrder;
	}
	
//  -------------------- category related methods	-------------------
	/**
	 * 
	 * @param code
	 * @param name
	 * @param vendorNameList
	 */
	public void newCategory(String code, String name, List<String> vendorNameList){
		
		// invoke vendorMgr.getVendorByName()
		
		// invoke categoryMgr.newCategory
		
		
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Category> getCategoryList(){
		return categoryMgr.getCategoryList();
	}
	
	/**
	 * 
	 * @return
	 */
	public Category getCategoryByCode(String code){
		if(getCategoryList().size()==0){
			return null;
		}
		return categoryMgr.getCategoryByCode(code);
	}
	
	/**
	 * 
	 * @param date
	 * @return TransactionList
	 */
	public ArrayList<Transaction> getTransactionByDate(Date date){
		return transactionMgr.getTransactionListByDate(date);
	}

	public ProductMgr getPm() {
		return productMgr;
	}
	
	
	

}
