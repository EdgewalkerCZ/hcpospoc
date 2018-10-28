package com.hcin.axelor.jaxrs.resource;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hcin.axelor.model.Invoice;

@Path("/invoice")
public class InvoiceResource extends BaseResourceWrite<Invoice> {
    
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
    	return createObject(token, invoice);
    }
    
    @POST
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject updateProduct(@PathParam("id") String id, @HeaderParam(JSESSIONID) String token, Invoice invoice) throws Exception {
    	return updateObject(id, token, invoice);
    }

    @Override
    protected Invoice createEntity() {
    	return new Invoice();
    }
    
    @Override
    public Invoice mapAxelorJson(JsonObject jsonObject, String token) throws Exception {
    	Invoice invoice = super.mapAxelorJson(jsonObject, token);

    	invoice.setInvoiceId(jsonObject.getString("invoiceId"));
    	invoice.setInvoiceDate(jsonObject.getString("invoiceDate"));
    	invoice.setDueDate(jsonObject.getString("dueDate"));

    	if(jsonObject.getJsonObject("company") != null) {
    		invoice.setCompanyId(jsonObject.getJsonObject("company").getInt(ID));
    	}

    	if(jsonObject.getJsonObject("paymentCondition") != null) {
    		invoice.setPaymentConditionId(jsonObject.getJsonObject("paymentCondition").getInt(ID));
    	}

    	if(jsonObject.getJsonObject("partner") != null) {
    		invoice.setCustomerId(jsonObject.getJsonObject("partner").getInt(ID));
    	}

    	if(jsonObject.getJsonObject("paymentMode") != null) {
    		invoice.setPaymentModeId(jsonObject.getJsonObject("paymentMode").getInt(ID));
    	}

    	if(jsonObject.getJsonObject("currency") != null) {
    		invoice.setCurrencyId(jsonObject.getJsonObject("currency").getInt(ID));
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

    @Override
    protected JsonObjectBuilder buildAxelorJson(JsonObjectBuilder builder, Invoice invoice) throws Exception {
    	builder = super.buildAxelorJson(builder, invoice);
    	
    	if(invoice.getId() != null) builder.add("id", invoice.getId());

    	if(invoice.getInvoiceId() != null) builder.add("invoiceId", invoice.getInvoiceId());
    	if(invoice.getInvoiceDate() != null) builder.add("invoiceDate", invoice.getInvoiceDate());
    	if(invoice.getDueDate() != null) builder.add("dueDate", invoice.getDueDate());

    	if (invoice.getCompanyId() != null)
    		builder.add("company", Json.createObjectBuilder().add("id", invoice.getCompanyId()));

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
        
        return builder;
    }
    
}
