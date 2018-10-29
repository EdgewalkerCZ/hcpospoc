package com.hcin.axelor.jaxrs.resource;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcin.axelor.model.BaseEntity;

public abstract class BaseResourceRead<T extends BaseEntity> {

	protected static final String JSESSIONID = "JSESSIONID";
	protected static final String WS = "ws";
	protected static final String REST = "rest";

	protected static final String STATUS = "status";
	protected static final String OFFSET = "offset";
	protected static final String TOTAL = "total";
	protected static final String DATA = "data";
	protected static final String ID = "id";

    protected static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/").build();
    }

    protected abstract String getService();
    
    public JsonObject getObjects(String token) throws Exception {
    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path(getService());
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.get(JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    public JsonObject getObject(String id, String token) throws Exception {
        if((id == null) || id.isEmpty()) {
            return Json.createObjectBuilder().add(STATUS, 200).build();
        }
        
    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path(getService()).path(id);
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.get(JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    protected JsonObject processAxelorResponse(JsonObject jsonAxelorResponse, String token) throws Exception {
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
    			JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    			ObjectMapper objectMapper = new ObjectMapper();

    			for (int i = 0; i < jsonDataArray.size(); i++) {
    				T entity = mapAxelorJson(jsonDataArray.getJsonObject(i), token);
    				
    				if(filter(entity)) {
    					String jsonInString = objectMapper.writeValueAsString(entity);
    					JsonReader jsonReader = Json.createReader(new StringReader(jsonInString));
    					jsonArrayBuilder.add(jsonReader.readObject());
    					jsonReader.close();
    				}
    			}

    			JsonArray hcinDataArray = jsonArrayBuilder.build();
    			jsonHcinResponse.add(DATA, hcinDataArray);
    			jsonHcinResponse.add(TOTAL, hcinDataArray.size());
    		} else {
        		jsonHcinResponse.add(DATA, jsonAxelorResponse.get(DATA));
    		}
    	}

    	return jsonHcinResponse.build();
    }

    protected boolean filter(T entity) {
    	return true;
    }

    protected abstract T createEntity();
    
    public T mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	T entity = createEntity();

    	entity.setId(jsonObject.getInt(ID));
    	
    	return entity;
    }

    protected BigDecimal getBigDecimalValue(JsonObject jsonObject, String key) {
    	DecimalFormat decimalFormat = new DecimalFormat("0.00");
    	decimalFormat.setParseBigDecimal(true);
    	
    	try {
			return (BigDecimal)decimalFormat.parse(jsonObject.getString(key));
		} catch (ParseException e) {
			return BigDecimal.ZERO;
		}
    }
    
}
