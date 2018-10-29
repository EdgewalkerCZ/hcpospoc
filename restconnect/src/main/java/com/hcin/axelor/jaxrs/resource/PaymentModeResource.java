package com.hcin.axelor.jaxrs.resource;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.PaymentMode;

@Path("/paymentMode")
public class PaymentModeResource extends BaseResourceRead<PaymentMode> {
    
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
    
    @Override
    protected PaymentMode createEntity() {
    	return new PaymentMode();
    }
    
    @Override
    public PaymentMode mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	PaymentMode paymentMode = super.mapAxelorJson(jsonObject, token);

    	paymentMode.setCode(jsonObject.getString("code"));
    	paymentMode.setName(jsonObject.getString("name"));
    	paymentMode.setInOutSelect(jsonObject.getInt("inOutSelect"));

    	return paymentMode;
    }

	@Override
	protected boolean filter(PaymentMode entity) {
		return entity.getInOutSelect() == 2;
	}

}
