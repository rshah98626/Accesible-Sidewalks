package com.cs465.team_award.accessiblesidewalks;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

public class LowVisStreets {
    // Green St past Lincoln
    static LatLng a = new LatLng(40.110629, -88.219313);
    static LatLng b = new LatLng(40.110629, -88.214539);
    // Springfield Goodwin to Gregory
    static LatLng c = new LatLng(40.112787, -88.223991);
    static LatLng d = new LatLng(40.112803, -88.220665);
    // 6th Pennsylvania to Gregory
    static LatLng e = new LatLng(40.104194, -88.230316);
    static LatLng f = new LatLng(40.100566, -88.230209);
    // Florida Goodwin to Orchard
    static LatLng g = new LatLng(40.098181, -88.223773);
    static LatLng h = new LatLng(40.098271, -88.214310);
    // Oak John to Armory
    static LatLng i = new LatLng(40.109017, -88.241617);
    static LatLng j = new LatLng(40.105390, -88.241553);
    // Healey 3rd to 4th
    static LatLng k = new LatLng(40.111457, -88.235392);
    static LatLng l = new LatLng(40.111466, -88.233566);
    // Sidewalk behind Busey/Evans
    static LatLng m = new LatLng(40.105362, -88.223784);
    static LatLng n = new LatLng(40.105363, -88.221447);
    // Sidewalk to LAR
    static LatLng o = new LatLng(40.104932, -88.220822);
    // Sidewalk in Illini Grove
    static LatLng p = new LatLng(40.102309, -88.221005);
    static LatLng q = new LatLng(40.100732, -88.220982);
    // Busey Pennsylvania to Springfield
    static LatLng r = new LatLng(40.100720, -88.217580);
    static LatLng s = new LatLng(40.112751, -88.217757);

    static ArrayList<LatLng> pts = new ArrayList<>(
            Arrays.asList(a,b,c,d,e,f,g,h,i,j,k,l,m,n,n,o,p,q,r,s)
    );
}
