package com.hcin.axelor.jaxrs.resource;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.Address;

@Path("/address")
public class AddressResource extends BaseResourceWrite<Address> {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.base.db.Address";
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
    public JsonObject createAddress(@HeaderParam(JSESSIONID) String token, Address entity) throws Exception {
    	return createObject(token, entity);
    }

    @Override
    protected Address createEntity() {
    	return new Address();
    }

    public Address mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	Address address = super.mapAxelorJson(jsonObject, token);

    	address.setFullName(jsonObject.getString("fullName", null));

        return address;
    }

    @Override
    protected JsonObjectBuilder buildAxelorJson(JsonObjectBuilder builder, String token, Address entity) throws Exception {
    	builder = super.buildAxelorJson(builder, token, entity);

    	builder.add("fullName", entity.getFullName());
    	builder.add("addressL4", entity.getFullName());

        return builder;
    }
}
