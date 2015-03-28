package sg.edu.nus.iss.usstore.domain;

import java.io.IOException;
import java.util.ArrayList;

import sg.edu.nus.iss.usstore.dao.CategoryDao;
import sg.edu.nus.iss.usstore.dao.VendorDao;
import sg.edu.nus.iss.usstore.exception.DataFileException;

/**
 * 
 * @author Xu Minsheng
 *
 */
public class CategoryMgr {
	private ArrayList<Category> categoryList;
	// this VendorList only exist for maintain data consistency
	// for example, if CLO and MUG share one vendor Nancy's , 
	// then in CLO and MUG, their vendors should reference to same instance of vendor  
	private ArrayList<Vendor> vendorList;
	
	private CategoryDao categoryDao;
	private VendorDao vendorDao;
	
	/**
	 * 
	 * @throws IOException
	 * @throws DataFileException
	 */
	public CategoryMgr() throws IOException, DataFileException{
		categoryDao = new CategoryDao();
		vendorDao = new VendorDao();
		loadData();
	}
	
	/**
	 * load data from file
	 * 
	 * @throws IOException
	 * @throws DataFileException
	 */
	public void loadData() throws IOException, DataFileException{
		// load category basic info.
		categoryList = categoryDao.loadDataFromFile();
		// load vendor and set to category
		vendorList = vendorDao.loadDataFromFile(categoryList);
	}
	
	/**
	 * save data to file
	 * 
	 * @throws IOException
	 */
	public void saveData() throws IOException{
		categoryDao.saveDataToFile(categoryList);
		vendorDao.saveDataToFile(categoryList);
	}
	
	
	public void setCategoryList(ArrayList<Category> categoryList) {
        this.categoryList = categoryList;
    }
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	public Category getCategoryByCode(String code){
		for(Category category : this.categoryList){
			if(code.equals(category.getCode())){
				return category;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Category> getCategoryList(){
		return this.categoryList;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Vendor getVendorByName(String name){
		for(Vendor vendor : this.vendorList){
			if(name.equals(vendor.getName())){
				return vendor;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Vendor> getVendorList(){
		return this.vendorList;
	}
	
	/**
	 * 
	 * @param code
	 * @param name
	 * @param vendorList
	 */
	public void addCategory(String code, String name, ArrayList<Vendor> vendorList){
		Category category = new Category(code, name, vendorList);
		this.categoryList.add(category);
		
		this.maintainVendorList();
	}
	
	/**
	 * 
	 * @param code
	 * @param name
	 * @param vendorList
	 */
	public void updCategory(String code, String name, ArrayList<Vendor> vendorList){
		Category category = this.getCategoryByCode(code);
		
		category.setName(name);
		category.setVendorList(vendorList);
		
		this.maintainVendorList();
	}
	
	/**
	 * 
	 * @param code
	 */
	public void delCategory(String code){
		Category category = this.getCategoryByCode(code);
		this.categoryList.remove(category);
		this.maintainVendorList();
	}
	
	/**
	 * When there is any change about category happens,
	 * this method will be called to maintain a non-duplicate vendor list 
	 */
	private void maintainVendorList(){
		ArrayList<Vendor> newVendorList = new ArrayList<Vendor>();
		for(Category category : this.categoryList){
			for(Vendor vendor:category.getVendorList()){
				if(newVendorList.contains(vendor)) continue;
				else newVendorList.add(vendor);
			}
		}
		this.vendorList = newVendorList;
	}
	
}
