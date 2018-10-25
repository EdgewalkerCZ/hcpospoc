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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcin.axelor.model.Address;
import com.hcin.axelor.model.BaseEntity;
import com.hcin.axelor.model.Customer;
import com.hcin.axelor.model.EmailAddress;
import com.hcin.axelor.model.PartnerAddress;

@Path("/customer")
public class CustomerResource extends BaseResourceRead {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.base.db.Partner";
	}

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject getCustomers(@HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObjects(token);
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getProduct(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObject(id, token);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject createProduct(@HeaderParam(JSESSIONID) String token, Customer customer) throws Exception {
        if(customer == null) {
            return Json.createObjectBuilder().add(STATUS, 404).build();
        }

        ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path(getService());
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.put(Entity.entity(produceAxelorJson(customer, token), MediaType.APPLICATION_JSON), JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject updateProduct(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token, Customer customer) throws Exception {
        if((id == null) || id.isEmpty()) {
            return Json.createObjectBuilder().add(STATUS, 200).build();
        }

        if((customer == null) || (customer.getId() == null) || !id.equals(String.valueOf(customer.getId()))) {
            return Json.createObjectBuilder().add(STATUS, 404).build();
        }

        ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path(getService()).path(id);
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.post(Entity.entity(produceAxelorJson(customer, token), MediaType.APPLICATION_JSON), JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    private JsonObject processAxelorResponse(JsonObject jsonAxelorResponse, String token) throws Exception {
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
    				Customer customer = (Customer) mapAxelorJson(jsonDataArray.getJsonObject(i), token);
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

    
	@Override
	protected boolean filter(BaseEntity entity) {
		return ((Customer)entity).getIsCustomer();
	}

	@Override
	public BaseEntity mapAxelorJson(JsonObject jsonCustomer, String token) throws Exception {
    	Customer customer = new Customer();

    	customer.setId(jsonCustomer.getInt("id"));
    	customer.setFirstName(jsonCustomer.getString("firstName", null));
    	customer.setName(jsonCustomer.getString("name", null));
    	customer.setPhone(jsonCustomer.getString("fixedPhone", null));
    	customer.setDescription(jsonCustomer.getString("description", null));
    	customer.setIsCustomer(jsonCustomer.getBoolean("isCustomer"));
    	
    	if(jsonCustomer.containsKey("partnerCategory") && !jsonCustomer.get("partnerCategory").equals(JsonValue.NULL)) {
    		customer.setPartnerCategoryId(jsonCustomer.getJsonObject("partnerCategory").getInt(ID));
    	}

    	if(jsonCustomer.getJsonObject("emailAddress") != null) {
        	customer.setEmail(getEmail(jsonCustomer.getJsonObject("emailAddress")));
        	customer.setEmailAddressId(jsonCustomer.getJsonObject("emailAddress").getInt(ID));
    	}

    	if(jsonCustomer.getJsonArray("partnerAddressList") != null) {
    		Address address = getAddress(jsonCustomer.getJsonArray("partnerAddressList"), token);
    		
    		if(address != null) {
    			customer.setAddress(address.getFullName());
    			customer.setPartnerAddressId(jsonCustomer.getJsonArray("partnerAddressList").getJsonObject(0).getInt(ID));
    		}
    	}

        return customer;
    }

    private String getEmail(JsonObject jsonEmail) {
    	if(jsonEmail == null) 
    		return "";
    	
    	String name = jsonEmail.getString("name", null);
    	if(name.indexOf("[") >= 0)
    		name = name.substring(name.indexOf("[") + 1, name.indexOf("]"));

    	return name;
    }

    private Address getAddress(JsonArray jsonAddresses, String token) throws Exception {
    	if(jsonAddresses.size() < 1)
    		return null;

    	JsonObject jsonAddress = jsonAddresses.getJsonObject(0);
    	int id = jsonAddress.getInt("id"); 

    	PartnerAddressResource partnerAddressResource = new PartnerAddressResource();
        JsonObject jsonObject = partnerAddressResource.getAddress(String.valueOf(id), token);

        if(jsonObject.containsKey(DATA)) {
    		JsonArray jsonDataArray = jsonObject.getJsonArray(DATA);
    		
    		if(jsonDataArray.size() > 0) {
    	        PartnerAddress partnerAddress = partnerAddressResource.mapAxelorJson(jsonDataArray.getJsonObject(0), token);

    	        AddressResource addressResource = new AddressResource();
    	        JsonObject jsonObjectAdr = addressResource.getAddress(String.valueOf(partnerAddress.getAddressId()), token);

    	        if(jsonObjectAdr.containsKey(DATA)) {
    	    		JsonArray jsonDataArrayAddr = jsonObjectAdr.getJsonArray(DATA);
    	    		
    	    		if(jsonDataArray.size() > 0) {
    	    	        Address address = (Address) addressResource.mapAxelorJson(jsonDataArrayAddr.getJsonObject(0), token);

    	    	        return address;
    	    		}
    	        }
    		}
        }
        
        return null;
    }

    public JsonObject produceAxelorJson(Customer customer) throws Exception {
    	return produceAxelorJson(customer, null);
    }
    
    public JsonObject produceAxelorJson(Customer customer, String token) throws Exception {
    	JsonObjectBuilder builder = Json.createObjectBuilder();

    	builder.add("isSupplier", false);
    	builder.add("isCustomer", true);
    	
    	if(customer.getPhone() != null) builder.add("fixedPhone", customer.getPhone());
    	if(customer.getFirstName() != null) builder.add("firstName", customer.getFirstName());
    	if(customer.getName() != null) builder.add("name", customer.getName());
    	if(customer.getFullName() != null) builder.add("fullName", customer.getFullName());
    	if(customer.getDescription() != null) builder.add("description", customer.getDescription());

    	if (customer.getPartnerCategoryId() != null)
    		builder.add("partnerCategory", Json.createObjectBuilder().add("id", customer.getPartnerCategoryId()));

    	if (customer.getEmailAddressId() == null)
    		customer.setEmailAddressId(createEmail(customer, token));
    	
		builder.add("emailAddress", Json.createObjectBuilder().add("id", customer.getEmailAddressId()));
    	
    	if (customer.getPartnerAddressId() == null)
    		customer.setPartnerAddressId(createPartnerAddress(customer, token));
    	
		builder.add("partnerAddressList", Json.createArrayBuilder().add(Json.createObjectBuilder().add("id", customer.getPartnerAddressId())));
    	
    	return Json.createObjectBuilder().add(DATA, builder).build();
    }
    
    private Integer createEmail(Customer customer, String token) throws Exception {
    	EmailAddressResource resource = new EmailAddressResource();
    	EmailAddress emailAddress = new EmailAddress();
    	emailAddress.setAddress(customer.getEmail());
    	emailAddress.setName(customer.getFullName() + " [" + customer.getEmail() + "]");

    	JsonObject response = resource.createAddress(token, emailAddress);

    	if(response.containsKey(DATA)) {
    		JsonArray jsonDataArray = response.getJsonArray(DATA);
    		
    		if(jsonDataArray.size() > 0)
    			return response.getJsonArray(DATA).getJsonObject(0).getInt("id");
    	}

    	throw new Exception("Email has not been created in the system.");
    }

    private Integer createPartnerAddress(Customer customer, String token) throws Exception {
        PartnerAddressResource resource = new PartnerAddressResource();
    	PartnerAddress address = new PartnerAddress();
    	address.setAddressId(createAddress(customer, token));

    	JsonObject response = resource.createAddress(token, address);

    	if(response.containsKey(DATA)) {
    		JsonArray jsonDataArray = response.getJsonArray(DATA);
    		
    		if(jsonDataArray.size() > 0)
    			return response.getJsonArray(DATA).getJsonObject(0).getInt("id");
    	}

    	throw new Exception("Partner address has not been created in the system.");
    }

    private Integer createAddress(Customer customer, String token) throws Exception {
    	AddressResource resource = new AddressResource();
    	Address address = new Address();
    	address.setFullName(customer.getAddress());

    	JsonObject response = resource.createAddress(token, address);

    	if(response.containsKey(DATA)) {
    		JsonArray jsonDataArray = response.getJsonArray(DATA);
    		
    		if(jsonDataArray.size() > 0)
    			return response.getJsonArray(DATA).getJsonObject(0).getInt("id");
    	}

    	throw new Exception("Address has not been created in the system.");
    }

}
