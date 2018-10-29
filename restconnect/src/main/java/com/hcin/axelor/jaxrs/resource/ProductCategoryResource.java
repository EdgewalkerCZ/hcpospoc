package com.hcin.axelor.jaxrs.resource;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.ProductCategory;

@Path("/productCategory")
public class ProductCategoryResource extends BaseResourceRead<ProductCategory> {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.base.db.ProductCategory";
	}

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject getCategories(@HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObjects(token);
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getProduct(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObject(id, token);
    }
    
    @Override
    protected ProductCategory createEntity() {
    	return new ProductCategory();
    }

    @Override
    public ProductCategory mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	ProductCategory category = super.mapAxelorJson(jsonObject, token);

    	category.setCode(jsonObject.getString("code"));
    	category.setName(jsonObject.getString("name"));

    	if(jsonObject.getJsonObject("productFamily") != null) {
    		category.setProductFamilyId(jsonObject.getJsonObject("productFamily").getInt(ID));
    	}

    	return category;
    }
}
