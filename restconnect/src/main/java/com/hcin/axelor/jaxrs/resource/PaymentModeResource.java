package com.hcin.axelor.jaxrs.resource;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.BaseEntity;
import com.hcin.axelor.model.PaymentMode;

@Path("/paymentMode")
public class PaymentModeResource extends BaseResourceRead {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.account.db.PaymentMode";
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
    
    public PaymentMode mapAxelorJson(JsonObject jsonObject, String token) {
    	PaymentMode paymentMode = new PaymentMode();

    	paymentMode.setId(jsonObject.getInt("id"));
    	paymentMode.setCode(jsonObject.getString("code"));
    	paymentMode.setName(jsonObject.getString("name"));
    	paymentMode.setInOutSelect(jsonObject.getInt("inOutSelect"));

    	return paymentMode;
    }

	@Override
	protected boolean filter(BaseEntity entity) {
		return ((PaymentMode)entity).getInOutSelect() == 2;
	}

}
