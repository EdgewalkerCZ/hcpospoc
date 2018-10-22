package com.example.vaibhavchahal93788.myapplication.billdesk.model;

import java.util.ArrayList;

public class JsonCustomerSet {

    private String status;

    private ArrayList<JsonCustomer> customers;

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public ArrayList<JsonCustomer> getCustomers ()
    {
        return customers;
    }

    public void setCustomers (ArrayList<JsonCustomer> customers)
    {
        this.customers = customers;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [status = "+status+", customers = "+customers+"]";
    }
}