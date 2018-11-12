package com.cs465.team_award.accessiblesidewalks;


import com.google.android.gms.maps.model.LatLng;

public class Obstacle {
    //Geolocation of the obstacle
    private LatLng loc;
    // Type of the obstable. = 0: Construction, 1:Pithole, 2: Other
    private int type;
    //Description added by the user
    private String description;
    //Relevance score
    private int relevance;
    //Orientation: 0: the obstacle is under a street in the map, 1: the obstacle is over the street
    private int orientation;

    public Obstacle(LatLng loc, int type, String description, int orientation){
        this.loc = loc;
        this.type = type;
        this.description = description;
        this.orientation = orientation;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc(LatLng loc) {
        this.loc = loc;
    }

    public String getType() {
        String stringType = "";

        switch (type){
            case 0:
                stringType = "Construction";
                break;
            case 1:
                stringType = "Pithole";
                break;
            case 2:
                stringType = "Other";
                break;
        }

        return stringType;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRelevance() {
        return relevance;
    }

    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }
}
