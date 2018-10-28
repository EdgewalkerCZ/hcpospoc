package com.hcin.axelor.jaxrs.resource;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.Product;

@Path("/product")
public class ProductResource extends BaseResourceWrite<Product> {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.base.db.Product";
	}

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject getProducts(@HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObjects(token);
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getProduct(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObject(id, token);
    }
    
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject createProduct(@HeaderParam(JSESSIONID) String token, Product product) throws Exception {
    	return createObject(token, product);
    }
    
    @POST
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject updateProduct(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token, Product product) throws Exception {
    	return updateObject(id, token, product);
    }

    @Override
    protected Product createEntity() {
    	return new Product();
    }
    
    @Override
    public Product mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	Product product = super.mapAxelorJson(jsonObject, token);

    	product.setCode(jsonObject.getString("code"));
    	product.setName(jsonObject.getString("name"));

    	if(jsonObject.getJsonObject("productCategory") != null) {
    		product.setProductCategoryId(jsonObject.getJsonObject("productCategory").getInt(ID));
    	}

    	if(jsonObject.getJsonObject("productFamily") != null) {
    		product.setProductFamilyId(jsonObject.getJsonObject("productFamily").getInt(ID));
    	}

    	product.setDescription(jsonObject.get("description").toString());
        product.setSalePrice(jsonObject.getString("salePrice"));
        product.setIsGst(jsonObject.getBoolean("blockExpenseTax"));
        product.setIsSellable(jsonObject.getBoolean("sellable"));
    	product.setWarrantyNbrOfMonths(jsonObject.getInt("warrantyNbrOfMonths"));
        product.setQuantity(getBigDecimalValue(jsonObject, "articleVolume"));

    	return product;
    }

    @Override
    protected JsonObjectBuilder buildAxelorJson(JsonObjectBuilder builder, Product product) throws Exception {
    	builder = super.buildAxelorJson(builder, product);
    	
    	builder.add("productTypeSelect", "storable");

    	if(product.getCode() != null) builder.add("code", product.getCode());
    	if(product.getName() != null) builder.add("name", product.getName());

    	if (product.getProductCategoryId() != null)
    		builder.add("productCategory", Json.createObjectBuilder().add("id", product.getProductCategoryId()));

    	if (product.getProductFamilyId() != null)
    		builder.add("productFamily", Json.createObjectBuilder().add("id", product.getProductFamilyId()));

    	if(product.getDescription() != null) builder.add("description", product.getDescription());
    	if(product.getSalePrice() != null) builder.add("salePrice", product.getSalePrice());

    	if(product.getIsGst() != null) builder.add("blockExpenseTax", product.getIsGst());
    	if(product.getIsSellable() != null) builder.add("sellable", product.getIsSellable());
    	if(product.getWarrantyNbrOfMonths() != null) builder.add("warrantyNbrOfMonths", product.getWarrantyNbrOfMonths());
    	if(product.getQuantity() != null) builder.add("articleVolume", product.getQuantity());

    	return builder;
    }

}
