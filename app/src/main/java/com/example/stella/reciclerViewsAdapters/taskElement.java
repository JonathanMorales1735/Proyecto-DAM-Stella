package com.example.stella.reciclerViewsAdapters;

public class taskElement {

    String name, description, type;
    int id;
    int notify;
    String time;
    int profileId;

    public taskElement(){}

    public taskElement(String name, String description, String type, int id, int notify, String time, int profileId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.id = id;
        this.notify = notify;
        this.time = time;
        this.profileId = profileId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int isNotify() {
        return notify;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setProfileId(int profileId){this.profileId = profileId;}

    public int getProfileId(){return this.profileId;}
}
