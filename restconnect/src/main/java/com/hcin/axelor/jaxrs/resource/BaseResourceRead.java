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
	protected static final String MSG = "message";

    protected static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/").build();
    }

    protected abstract String getService();
    
    public JsonObject getObjects(String token) throws Exception {
    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);
    	int offset = 0;
		JsonArrayBuilder responseDataBuilder = Json.createArrayBuilder();

    	while(true) {
    		WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path(getService()).queryParam(OFFSET, offset);
    		Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    		JsonObject jsonAxelorResponse = request.get(JsonObject.class);

    		JsonObject jsonObject = processResponseErrors(jsonAxelorResponse);
    		
    		//ends if error has been found
    		if(jsonObject != null)
    			return jsonObject;
    		
    		JsonArray dataArray = jsonAxelorResponse.getJsonArray(DATA);
    		offset += dataArray.size();
    		
    		processAxelorResponse(responseDataBuilder, dataArray, token);

           	int total = jsonAxelorResponse.containsKey(TOTAL) ? jsonAxelorResponse.getInt(TOTAL) : -1;

           	if(dataArray.size() == 0) 
           		break;
           	
            if((total >= 0) && (offset >= total))
            	break;
    	}
    	
    	JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
    	responseBuilder.add(STATUS, 0);
    	JsonArray responseData = responseDataBuilder.build();
    	responseBuilder.add(TOTAL, responseData.size());

    	return responseBuilder.add(DATA, responseData).build();
    }
    
    protected JsonObject processResponseErrors(JsonObject jsonAxelorResponse) {
		if(!jsonAxelorResponse.containsKey(STATUS))
			return Json.createObjectBuilder().add(STATUS, 404).add(MSG, "Status is missing in the response.").build();

		if(jsonAxelorResponse.getInt(STATUS) != 0) {
			JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder().add(STATUS, jsonAxelorResponse.getInt(STATUS));

			if(jsonAxelorResponse.containsKey(DATA)) {
	       		jsonObjectBuilder.add(DATA, jsonAxelorResponse.get(DATA));
	    	}
			
			return jsonObjectBuilder.build();
    	}

    	return null;
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
    
    protected JsonArrayBuilder processAxelorResponse(JsonArrayBuilder responseBuilder, JsonArray jsonDataArray, String token) throws Exception {
    	ObjectMapper objectMapper = new ObjectMapper();

    	for (int i = 0; i < jsonDataArray.size(); i++) {
    		T entity = mapAxelorJson(jsonDataArray.getJsonObject(i), token);

    		if(filter(entity)) {
    			String jsonInString = objectMapper.writeValueAsString(entity);
    			JsonReader jsonReader = Json.createReader(new StringReader(jsonInString));
    			responseBuilder.add(jsonReader.readObject());
    			jsonReader.close();
    		}
    	}

    	return responseBuilder;
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
