package com.cs465.team_award.accessiblesidewalks;
import android.util.Log;

import java.util.Observable;

public class Synchronizer extends Observable{

    public Synchronizer(){

        }

        public void updateChange(){
            setChanged();
            notifyObservers();

        }
}
