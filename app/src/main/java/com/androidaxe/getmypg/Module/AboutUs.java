package com.androidaxe.getmypg.Module;

public class AboutUs {

    String ContactUs, Description, Developer, Gmail, HelpCustomer, HelpSeller, LinkedIn, NewSeller;

    public AboutUs(){}

    public AboutUs(String contactUs, String description, String developer, String gmail, String helpCustomer, String helpSeller, String linkedIn, String newSeller) {
        ContactUs = contactUs;
        Description = description;
        Developer = developer;
        Gmail = gmail;
        HelpCustomer = helpCustomer;
        HelpSeller = helpSeller;
        LinkedIn = linkedIn;
        NewSeller = newSeller;
    }

    public String getContactUs() {
        return ContactUs;
    }

    public void setContactUs(String contactUs) {
        ContactUs = contactUs;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDeveloper() {
        return Developer;
    }

    public void setDeveloper(String developer) {
        Developer = developer;
    }

    public String getGmail() {
        return Gmail;
    }

    public void setGmail(String gmail) {
        Gmail = gmail;
    }

    public String getHelpCustomer() {
        return HelpCustomer;
    }

    public void setHelpCustomer(String helpCustomer) {
        HelpCustomer = helpCustomer;
    }

    public String getHelpSeller() {
        return HelpSeller;
    }

    public void setHelpSeller(String helpSeller) {
        HelpSeller = helpSeller;
    }

    public String getLinkedIn() {
        return LinkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        LinkedIn = linkedIn;
    }

    public String getNewSeller() {
        return NewSeller;
    }

    public void setNewSeller(String newSeller) {
        NewSeller = newSeller;
    }
}
