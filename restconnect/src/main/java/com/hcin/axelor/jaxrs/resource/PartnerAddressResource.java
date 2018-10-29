package com.hcin.axelor.jaxrs.resource;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.PartnerAddress;

@Path("/partnerAddress")
public class PartnerAddressResource extends BaseResourceWrite<PartnerAddress> {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.base.db.PartnerAddress";
	}

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject getAddresses(@HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObjects(token);
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAddress(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObject(id, token);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject createAddress(@HeaderParam(JSESSIONID) String token, PartnerAddress address) throws Exception {
    	return createObject(token, address);
    }
    
    @Override
    protected PartnerAddress createEntity() {
    	return new PartnerAddress();
    }
    
    @Override
    public PartnerAddress mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	PartnerAddress address = super.mapAxelorJson(jsonObject, token);

    	JsonValue jsonValue = jsonObject.get("address");
    	
    	if ((jsonValue != null) && !jsonValue.equals(JsonValue.NULL)) {
    		address.setAddressId(jsonObject.getJsonObject("address").getInt(ID));
    	} else if (jsonObject.get("addressId") != null) {
    		address.setAddressId(jsonObject.getInt("addressId"));
    	}

    	jsonValue = jsonObject.get("partner");
    	
    	if ((jsonValue != null) && !jsonValue.equals(JsonValue.NULL)) {
    		address.setPartnerId(jsonObject.getJsonObject("partner").getInt(ID));
    	} else if (jsonObject.get("partnerId") != null) {
    		address.setPartnerId(jsonObject.getInt("partnerId"));
    	}

        return address;
    }
    
    @Override
    protected JsonObjectBuilder buildAxelorJson(JsonObjectBuilder builder, String token, PartnerAddress address) throws Exception {
    	builder = super.buildAxelorJson(builder, token, address);

    	if (address.getAddressId() != null)
    		builder.add("address", Json.createObjectBuilder().add("id", address.getAddressId()));

    	builder.add("isDefaultAddr", true);
    	builder.add("isDeliveryAddr", true);

    	if (address.getPartnerId() != null)
    		builder.add("partner", Json.createObjectBuilder().add("id", address.getPartnerId()));

    	builder.add("isInvoicingAddr", true);

        return builder;
    }
}
