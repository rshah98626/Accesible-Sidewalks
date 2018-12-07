package com.cs465.team_award.accessiblesidewalks;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Logic extends Thread{
    //Reference of my logic
    private static Logic ref=null;
    //Debugging string
    private static String TAG = "DEBUGGING";



    // --- Obstacles management variables --- //

    //Obstacles arraylist
    private ArrayList<Obstacle> obstacles;
    //Ref of the current obstcle
    private Obstacle currentObstacle;
    //Boolean to know if is currently creating a new obstacle
    private boolean adding;

    // --- Curbs management variables --- //
    private ArrayList<Curb> curbs;


    // --- Location management variables --- //

    private Location myLoc;
    // Last locations: To temporary calculate the speed


    //FOR THE Shake
    private Synchronizer sync;

    private boolean openedFirst;

    private Logic(){
        openedFirst = true;
        myLoc = new Location(" ");

        //Initialize the list
        obstacles = new ArrayList<Obstacle>();
        curbs = new ArrayList<Curb>();

        //Add some obstacles
        // Sixth and Green
        obstacles.add(new Obstacle(new LatLng(40.110242,-88.230030),0, "Minimal issue, ramp provided to the street to bypass construction"));
        // Outside Cravings
        obstacles.add(new Obstacle(new LatLng(40.111803,-88.229126),0, "Rough asphalt ramp leading to the street to bypass construction"));
        // John St
        obstacles.add(new Obstacle(new LatLng(40.109223,-88.234347),0, "Sidewalk is blocked for half the block, use south sidewalk instead"));
        // Near FLB
        obstacles.add(new Obstacle(new LatLng(40.106102,-88.225156),0, "Sidewalk closed, use south sidewalk instead"));
        // Lincoln and Oregon
        obstacles.add(new Obstacle(new LatLng(40.107075,-88.219435),1, "Street in bad condition, lots of potholes when crossing Oregon at the corner"));
        // Main and Harvey
        obstacles.add(new Obstacle(new LatLng(40.114183, -88.222558),1,"Entire sidewalk segment missing"));
        // Clark and Gregory
        obstacles.add(new Obstacle(new LatLng(40.115602, -88.220443),1,"Cobblestone sidewalk, very uneven"));
        // Gregory, between Springfield and Western
        obstacles.add(new Obstacle(new LatLng(40.112654, -88.220864),1,"Cobblestone sidewalk, very uneven"));
        // Sixth and Peabody
        obstacles.add(new Obstacle(new LatLng(40.101251, -88.233687),0,"Sidewalk blocked, use east side of Sixth Street"));

        //Add some missing curbs
        // Lincoln and Stoughton
        curbs.add(new Curb(new LatLng(40.113600, -88.219307),1));
        // Lincoln and Main
        curbs.add(new Curb(new LatLng(40.114525, -88.219302),1));
        curbs.add(new Curb(new LatLng(40.114525, -88.219302),3));
        // Lincoln and Clark
        curbs.add(new Curb(new LatLng(40.115463, -88.219300),1));
        curbs.add(new Curb(new LatLng(40.115463, -88.219300),3));
        // Clark and Harvey
        curbs.add(new Curb(new LatLng(40.115404, -88.222305),3));
        // Gregory and Main
        curbs.add(new Curb(new LatLng(40.114473, -88.220687),2));
        curbs.add(new Curb(new LatLng(40.114473, -88.220687),3));
        // Gregory and Western
        curbs.add(new Curb(new LatLng(40.112261, -88.220691),0));
        // First and Peabody
        curbs.add(new Curb(new LatLng(40.101410, -88.238547),3));

        sync =  new Synchronizer();
    }

    public static Logic getInstance(){
        if(ref == null) ref = new Logic();
        return ref;
    }

    //Creates a new obstacle in the arrayList obstacles
    public void addNewObstacle(){
        currentObstacle = new Obstacle();
        adding = true;
    }

    //Close the current session
    public int finishAdding(){
        int state = 0;
        if(currentObstacle.getLoc().latitude == 0 && currentObstacle.getLoc().longitude == 0){
            adding = false;
            state = -1;
        }else{
            obstacles.add(currentObstacle);
            adding = false;
            state = 1;
        }
        return state;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public boolean isAdding() {
        return adding;
    }

    public Obstacle getCurrentObstacle() {
        return currentObstacle;
    }

    public Synchronizer getSync() {
        return sync;
    }

    public ArrayList<Curb> getCurbs() {
        return curbs;
    }

    public boolean getOpenFirst(){
        return openedFirst;
    }

    public boolean setOpenFirst(boolean b){
        openedFirst = b;
        return openedFirst;
    }
}
