package com.example.csgroupg.bilvend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Favourites Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class Favourites extends AppCompatActivity {

    //parameters
    private ListView listview;
    private ArrayList<String> titles;
    private ArrayList<String> description;
    private ArrayList<String> links;
    private ArrayList<String> imageLinks;
    private ArrayList<String> finalIds;
    private User mUser;


    /**
     * This method starts the activity
     * @param savedInstanceState the instance of Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        //initializing
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        titles = new ArrayList<>();
        description = new ArrayList<>();
        links = new ArrayList<>();
        finalIds = new ArrayList<>();
        imageLinks = new ArrayList<>();
        mUser = getIntent().getParcelableExtra("User");
        assert user != null;
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Favourites");
        listview = findViewById(R.id.listView);

        reference.addValueEventListener(new ValueEventListener() {

            /**
             *
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    links.add(ds.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference dReference = FirebaseDatabase.getInstance().getReference().child("Advertisements");

        dReference.addValueEventListener(new ValueEventListener() {

            /**
             *
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < links.size(); i++) {
                    if( dataSnapshot.child(links.get(i)).getValue() != null) {
                        titles.add(dataSnapshot.child(links.get(i)).child("Title").getValue(String.class));
                        description.add(dataSnapshot.child(links.get(i)).child("Description").getValue(String.class));
                        imageLinks.add(dataSnapshot.child(links.get(i)).child("Image").getValue(String.class));
                        finalIds.add(links.get(i));
                    }
                }
                CustomAdaptor adaptor = new CustomAdaptor();
                listview.setAdapter(adaptor);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FloatingActionButton fabMain = findViewById(R.id.fabMain);
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                intent.putExtra("User", mUser);
                startActivity(intent);

            }
        });


    }

    /**
     * Favourites Class
     * @author Bilvend
     * @version 11.05.2018
     */
    class CustomAdaptor extends BaseAdapter {

        @Override
        public int getCount() {

            return titles.size();
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        /**
         * This method displays the view for the user
         * @param position the position
         * @param convertView the view
         * @param parent the parent
         * @return view
         */
        @SuppressLint("DefaultLocale")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.customlayout, null);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AdPage.class);
                    intent.putExtra("ID", finalIds.get(position) +"");
                    intent.putExtra("User", mUser);
                    startActivity(intent);
                }
            });

            TextView mTextView = view.findViewById(R.id.adDescription);
            TextView mTextView2 = view.findViewById(R.id.adTitle);

            mTextView.setText(description.get(position));
            mTextView2.setText(titles.get(position));

            mTextView2.setTextColor(getResources().getColor(android.R.color.holo_red_light));



            ImageView imageView = view.findViewById(R.id.customPageImage);
            Picasso.get().load(imageLinks.get(position)).fit().into(imageView);
            return view;

        }
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainPage.class);
        intent.putExtra("User", mUser);
        startActivity(intent);
    }




}