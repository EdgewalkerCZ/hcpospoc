package com.hcin.axelor.jaxrs.resource;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.BaseEntity;
import com.hcin.axelor.model.PaymentCondition;

@Path("/paymentCondition")
public class PaymentConditionResource extends BaseResourceRead {
    
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
    
    public BaseEntity mapAxelorJson(JsonObject jsonObject, String token) {
    	PaymentCondition paymentCondition = new PaymentCondition();

    	paymentCondition.setId(jsonObject.getInt("id"));
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
