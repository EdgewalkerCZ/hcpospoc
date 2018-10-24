package com.hcin.axelor.jaxrs.resource;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

import com.hcin.axelor.model.Credentials;


@Path("/login")
public class LoginResource extends BaseResource {
    
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public JsonObject postLogin(Credentials credentials) throws Exception {
        ClientConfig config = new ClientConfig();

        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(getBaseURI()).path("login.jsp");
        Builder request = target.request().accept(MediaType.APPLICATION_JSON);
        Response response = request.post(Entity.entity(credentials, MediaType.APPLICATION_JSON));

        Map<String, NewCookie> cookies = response.getCookies();

        if (cookies.containsKey(JSESSIONID)) {
        	return Json.createObjectBuilder().add(JSESSIONID, cookies.get(JSESSIONID).getValue()).build();
        }
        
        return Json.createObjectBuilder().add("error", response.toString()).build();
    }

}
