package com.hcin.axelor.jaxrs.resource;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.BaseEntity;
import com.hcin.axelor.model.PaymentCondition;

@Path("/paymentCondition")
public class PaymentConditionResource extends BaseResourceRead<PaymentCondition> {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.account.db.PaymentCondition";
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
    protected PaymentCondition createEntity() {
    	return new PaymentCondition();
    }
    
    @Override
    public PaymentCondition mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	PaymentCondition paymentCondition = super.mapAxelorJson(jsonObject, token);

    	paymentCondition.setCode(jsonObject.getString("code"));
    	paymentCondition.setName(jsonObject.getString("name"));
  		paymentCondition.setDaySelect(jsonObject.getInt("daySelect"));
  		paymentCondition.setPaymentTime(jsonObject.getInt("paymentTime"));
  		paymentCondition.setTypeSelect(jsonObject.getInt("typeSelect"));
    	
    	return paymentCondition;
    }

	@Override
	protected boolean filter(BaseEntity entity) {
		return ((PaymentCondition)entity).getTypeSelect() == 1;
	}

    
}
