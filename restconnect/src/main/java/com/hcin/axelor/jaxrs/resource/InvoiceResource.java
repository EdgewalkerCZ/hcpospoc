package com.hcin.axelor.jaxrs.resource;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

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
import com.hcin.axelor.model.Invoice;

@Path("/invoice")
public class InvoiceResource extends BaseResourceRead {
    
	@Override
	protected String getService() {
		return "com.axelor.apps.account.db.Invoice";
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
    public JsonObject createProduct(@HeaderParam(JSESSIONID) String token, Invoice invoice) throws Exception {
        if(invoice == null) {
            return Json.createObjectBuilder().add(STATUS, 404).build();
        }

    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path(getService());
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.put(Entity.entity(produceAxelorJson(invoice), MediaType.APPLICATION_JSON), JsonObject.class);

    	return processAxelorResponse(jsonAxelorResponse, token);
    }
    
    @POST
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject updateProduct(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token, Invoice invoice) throws Exception {
        if((id == null) || id.isEmpty()) {
            return Json.createObjectBuilder().add(STATUS, 200).build();
        }

        if((invoice == null) || (invoice.getId() == null) || !id.equals(String.valueOf(invoice.getId()))) {
            return Json.createObjectBuilder().add(STATUS, 404).build();
        }

    	ClientConfig config = new ClientConfig();

    	Client client = ClientBuilder.newClient(config);

    	WebTarget target = client.target(getBaseURI()).path(WS).path(REST).path(getService()).path(id);
    	Builder request = target.request().accept(MediaType.APPLICATION_JSON).header("Cookie", JSESSIONID + "=" + token);
    	JsonObject jsonAxelorResponse = request.post(Entity.entity(invoice, MediaType.APPLICATION_JSON), JsonObject.class);

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
    				Invoice invoice = mapAxelorJson(jsonDataArray.getJsonObject(i), token);
    				String jsonInString = objectMapper.writeValueAsString(invoice);
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

    public Invoice mapAxelorJson(JsonObject jsonObject, String token) {
    	Invoice invoice = new Invoice();

    	invoice.setId(jsonObject.getInt("id"));
    	invoice.setInvoiceId(jsonObject.getString("invoiceId"));
    	invoice.setInvoiceDate(jsonObject.getString("invoiceDate"));
    	invoice.setDueDate(jsonObject.getString("dueDate"));

    	if(jsonObject.getJsonObject("company") != null) {
    		invoice.setCompanyId(jsonObject.getJsonObject("company").getInt(ID));
    	}

    	if(jsonObject.getJsonObject("paymentCondition") != null) {
    		invoice.setCompanyId(jsonObject.getJsonObject("paymentCondition").getInt(ID));
    	}

    	if(jsonObject.getJsonObject("partner") != null) {
    		invoice.setCompanyId(jsonObject.getJsonObject("partner").getInt(ID));
    	}

    	if(jsonObject.getJsonObject("paymentMode") != null) {
    		invoice.setCompanyId(jsonObject.getJsonObject("paymentMode").getInt(ID));
    	}

    	if(jsonObject.getJsonObject("currency") != null) {
    		invoice.setCompanyId(jsonObject.getJsonObject("currency").getInt(ID));
    	}

    	JsonArray jsonArray = jsonObject.getJsonArray("invoiceLineList");
    	List<Integer> invoiceLineIdList = invoice.getInvoiceLineIdList();
    	invoiceLineIdList.clear();
    	
    	for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject arrayObject = jsonArray.getJsonObject(i);
			invoiceLineIdList.add(arrayObject.getInt(ID));
		}
    	
		invoice.setCompanyExTaxTotal(getBigDecimalValue(jsonObject, "companyExTaxTotal"));
    	invoice.setCompanyTaxTotal(getBigDecimalValue(jsonObject, "companyTaxTotal"));
    	invoice.setAmountRemaining(getBigDecimalValue(jsonObject, "amountRemaining"));
    	invoice.setAmountPaid(getBigDecimalValue(jsonObject, "amountPaid"));
    	invoice.setCompanyInTaxTotalRemaining(getBigDecimalValue(jsonObject, "companyInTaxTotalRemaining"));
    	invoice.setAmountRejected(getBigDecimalValue(jsonObject, "amountRejected"));
    	invoice.setExTaxTotal(getBigDecimalValue(jsonObject, "exTaxTotal"));
    	invoice.setDirectDebitAmount(getBigDecimalValue(jsonObject, "directDebitAmount"));

    	return invoice;
    }

    private BigDecimal getBigDecimalValue(JsonObject jsonObject, String key) {
    	DecimalFormat decimalFormat = new DecimalFormat("0.00");
    	decimalFormat.setParseBigDecimal(true);
    	
    	try {
			return (BigDecimal)decimalFormat.parse(jsonObject.getString(key));
		} catch (ParseException e) {
			return BigDecimal.ZERO;
		}
    }
    
    private static JsonObject produceAxelorJson(Invoice invoice) throws Exception {
    	JsonObjectBuilder builder = Json.createObjectBuilder();
    	
    	if(invoice.getId() != null) builder.add("id", invoice.getId());

    	if(invoice.getInvoiceId() != null) builder.add("invoiceId", invoice.getInvoiceId());
    	if(invoice.getInvoiceDate() != null) builder.add("invoiceDate", invoice.getInvoiceDate());
    	if(invoice.getDueDate() != null) builder.add("dueDate", invoice.getDueDate());

    	if (invoice.getCompanyId() != null)
    		builder.add("companyId", Json.createObjectBuilder().add("id", invoice.getCompanyId()));

    	if (invoice.getPaymentConditionId() != null)
    		builder.add("paymentCondition", Json.createObjectBuilder().add("id", invoice.getPaymentConditionId()));

    	if (invoice.getCurrencyId() != null)
    		builder.add("partner", Json.createObjectBuilder().add("id", invoice.getCustomerId()));

    	if (invoice.getPaymentModeId() != null)
    		builder.add("paymentMode", Json.createObjectBuilder().add("id", invoice.getPaymentModeId()));

    	builder.add("operationTypeSelect", 3);
    	builder.add("statusSelect", 3);

        List<Integer> invoiceLineIdList = invoice.getInvoiceLineIdList();
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        
        for(int i = 0; i < invoiceLineIdList.size(); i++) {
        	jsonArray.add(Json.createObjectBuilder().add(ID, invoiceLineIdList.get(i)).build());
        }

        builder.add("invoiceLineList", jsonArray.build());

    	if (invoice.getCurrencyId() != null)
    		builder.add("currency", Json.createObjectBuilder().add("id", invoice.getCurrencyId()));

    	if(invoice.getCompanyExTaxTotal() != null) builder.add("companyExTaxTotal", invoice.getCompanyExTaxTotal());
    	if(invoice.getCompanyTaxTotal() != null) builder.add("companyTaxTotal", invoice.getCompanyTaxTotal());
    	if(invoice.getAmountRemaining() != null) builder.add("amountRemaining", invoice.getAmountRemaining());
    	if(invoice.getAmountPaid() != null) builder.add("amountPaid", invoice.getAmountPaid());
    	if(invoice.getCompanyInTaxTotalRemaining() != null) builder.add("companyInTaxTotalRemaining", invoice.getCompanyInTaxTotalRemaining());
    	if(invoice.getAmountRejected() != null) builder.add("amountRejected", invoice.getAmountRejected());
    	if(invoice.getExTaxTotal() != null) builder.add("exTaxTotal", invoice.getExTaxTotal());
    	if(invoice.getDirectDebitAmount()!= null) builder.add("directDebitAmount", invoice.getDirectDebitAmount());
        
        return Json.createObjectBuilder().add(DATA, builder).build();
    }
    
}
