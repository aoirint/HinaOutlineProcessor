package com.kanomiya.hinaoutlineprocessor.structure;

/**
 * Created by Kanomiya in 2017/01.
 */
public class HOPDocumentOwner
{
    public static HOPDocumentOwner empty() {
        return new HOPDocumentOwner("", "", "", "");
    }

    public String name;
    public String mail;
    public String phone;
    public String website;

    public HOPDocumentOwner(String name, String mail, String phone, String website)
    {
        this.name = name;
        this.mail = mail;
        this.phone = phone;
        this.website = website;
    }

    public HOPDocumentOwner clone() {
        return new HOPDocumentOwner(name, mail, phone, website);
    }
}
