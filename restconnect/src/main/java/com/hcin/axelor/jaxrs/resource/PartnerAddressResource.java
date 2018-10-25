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
import com.hcin.axelor.model.PartnerAddress;

@Path("/partnerAddress")
public class PartnerAddressResource extends BaseResource {
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject getAddresses(@HeaderParam(JSESSIONID) String token) throws Exception {
    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path("com.axelor.apps.base.db.PartnerAddress");
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.get(JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAddress(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token) throws Exception {
        if((id == null) || id.isEmpty()) {
            return Json.createObjectBuilder().add(STATUS, 200).build();
        }
        
    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path("com.axelor.apps.base.db.PartnerAddress").path(id);
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.get(JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject createAddress(@HeaderParam(JSESSIONID) String token, PartnerAddress address) throws Exception {
        if(address == null) {
            return Json.createObjectBuilder().add(STATUS, 404).build();
        }

        ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path("com.axelor.apps.base.db.PartnerAddress");
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.put(Entity.entity(produceAxelorJson(address), MediaType.APPLICATION_JSON), JsonObject.class);

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
    			PartnerAddress address = mapAxelorJson(jsonDataArray.getJsonObject(i));
    			String jsonInString = objectMapper.writeValueAsString(address);
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

    public PartnerAddress mapAxelorJson(JsonObject jsonAddress) {
    	PartnerAddress address = new PartnerAddress();

    	address.setId(jsonAddress.getInt("id"));

    	JsonValue jsonValue = jsonAddress.get("address");
    	
    	if ((jsonValue != null) && !jsonValue.equals(JsonValue.NULL)) {
    		JsonObject jsonObject = jsonAddress.getJsonObject("address");
    		address.setAddressId(jsonObject.getInt("id"));
    	} else if (jsonAddress.get("addressId") != null) {
    		address.setAddressId(jsonAddress.getInt("addressId"));
    	}

    	jsonValue = jsonAddress.get("partner");
    	
    	if ((jsonValue != null) && !jsonValue.equals(JsonValue.NULL)) {
    		JsonObject jsonObject = jsonAddress.getJsonObject("partner");
    		address.setPartnerId(jsonObject.getInt("id"));
    	} else if (jsonAddress.get("partnerId") != null) {
    		address.setPartnerId(jsonAddress.getInt("partnerId"));
    	}

        return address;
    }

    private JsonObject produceAxelorJson(PartnerAddress address) {
    	JsonObjectBuilder builder = Json.createObjectBuilder();

    	if (address.getAddressId() != null)
    		builder.add("address", Json.createObjectBuilder().add("id", address.getAddressId()));

    	builder.add("isDefaultAddr", true);
    	builder.add("isDeliveryAddr", true);

    	if (address.getPartnerId() != null)
    		builder.add("partner", Json.createObjectBuilder().add("id", address.getPartnerId()));

    	builder.add("isInvoicingAddr", true);

    	if (address.getId() != null)
    		builder.add("id", address.getId());

        return Json.createObjectBuilder().add(DATA, builder).build();
    }
}
