package com.hcin.axelor.jaxrs.resource;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.Category;

@Path("/category")
public class CategoryResource extends BaseResourceRead {
    
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
    
    public Category mapAxelorJson(JsonObject jsonCategory, String token) {
    	Category category = new Category();

    	category.setId(jsonCategory.getInt("id"));
    	category.setCode(jsonCategory.getString("code"));
    	category.setName(jsonCategory.getString("name"));

    	return category;
    }
}
