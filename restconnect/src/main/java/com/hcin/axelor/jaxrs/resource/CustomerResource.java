package com.hcin.axelor.jaxrs.resource;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.Address;
import com.hcin.axelor.model.Customer;
import com.hcin.axelor.model.EmailAddress;
import com.hcin.axelor.model.PartnerAddress;

@Path("/customer")
public class CustomerResource extends BaseResourceWrite<Customer> {
    
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
    public JsonObject getCustomers(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token) throws Exception {
    	return getObject(id, token);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject createCustomers(@HeaderParam(JSESSIONID) String token, Customer entity) throws Exception {
    	return createObject(token, entity);
    }
    
    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject updateCustomers(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token, Customer entity) throws Exception {
    	return updateObject(id, token, entity);
    }
    
	@Override
	protected boolean filter(Customer entity) {
		return entity.getIsCustomer();
	}

	@Override
	protected Customer createEntity() {
		return new Customer();
	}
	
	@Override
	public Customer mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	Customer customer = super.mapAxelorJson(jsonObject, token);

    	customer.setId(jsonObject.getInt("id"));
    	customer.setFirstName(jsonObject.getString("firstName", null));
    	customer.setName(jsonObject.getString("name", null));
    	customer.setPhone(jsonObject.getString("fixedPhone", null));
    	customer.setDescription(jsonObject.getString("description", null));
    	customer.setIsCustomer(jsonObject.getBoolean("isCustomer"));
    	
    	if(jsonObject.containsKey("partnerCategory") && !jsonObject.get("partnerCategory").equals(JsonValue.NULL)) {
    		customer.setPartnerCategoryId(jsonObject.getJsonObject("partnerCategory").getInt(ID));
    	}

    	if(jsonObject.containsKey("emailAddress") && !jsonObject.get("emailAddress").equals(JsonValue.NULL)) {
        	customer.setEmail(getEmail(jsonObject.getJsonObject("emailAddress")));
        	customer.setEmailAddressId(jsonObject.getJsonObject("emailAddress").getInt(ID));
    	}

    	if(jsonObject.getJsonArray("partnerAddressList") != null) {
    		Address address = getAddress(jsonObject.getJsonArray("partnerAddressList"), token);
    		
    		if(address != null) {
    			customer.setAddress(address.getFullName());
    			customer.setPartnerAddressId(jsonObject.getJsonArray("partnerAddressList").getJsonObject(0).getInt(ID));
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

    @Override
    protected JsonObjectBuilder buildAxelorJson(JsonObjectBuilder builder, String token, Customer entity) throws Exception {
    	builder = super.buildAxelorJson(builder, token,  entity);

    	builder.add("isSupplier", false);
    	builder.add("isCustomer", true);
    	
    	if(entity.getPhone() != null) builder.add("fixedPhone", entity.getPhone());
    	if(entity.getFirstName() != null) builder.add("firstName", entity.getFirstName());
    	if(entity.getName() != null) builder.add("name", entity.getName());
    	if(entity.getFullName() != null) builder.add("fullName", entity.getFullName());
    	if(entity.getDescription() != null) builder.add("description", entity.getDescription());

    	if (entity.getPartnerCategoryId() != null)
    		builder.add("partnerCategory", Json.createObjectBuilder().add("id", entity.getPartnerCategoryId()));

    	if (entity.getEmailAddressId() == null)
    		entity.setEmailAddressId(createEmail(entity, token));
    	
		builder.add("emailAddress", Json.createObjectBuilder().add("id", entity.getEmailAddressId()));
    	
    	if (entity.getPartnerAddressId() == null)
    		entity.setPartnerAddressId(createPartnerAddress(entity, token));
    	
		builder.add("partnerAddressList", Json.createArrayBuilder().add(Json.createObjectBuilder().add("id", entity.getPartnerAddressId())));
    	
    	return builder;
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
