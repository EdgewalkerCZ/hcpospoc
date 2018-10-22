package com.hcin.axelor.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
//@XmlRootElement       //only needed if we also want to generate XML
public class Product {

    private Integer id;
    private String code;
    private String name;
    private String productCategory;
    private String category;
    private String productFamily;
    private Integer quantity;
    private String description;
    private String price;
    private Boolean isGst;
    private Boolean isSellable;
    
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
	public String getProductCategory() {
		return productCategory;
	}
	
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getProductFamily() {
		return productFamily;
	}

	public void setProductFamily(String productFamily) {
		this.productFamily = productFamily;
	}

	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPrice() {
		return price;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	
	public Boolean getIsGst() {
		return isGst;
	}
	
	public void setIsGst(Boolean isGst) {
		this.isGst = isGst;
	}

	public Boolean getIsSellable() {
		return isSellable;
	}

	public void setIsSellable(Boolean isSellable) {
		this.isSellable = isSellable;
	}
	
	
/*    {
 "purchasesUnit": null,
 "barcodeTypeConfig": null,
 "id": 11,
 "productSubTypeSelect": 3,
 "productTypeSelect": "storable",
 "isActivity": false,
 "warrantyNbrOfMonths": 12,
 "salePrice": "60.0000000000",
 "costPrice": "54.0000000000",
 "picture":          {
    "fileName": "COMP-0001.png",
    "id": 165,
    "$version": 0
 },
 "name": "Hard Disk SATA 1To",
 "blockExpenseTax": false,
 "productFamily":          {
    "code": "COMP",
    "name": "Components",
    "id": 3,
    "$version": 0
 },
 "description": "Internal HDD 3,5'' - Capacity : 1 To / Memory : 64 Mo / Speed : 7200 trs/min",
 "productCategory":          {
    "code": "HDD",
    "name": "Hard Disk",
    "id": 5,
    "$version": 0
 },
 "saleSupplySelect": 1,
 "saleCurrency":          {
    "code": "EUR",
    "name": "Euro",
    "id": 46,
    "$version": 0
 },
};
*/	
	
	
	
	
	


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}