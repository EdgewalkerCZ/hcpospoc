package com.hcin.axelor.jaxrs.resource;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.BaseEntity;
import com.hcin.axelor.model.Currency;

@Path("/currency")
public class CurrencyResource extends BaseResourceRead {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.base.db.Currency";
	}

    @GET
    @Produces({MediaType.APPLICATION_JSON})
	public JsonObject getCurrencies(@HeaderParam(JSESSIONID) String token) throws Exception {
		return getObjects(token);
	}

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public JsonObject getCurrency(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token) throws Exception {
		return getObject(id, token);
	}

	public BaseEntity mapAxelorJson(JsonObject jsonObject, String token) {
    	Currency currency = new Currency();

    	currency.setId(jsonObject.getInt(ID));
    	currency.setCode(jsonObject.getString("code", null));
    	currency.setName(jsonObject.getString("name", null));
    	currency.setSymbol(jsonObject.getString("symbol", null));

    	return currency;
    }
}
