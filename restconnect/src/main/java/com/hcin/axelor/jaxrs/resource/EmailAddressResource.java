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

import com.hcin.axelor.model.EmailAddress;

@Path("/emailAddress")
public class EmailAddressResource extends BaseResourceWrite<EmailAddress> {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.message.db.EmailAddress";
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
    public JsonObject createAddress(@HeaderParam(JSESSIONID) String token, EmailAddress emailAddress) throws Exception {
    	return createObject(token, emailAddress);
    }
    
    @Override
    protected EmailAddress createEntity() {
    	return new EmailAddress();
    }

    @Override
    public EmailAddress mapAxelorJson(JsonObject jsonAddress, String token) throws Exception {
    	EmailAddress address = super.mapAxelorJson(jsonAddress, token);

    	address.setAddress(jsonAddress.getString("address"));
    	address.setName(jsonAddress.getString("name"));

    	JsonValue jsonValue = jsonAddress.get("partner");
    	
    	if ((jsonValue != null) && !jsonValue.equals(JsonValue.NULL)) {
    		JsonObject jsonObject = jsonAddress.getJsonObject("partner");
    		address.setPartnerId(jsonObject.getInt("id"));
    	}

        return address;
    }

    @Override
    protected JsonObjectBuilder buildAxelorJson(JsonObjectBuilder builder, EmailAddress address) throws Exception {
    	builder = super.buildAxelorJson(builder, address);

    	builder.add("address", address.getAddress());
    	
    	if (address.getPartnerId() != null)
    		builder.add("partner", Json.createObjectBuilder().add("id", address.getPartnerId()));
    	
    	builder.add("name", address.getName());
    	if (address.getId() != null)
    		builder.add("id", address.getId());

        return builder;
    }
}
