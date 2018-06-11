package com.example.csgroupg.bilvend;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Categories Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class Categories extends AppCompatActivity {

    //parameters
    private CheckedTextView ctv_books;
    private CheckedTextView ctv_notes;
    private CheckedTextView ctv_household;
    private CheckedTextView ctv_electronic_device;
    private CheckedTextView ctv_roommate;
    private CheckedTextView ctv_others;
    private ImageView send;
    private User user;

    /**
     * This method starts the activity
     * @param savedInstanceState the instance of Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        send = findViewById(R.id.sendMain);
        user = getIntent().getParcelableExtra("User");

        ctv_books = findViewById(R.id.ctv_books);
        ctv_books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ctv_books.isChecked()) {
                    ctv_books.setChecked(false);

                }
                else{
                    ctv_books.setChecked(true);
                    ctv_others.setChecked(false);
                    ctv_roommate.setChecked(false);
                    ctv_electronic_device.setChecked(false);
                    ctv_notes.setChecked(false);
                    ctv_household.setChecked(false);
                }
            }
        });


        ctv_notes = findViewById(R.id.ctv_notes);
        ctv_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ctv_notes.isChecked()  ) {
                    ctv_notes.setChecked(false);

                }
                else{
                    ctv_books.setChecked(false);
                    ctv_others.setChecked(false);
                    ctv_roommate.setChecked(false);
                    ctv_electronic_device.setChecked(false);
                    ctv_notes.setChecked(true);
                    ctv_household.setChecked(false);

                }
            }
        });

        ctv_household = findViewById(R.id.ctv_household);
        ctv_household.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ctv_household.isChecked()  ) {
                    ctv_household.setChecked(false);

                }
                else {
                    ctv_books.setChecked(false);
                    ctv_others.setChecked(false);
                    ctv_roommate.setChecked(false);
                    ctv_electronic_device.setChecked(false);
                    ctv_notes.setChecked(false);
                    ctv_household.setChecked(true);

                }
            }
        });

        ctv_electronic_device = findViewById(R.id.ctv_electronic_device);
        ctv_electronic_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ctv_electronic_device.isChecked()  ) {
                    ctv_electronic_device.setChecked(false);

                }
                else  {
                    ctv_books.setChecked(false);
                    ctv_others.setChecked(false);
                    ctv_roommate.setChecked(false);
                    ctv_electronic_device.setChecked(true);
                    ctv_notes.setChecked(false);
                    ctv_household.setChecked(false);

                }
            }
        });

        ctv_roommate = findViewById(R.id.ctv_roommate);
        ctv_roommate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ctv_roommate.isChecked()  ) {
                    ctv_roommate.setChecked(false);

                }
                else  {
                    ctv_books.setChecked(false);
                    ctv_others.setChecked(false);
                    ctv_roommate.setChecked(true);
                    ctv_electronic_device.setChecked(false);
                    ctv_notes.setChecked(false);
                    ctv_household.setChecked(false);

                }
            }
        });

        ctv_others = findViewById(R.id.ctv_others);
        ctv_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ctv_others.isChecked()) {
                    ctv_others.setChecked(false);

                }
                else {
                    ctv_books.setChecked(false);
                    ctv_others.setChecked(true);
                    ctv_roommate.setChecked(false);
                    ctv_electronic_device.setChecked(false);
                    ctv_notes.setChecked(false);
                    ctv_household.setChecked(false);

                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String category = "Category";


                if(ctv_books.isChecked())category = "Books";
                else if(ctv_household.isChecked())category = "Household";
                else if(ctv_notes.isChecked())category = "Notes";
                else if(ctv_electronic_device.isChecked())category = "Electronic devices";
                else if(ctv_roommate.isChecked())category = "Roommate";
                else if(ctv_others.isChecked())category = "Others";


                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                intent.putExtra("Category",category);
                intent.putExtra("User", user);
                startActivity(intent);
                finish();

            }
        });

    }

}
