package com.hcin.axelor.jaxrs.resource;

import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
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

    	return processAxelorResponse(jsonAxelorResponse, token);
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

    	JsonObjectBuilder jsonHcinResponse = Json.createObjectBuilder();
    	boolean statusOk = true;
    	
    	if(jsonAxelorResponse.containsKey(STATUS)) {
    		jsonHcinResponse.add(STATUS, jsonAxelorResponse.get(STATUS));
    		statusOk = jsonAxelorResponse.getInt(STATUS) == 0;
    	}

    	if(jsonAxelorResponse.containsKey(OFFSET))
    		jsonHcinResponse.add(OFFSET, jsonAxelorResponse.get(OFFSET));

    	if(jsonAxelorResponse.containsKey(DATA)) {
    		if(statusOk) {
    			JsonArray jsonDataArray = jsonAxelorResponse.getJsonArray(DATA);

    			if (jsonDataArray.size() > 0) {
    				JsonObject jsonObjectData = jsonDataArray.getJsonObject(0);
        			JsonObject jsonAxelorResponsePost = request.post(Entity.entity(produceAxelorJson(mergeEntity(jsonObjectData, token, entity)), MediaType.APPLICATION_JSON), JsonObject.class);

        			return processAxelorResponse(jsonAxelorResponsePost, token);
    			}

    		} else {
        		jsonHcinResponse.add(DATA, jsonAxelorResponse.get(DATA));
    		}
    	}

    	return jsonHcinResponse.build();
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
