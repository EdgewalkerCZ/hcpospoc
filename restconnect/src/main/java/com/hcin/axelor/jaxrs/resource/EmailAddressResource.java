package com.hcin.axelor.jaxrs.resource;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcin.axelor.model.EmailAddress;

@Path("/emailAddress")
public class EmailAddressResource extends BaseResourceRead {
    
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
        if(emailAddress == null) {
            return Json.createObjectBuilder().add(STATUS, 404).build();
        }

        ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path(getService());
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.put(Entity.entity(produceAxelorJson(emailAddress), MediaType.APPLICATION_JSON), JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    private JsonObject processAxelorResponse(JsonObject jsonAxelorResponse, String token) throws JsonProcessingException {
    	JsonObjectBuilder jsonHcinResponse = Json.createObjectBuilder();

    	if(jsonAxelorResponse.containsKey(STATUS))
    		jsonHcinResponse.add(STATUS, jsonAxelorResponse.get(STATUS));

    	if(jsonAxelorResponse.containsKey(OFFSET))
    		jsonHcinResponse.add(OFFSET, jsonAxelorResponse.get(OFFSET));
    	
    	int total = 0;
    	
    	if(jsonAxelorResponse.containsKey(DATA)) {
    		JsonArray jsonDataArray = jsonAxelorResponse.getJsonArray(DATA);
    		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    		ObjectMapper objectMapper = new ObjectMapper();

    		for (int i = 0; i < jsonDataArray.size(); i++) {
    			EmailAddress emailAddress = mapAxelorJson(jsonDataArray.getJsonObject(i), token);
    			String jsonInString = objectMapper.writeValueAsString(emailAddress);
    			JsonReader jsonReader = Json.createReader(new StringReader(jsonInString));
    			jsonArrayBuilder.add(jsonReader.readObject());
    			jsonReader.close();
    			total++;
    		}

    		if(jsonAxelorResponse.containsKey(TOTAL) && (total > 0))
    			jsonHcinResponse.add(TOTAL, total);

    		jsonHcinResponse.add(DATA, jsonArrayBuilder);
    	}

    	return jsonHcinResponse.build();
    }

    public EmailAddress mapAxelorJson(JsonObject jsonAddress, String token) {
    	EmailAddress address = new EmailAddress();

    	address.setId(jsonAddress.getInt("id"));
    	address.setAddress(jsonAddress.getString("address"));
    	address.setName(jsonAddress.getString("name"));

    	JsonValue jsonValue = jsonAddress.get("partner");
    	
    	if ((jsonValue != null) && !jsonValue.equals(JsonValue.NULL)) {
    		JsonObject jsonObject = jsonAddress.getJsonObject("partner");
    		address.setPartnerId(jsonObject.getInt("id"));
    	}

        return address;
    }

    private JsonObject produceAxelorJson(EmailAddress address) {
    	JsonObjectBuilder builder = Json.createObjectBuilder();

    	builder.add("address", address.getAddress());
    	
    	if (address.getPartnerId() != null)
    		builder.add("partner", Json.createObjectBuilder().add("id", address.getPartnerId()));
    	
    	builder.add("name", address.getName());
    	if (address.getId() != null)
    		builder.add("id", address.getId());

        return Json.createObjectBuilder().add(DATA, builder).build();
    }
}
