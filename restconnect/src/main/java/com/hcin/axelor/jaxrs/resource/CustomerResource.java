package com.hcin.axelor.jaxrs.resource;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcin.axelor.model.Customer;

@Path("/customer")
public class CustomerResource extends BaseResource {
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject getCustomers(@HeaderParam(JSESSIONID) String token) throws Exception {
    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path("com.axelor.apps.base.db.Partner");
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.get(JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getProduct(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token) throws Exception {
        if((id == null) || id.isEmpty()) {
            return Json.createObjectBuilder().add(STATUS, 200).build();
        }
        
    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path("com.axelor.apps.base.db.ProductCategory").path(id);
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.get(JsonObject.class);

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
    			if(jsonDataArray.getJsonObject(i).getBoolean("isCustomer")) { 
    				Customer customer = mapAxelorJson(jsonDataArray.getJsonObject(i), token);
    				String jsonInString = objectMapper.writeValueAsString(customer);
    				JsonReader jsonReader = Json.createReader(new StringReader(jsonInString));
    				jsonArrayBuilder.add(jsonReader.readObject());
    				jsonReader.close();
    				total++;
    			}
    		}
    		
        	if(jsonAxelorResponse.containsKey(TOTAL) && (total > 1))
        		jsonHcinResponse.add(TOTAL, total);

        	jsonHcinResponse.add(DATA, jsonArrayBuilder);
    	}

    	return jsonHcinResponse.build();
    }

    private Customer mapAxelorJson(JsonObject jsonCustomer, String token) {
    	Customer customer = new Customer();

    	customer.setId(jsonCustomer.getInt("id"));
    	customer.setName(jsonCustomer.getString("fullName"));
    	customer.setPhone(jsonCustomer.getString("fixedPhone"));
    	customer.setEmail(getEmail(jsonCustomer.getJsonObject("emailAddress")));
    	customer.setAddress(getAddress(jsonCustomer.getJsonArray("partnerAddressList"), token));

        return customer;
    }

    private String getEmail(JsonObject jsonEmail) {
    	if(jsonEmail == null) 
    		return "";
    	
    	String name = jsonEmail.getString("name");
    	name = name.substring(name.indexOf("[") + 1, name.indexOf("]"));

    	return name;
    }

    private String getAddress(JsonArray jsonAddresses, String token) {
    	if(jsonAddresses.size() < 1)
    		return "";

    	JsonObject jsonAddress = jsonAddresses.getJsonObject(0);
    	int id = jsonAddress.getInt("id"); 

    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path("com.axelor.apps.base.db.Address").path(String.valueOf(id));
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.get(JsonObject.class);

    	if(jsonAxelorResponse.containsKey(DATA)) {
    		JsonArray jsonDataArray = jsonAxelorResponse.getJsonArray(DATA);
    		
    		if(jsonDataArray.size() > 0)
    			return jsonDataArray.getJsonObject(0).getString("fullName");
    	}
		
		return "";
    }
}
