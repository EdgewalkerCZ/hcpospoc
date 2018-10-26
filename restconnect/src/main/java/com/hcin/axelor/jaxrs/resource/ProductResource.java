package com.hcin.axelor.jaxrs.resource;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
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
import com.hcin.axelor.model.Product;

@Path("/product")
public class ProductResource extends BaseResourceRead {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.base.db.Product";
	}

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject getProducts(@HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObjects(token);
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getProduct(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObject(id, token);
    }
    
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject createProduct(@HeaderParam(JSESSIONID) String token, Product product) throws Exception {
        if(product == null) {
            return Json.createObjectBuilder().add(STATUS, 404).build();
        }

    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path("com.axelor.apps.base.db.Product");
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.put(Entity.entity(produceAxelorJson(product), MediaType.APPLICATION_JSON), JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    @POST
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject updateProduct(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token, Product product) throws Exception {
        if((id == null) || id.isEmpty()) {
            return Json.createObjectBuilder().add(STATUS, 200).build();
        }

        if((product == null) || (product.getId() == null) || !id.equals(String.valueOf(product.getId()))) {
            return Json.createObjectBuilder().add(STATUS, 404).build();
        }

    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path("com.axelor.apps.base.db.Product").path(id);
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.post(Entity.entity(product, MediaType.APPLICATION_JSON), JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    private JsonObject processAxelorResponse(JsonObject jsonAxelorResponse, String token) throws JsonProcessingException {
    	JsonObjectBuilder jsonHcinResponse = Json.createObjectBuilder();
    	boolean statusOk = true;
    	
    	if(jsonAxelorResponse.containsKey(STATUS)) {
    		jsonHcinResponse.add(STATUS, jsonAxelorResponse.get(STATUS));
    		statusOk = jsonAxelorResponse.getInt(STATUS) == 0;
    	}

    	if(jsonAxelorResponse.containsKey(OFFSET))
    		jsonHcinResponse.add(OFFSET, jsonAxelorResponse.get(OFFSET));
    	
    	if(jsonAxelorResponse.containsKey(TOTAL))
    		jsonHcinResponse.add(TOTAL, jsonAxelorResponse.get(TOTAL));

    	if(jsonAxelorResponse.containsKey(DATA)) {
    		if(statusOk) {
    			JsonArray jsonDataArray = jsonAxelorResponse.getJsonArray(DATA);
    			JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    			ObjectMapper objectMapper = new ObjectMapper();

    			for (int i = 0; i < jsonDataArray.size(); i++) {
    				Product product = mapAxelorJson(jsonDataArray.getJsonObject(i), token);
    				String jsonInString = objectMapper.writeValueAsString(product);
    				JsonReader jsonReader = Json.createReader(new StringReader(jsonInString));
    				jsonArrayBuilder.add(jsonReader.readObject());
    				jsonReader.close();
    			}

    			jsonHcinResponse.add(DATA, jsonArrayBuilder);
    		} else
        		jsonHcinResponse.add(DATA, jsonAxelorResponse.get(DATA));
    	}

    	return jsonHcinResponse.build();
    }

    public Product mapAxelorJson(JsonObject jsonProduct, String token) {
    	Product product = new Product();

    	product.setId(jsonProduct.getInt("id"));
    	product.setCode(jsonProduct.getString("code"));
    	product.setName(jsonProduct.getString("name"));

    	JsonObject jsonObject = jsonProduct.getJsonObject("productCategory");
    	
    	product.setProductCategoryId(jsonObject.getInt("id"));

    	jsonObject = jsonProduct.getJsonObject("productFamily");

    	product.setProductFamilyId(jsonProduct.getInt("id"));

    	product.setDescription(jsonProduct.get("description").toString());
        product.setSalePrice(jsonProduct.getString("salePrice"));
        product.setIsGst(jsonProduct.getBoolean("blockExpenseTax"));
        product.setIsSellable(jsonProduct.getBoolean("sellable"));

    	return product;
    }

    private static JsonObject produceAxelorJson(Product product) throws Exception {
    	JsonObjectBuilder builder = Json.createObjectBuilder();
    	
    	builder.add("productTypeSelect", "storable");

    	if(product.getId() != null) builder.add("id", product.getId());
    	if(product.getCode() != null) builder.add("code", product.getCode());
    	if(product.getName() != null) builder.add("name", product.getName());
    	if(product.getDescription() != null) builder.add("description", product.getDescription());
    	if(product.getSalePrice() != null) builder.add("salePrice", product.getSalePrice());

    	if (product.getProductCategoryId() != null)
    		builder.add("productCategory", Json.createObjectBuilder().add("id", product.getProductCategoryId()));

    	if (product.getProductFamilyId() != null)
    		builder.add("productFamily", Json.createObjectBuilder().add("id", product.getProductFamilyId()));

    	return Json.createObjectBuilder().add(DATA, builder).build();
    }
    
}
