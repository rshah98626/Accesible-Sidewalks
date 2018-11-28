package com.cs465.team_award.accessiblesidewalks;

import com.google.android.gms.maps.model.LatLng;

public class Curb {
    private LatLng position;
    //0: top_right, 1: top_left, 2: bottom_right, 3:bottom_left
    private int orientation;

    public Curb(LatLng position, int orientation){
        this.orientation = orientation;
        this.position = position;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
