package com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class LoginSuccessResponse{

	@SerializedName("JSESSIONID")
	private String jSESSIONID;

	public void setJSESSIONID(String jSESSIONID){
		this.jSESSIONID = jSESSIONID;
	}

	public String getJSESSIONID(){
		return jSESSIONID;
	}
}