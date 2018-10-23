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
import com.hcin.axelor.model.Category;

@Path("/category")
public class CategoryResource extends BaseResource {
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject getCategories(@HeaderParam(JSESSIONID) String token) throws Exception {
    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path("com.axelor.apps.base.db.ProductCategory");
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.get(JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse);
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

    	return processAxelorResponse(jsonAxelorResponse);
    }
    
    private JsonObject processAxelorResponse(JsonObject jsonAxelorResponse) throws JsonProcessingException {
    	JsonObjectBuilder jsonHcinResponse = Json.createObjectBuilder();

    	if(jsonAxelorResponse.containsKey(STATUS))
    		jsonHcinResponse.add(STATUS, jsonAxelorResponse.get(STATUS));

    	if(jsonAxelorResponse.containsKey(OFFSET))
    		jsonHcinResponse.add(OFFSET, jsonAxelorResponse.get(OFFSET));
    	
    	if(jsonAxelorResponse.containsKey(TOTAL))
    		jsonHcinResponse.add(TOTAL, jsonAxelorResponse.get(TOTAL));

    	if(jsonAxelorResponse.containsKey(DATA)) {
    		JsonArray jsonDataArray = jsonAxelorResponse.getJsonArray(DATA);
    		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    		ObjectMapper objectMapper = new ObjectMapper();
    		
    		for (int i = 0; i < jsonDataArray.size(); i++) {
    			Category category = mapAxelorJson(jsonDataArray.getJsonObject(i));
    			String jsonInString = objectMapper.writeValueAsString(category);
    			JsonReader jsonReader = Json.createReader(new StringReader(jsonInString));
        		jsonArrayBuilder.add(jsonReader.readObject());
    			jsonReader.close();
    		}
    		
    		jsonHcinResponse.add(DATA, jsonArrayBuilder);
    	}

    	return jsonHcinResponse.build();
    }

    private static Category mapAxelorJson(JsonObject jsonCategory) {
    	Category category = new Category();

    	category.setId(jsonCategory.getInt("id"));
    	category.setCode(jsonCategory.getString("code"));
    	category.setName(jsonCategory.getString("name"));

    	return category;
    }
}
