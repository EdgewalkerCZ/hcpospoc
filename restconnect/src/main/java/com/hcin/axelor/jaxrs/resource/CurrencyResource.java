package com.hcin.axelor.jaxrs.resource;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.Currency;

@Path("/currency")
public class CurrencyResource extends BaseResourceRead<Currency> {
    
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

    @Override
    protected Currency createEntity() {
    	return new Currency();
    }
    
	public Currency mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	Currency currency = super.mapAxelorJson(jsonObject, token);

    	currency.setCode(jsonObject.getString("code", null));
    	currency.setName(jsonObject.getString("name", null));
    	currency.setSymbol(jsonObject.getString("symbol", null));

    	return currency;
    }
}
