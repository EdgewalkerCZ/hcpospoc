package com.hcin.axelor.jaxrs.resource;

import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;

import com.hcin.axelor.model.BaseEntity;

public abstract class BaseResourceWrite<T extends BaseEntity> extends BaseResourceRead<T> {
    
    public JsonObject createObject(String token, T entity) throws Exception {
        if(entity == null) {
            return Json.createObjectBuilder().add(STATUS, 404).build();
        }

    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path(getService());
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.put(Entity.entity(produceAxelorJson(buildAxelorJson(token, entity)), MediaType.APPLICATION_JSON), JsonObject.class);
		JsonObject jsonObject = processResponseErrors(jsonAxelorResponse);
		
		//ends if error has been found
		if(jsonObject != null)
			return jsonObject;
		
		JsonArray dataArray = jsonAxelorResponse.getJsonArray(DATA);
		JsonArrayBuilder responseDataBuilder = processAxelorResponse(Json.createArrayBuilder(), dataArray, token);
    	JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
    	responseBuilder.add(STATUS, 0);

    	return responseBuilder.add(DATA, responseDataBuilder).build();
    }
    
    public JsonObject updateObject(String id, String token, T entity) throws Exception {
        if((id == null) || id.isEmpty()) {
            return Json.createObjectBuilder().add(STATUS, 200).build();
        }

        if((entity == null) || (entity.getId() == null) || !id.equals(String.valueOf(entity.getId()))) {
            return Json.createObjectBuilder().add(STATUS, 404).build();
        }

    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path(getService()).path(id);
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.get(JsonObject.class);
		JsonObject jsonObject = processResponseErrors(jsonAxelorResponse);
		
		//ends if error has been found
		if(jsonObject != null)
			return jsonObject;
		
		JsonArray jsonDataArray = jsonAxelorResponse.getJsonArray(DATA);

		if (jsonDataArray.size() > 0) {
			JsonObject jsonObjectData = jsonDataArray.getJsonObject(0);
			JsonObject jsonAxelorResponsePost = request.post(Entity.entity(produceAxelorJson(mergeEntity(jsonObjectData, token, entity)), MediaType.APPLICATION_JSON), JsonObject.class);
			JsonObject jsonObjectPost = processResponseErrors(jsonAxelorResponse);
			
			//ends if error has been found
			if(jsonObjectPost != null)
				return jsonObjectPost;
			
			JsonArray dataArray = jsonAxelorResponsePost.getJsonArray(DATA);
			JsonArrayBuilder responseDataBuilder = processAxelorResponse(Json.createArrayBuilder(), dataArray, token);
	    	JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
	    	responseBuilder.add(STATUS, 0);

	    	return responseBuilder.add(DATA, responseDataBuilder).build();
		}

    	return Json.createObjectBuilder().build();
    }
    
    private JsonObjectBuilder buildAxelorJson(String token, T entity) throws Exception {
        return buildAxelorJson(Json.createObjectBuilder(), token, entity);
    }
    
    protected JsonObjectBuilder buildAxelorJson(JsonObjectBuilder builder, String token, T entity) throws Exception {
    	if(entity.getId() != null) builder.add(ID, entity.getId());

        return builder;
    }
    
    private JsonObject produceAxelorJson(JsonObjectBuilder builder) throws Exception {
        return Json.createObjectBuilder().add(DATA, builder.build()).build();
    }
    
    private JsonObjectBuilder mergeEntity(JsonObject originObject, String token, T entity) throws Exception {
    	JsonObjectBuilder builder = Json.createObjectBuilder();
    	
    	for (Entry<String, JsonValue> entry: originObject.entrySet()) {
    		builder.add(entry.getKey(), entry.getValue());
		}
    	
    	return buildAxelorJson(builder, token, entity);
    }
}
