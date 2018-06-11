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
 * MyPosts Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class MyPosts extends AppCompatActivity {

    //properties
    private ListView listview;
    private ArrayList<String> titles;
    private ArrayList<String> description;
    private ArrayList<String> links;
    private ArrayList<String> imageLinks;
    private User mUser;
    private boolean editable;

    /**
     * This method starts the activity
     * @param savedInstanceState the instance of Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        //initializing
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        titles = new ArrayList<>();
        description = new ArrayList<>();
        links = new ArrayList<>();
        imageLinks = new ArrayList<>();
        editable = false;
        mUser = getIntent().getParcelableExtra("User");
        assert user != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Published advertisements:");
        listview = findViewById(R.id.listView);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                    links.add(ds.getValue(String.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Connection timed out", Toast.LENGTH_SHORT).show();
            }
        });
        FloatingActionButton floatingActionButton = findViewById(R.id.myPostsEdit);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editable) {
                    Toast.makeText(getApplicationContext(), "Editing feature disabled", Toast.LENGTH_SHORT).show();
                    editable = false;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Editing feature enabled, choose an advertisement to edit or delete", Toast.LENGTH_SHORT).show();
                    editable = true;
                }
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



        DatabaseReference dReference = FirebaseDatabase.getInstance().getReference().child("Advertisements");

        dReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < links.size(); i++) {
                    if( dataSnapshot.child(links.get(i)).getValue() != null) {
                        titles.add(dataSnapshot.child(links.get(i)).child("Title").getValue(String.class));
                        description.add(dataSnapshot.child(links.get(i)).child("Description").getValue(String.class));
                        imageLinks.add(dataSnapshot.child(links.get(i)).child("Image").getValue(String.class));
                    }
                }
                CustomAdaptor adaptor = new CustomAdaptor();
                listview.setAdapter(adaptor);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Connection timed out", Toast.LENGTH_SHORT).show();
            }
        });

    }


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

        @SuppressLint("DefaultLocale")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.customlayout, null);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (editable)
                    {
                        intent = new Intent(getApplicationContext(), MyPostsAdPage.class);
                        intent.putExtra("Title", titles.get(position));
                    }
                    else
                    {
                        intent = new Intent(getApplicationContext(), AdPage.class);
                    }
                    intent.putExtra("ID", links.get(position) +"");
                    intent.putExtra("User",mUser);
                    startActivity(intent);
                }
            });

            TextView mTextView = view.findViewById(R.id.adDescription);
            TextView mTextView2 = view.findViewById(R.id.adTitle);

            mTextView.setText(description.get(position));
            mTextView2.setText(titles.get(position));

            ImageView imageView = view.findViewById(R.id.customPageImage);
            Picasso.get().load(imageLinks.get(position)).fit().into(imageView);
            return view;

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainPage.class);
        intent.putExtra("User", mUser);
        startActivity(intent);
    }
}