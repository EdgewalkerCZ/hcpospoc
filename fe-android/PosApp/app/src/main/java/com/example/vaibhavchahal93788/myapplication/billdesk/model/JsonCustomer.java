package com.example.vaibhavchahal93788.myapplication.billdesk.model;

public class JsonCustomer {
    private String phone;

    private String email;

    private String name;

    private String img;

    public String getPhone ()
    {
        return phone;
    }

    public void setPhone (String phone)
    {
        this.phone = phone;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getImg ()
    {
        return img;
    }

    public void setImg (String img)
    {
        this.img = img;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [phone = "+phone+", email = "+email+", name = "+name+", img = "+img+"]";
    }
}