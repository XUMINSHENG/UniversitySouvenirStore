package sg.edu.nus.iss.usstore.domain;

import java.util.ArrayList;

/**
 * 
 * @author 
 *
 */
public class Category {
	private String code;
	private String name;
	private ArrayList<Vendor> vendorList;
	
	public Category(){
		this.code = "";
		this.name = "";
	}
	
	public Category(String code, String name) {
		this.code = code;
		this.name = name;
		this.vendorList = new ArrayList<Vendor>();
	}
	
	public Category(String code, String name, ArrayList<Vendor> vendorList) {
		super();
		this.code = code;
		this.name = name;
		this.vendorList = vendorList;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Vendor> getVendorList() {
		return vendorList;
	}
	public void setVendorList(ArrayList<Vendor> vendorList) {
		this.vendorList = vendorList;
	}
	
	/**
	 * 
	 * @return most preference vendor
	 */
	public Vendor getPreferenceVendor(){
		Vendor vendor = null;
		
		// has vendor
		if (!this.vendorList.isEmpty()){
			vendor = this.vendorList.get(0);
		}
		
		return vendor;
	}
	
    
    public boolean equalsCode(Category CATOBJ)
    {
        return this.code.equalsIgnoreCase(CATOBJ.code);
    }
    
    public boolean equals(Category CATOBJ)
    {
        return this.code.equalsIgnoreCase(CATOBJ.code) && this.name.equalsIgnoreCase(CATOBJ.name);
    }

	
}
