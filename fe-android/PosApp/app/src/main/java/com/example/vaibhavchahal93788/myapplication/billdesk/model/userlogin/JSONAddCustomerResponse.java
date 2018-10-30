package com.example.vaibhavchahal93788.myapplication.billdesk.model.userlogin;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class JSONAddCustomerResponse{

	@SerializedName("data")
	private List<CustomerResponse> data;

	@SerializedName("status")
	private int status;

	public void setData(List<CustomerResponse> data){
		this.data = data;
	}

	public List<CustomerResponse> getData(){
		return data;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}