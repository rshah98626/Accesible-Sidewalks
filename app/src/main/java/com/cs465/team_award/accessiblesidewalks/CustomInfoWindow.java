package com.cs465.team_award.accessiblesidewalks;

/**
 * Created by bella on 11/27/2018.
 */


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindow(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.fragment_obstacle_info, null);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUGGING", "ASDFGHJK");

            }
        });

        TextView description = view.findViewById(R.id.description);
        TextView type = view.findViewById(R.id.type);
        final Button myButton = view.findViewById(R.id.reportButton);


        type.setText(marker.getTitle());
        description.setText(marker.getSnippet());
        CharSequence text = "Report Received!";
        int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, text, duration);

        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myButton.setText("Thank you");
                // Code here executes on main thread after user presses button
            }
        });



        return view;
    }
}