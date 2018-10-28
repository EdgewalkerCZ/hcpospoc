package com.hcin.axelor.jaxrs.resource;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.ProductFamily;

@Path("/productFamily")
public class ProductFamilyResource extends BaseResourceRead<ProductFamily> {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.base.db.ProductFamily";
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
    protected ProductFamily createEntity() {
    	return new ProductFamily();
    }

    @Override
    public ProductFamily mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	ProductFamily family = super.mapAxelorJson(jsonObject, token);

    	family.setCode(jsonObject.getString("code"));
    	family.setName(jsonObject.getString("name"));

    	return family;
    }
}
