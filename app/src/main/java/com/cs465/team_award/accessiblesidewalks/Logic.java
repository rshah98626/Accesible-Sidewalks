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



    // --- Session management variables --- //

    //Obstacles arraylist
    private ArrayList<Obstacle> obstacles;
    //Ref of the current obstcle
    private Obstacle currentObstacle;
    //Boolean to know if is currently creating a new obstacle
    private boolean adding;


    // --- Location management variables --- //

    private Location myLoc;
    // Last locations: To temporary calculate the speed


    //FOR THE Shake
    private Synchronizer sync;



    private Logic(){
        myLoc = new Location(" ");

        //Initialize the obstacle list
        obstacles = new ArrayList<Obstacle>();


        //Test obstacles
        String testDescription = "Lorem ipsum dolor sit amet consectetur adipiscing elit per commodo ullamcorper, fringilla luctus gravida at viverra vivamus aenean nulla condimentum pellentesque vestibulum, ridiculus natoque netus aliquet ad praesent arcu bibendum faucibus. ";
        obstacles.add(new Obstacle(new LatLng(40.110404, -88.231220),0, testDescription));
        obstacles.add(new Obstacle(new LatLng(40.112685, -88.222637),1, testDescription));
        obstacles.add(new Obstacle(new LatLng(40.116443, -88.226773),0, testDescription));


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
    public void finishAdding(){
        Log.d(TAG, "A NEW OBSTACLE WAS CREATED: Type = "+currentObstacle.getType()+" Description"+ currentObstacle.getDescription());
        if(currentObstacle.getLoc().latitude != 0 && currentObstacle.getLoc().longitude !=0){
            // TODO: feedback If the obstacle is added correctl

            obstacles.add(currentObstacle);
        }else{
            // TODO: feedback If the obstacle is not added
        }

        adding = false;

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
}
