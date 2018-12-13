package edu.upc.citm.android.speakerfeedback;

public class Room {
    private String name;
    private String Speakerid;
    private Boolean open;
    private String password;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpeakerid() {
        return Speakerid;
    }

    public void setSpeakerid(String speakerid) {
        Speakerid = speakerid;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}




