package com.cs465.team_award.accessiblesidewalks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddObstacleActivity extends AppCompatActivity {

    private ImageButton pithole, construction, other;
    private EditText description;
    private Button chooseLocation;
    private ImageButton close_button;

    //Data
    private String descriptionText;
    private int type;

    //ref of logic
    private Logic logic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_obstacle);

        logic = Logic.getInstance();

        initViews();
        type = 2;
        toggleType();

    }

    public void initViews(){
        this.description = findViewById(R.id.description_field);

        this.chooseLocation = findViewById(R.id.choose_location);
        this.chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddObstacleActivity.this, MapsActivity.class));


                //Add the information to the temporal obstacle in the logic
                if(!getDescription().isEmpty()) {
                    logic.getCurrentObstacle().setDescription(getDescription());
                }
                logic.getCurrentObstacle().setType(type);
            }
        });


        this.close_button = findViewById(R.id.close_button);
        this.close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddObstacleActivity.this, MapsActivity.class));

                logic.finishAdding();
            }
        });


        //Type buttons
        this.pithole = findViewById(R.id.pithole_opt);
        this.construction = findViewById(R.id.contr_opt);
        this.other = findViewById(R.id.other_opt);

        this.construction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 0;
                toggleType();
            }
        });

        this.pithole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            type = 1;
            toggleType();

            }
        });

        this.other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 2;
                toggleType();
            }
        });
    }

    public void toggleType(){

        switch(type){
            case -1:
                this.construction.setSelected(false);
                this.pithole.setSelected(false);
                this.other.setSelected(false);
                break;
            case 0:
                this.construction.setSelected(true);
                this.pithole.setSelected(false);
                this.other.setSelected(false);
                break;
            case 1:
                this.construction.setSelected(false);
                this.pithole.setSelected(true);
                this.other.setSelected(false);
                break;

            case 2:
                this.construction.setSelected(false);
                this.pithole.setSelected(false);
                this.other.setSelected(true);
                break;

        }
    }

    public String getDescription(){
        return this.description.getText().toString();
    }
}
