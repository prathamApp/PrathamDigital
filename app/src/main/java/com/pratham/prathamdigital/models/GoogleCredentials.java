package com.pratham.prathamdigital.models;


public class GoogleCredentials {

    public String GoogleID;
    public String PersonPhotoUrl, Email, PersonName;
    public Integer IntroShown;

    public String getGoogleID() {
        return GoogleID;
    }

    public void setGoogleID(String googleID) {
        this.GoogleID = googleID;
    }

    public String getPersonPhotoUrl() {
        return PersonPhotoUrl;
    }

    public void setPersonPhotoUrl(String personPhotoUrl) {
        this.PersonPhotoUrl = personPhotoUrl;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getPersonName() {
        return PersonName;
    }

    public void setPersonName(String personName) {
        this.PersonName = personName;
    }

    public Integer getIntroShown() {
        return IntroShown;
    }

    public void setIntroShown(Integer introShown) {
        this.IntroShown = introShown;
    }
}