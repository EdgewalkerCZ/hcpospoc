package com.hcin.axelor.jaxrs.resource;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

public class BaseResource {

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
}
