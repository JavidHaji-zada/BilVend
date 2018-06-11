package com.example.csgroupg.bilvend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static android.provider.ContactsContract.Intents.Insert.ACTION;


public class AdPage extends AppCompatActivity {

    private TextView mDateView;
    private TextView mPublisherView;
    private TextView mPriceView;
    private TextView mDescriptionView;
    private TextView contact;
    private TextView ID;
    private String price;
    private String publisher;
    private TextView mNameView;
    private String title;
    private String description;
    private ImageView imageView;
    private String neededID;
    private String id;
    private DatabaseReference mReference;
    private String urii;
    private User mUser;
    private Boolean isFavourite = false;
    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        neededID = getIntent().getStringExtra("ID");
        setContentView(R.layout.activity_ad_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mUser = getIntent().getParcelableExtra("User");

        imageView = findViewById(R.id.adPageImage);
        mDateView =  findViewById(R.id.dateView);
        mPriceView = findViewById(R.id.adPagePrice);
        mDescriptionView = findViewById(R.id.adPageDescription);
        mNameView =  findViewById(R.id.adPageTitle);
        mPublisherView = findViewById(R.id.adPagePublisher);
        ID = findViewById(R.id.adPageID);
        contact = findViewById(R.id.contact);


        FloatingActionButton fabMain = findViewById(R.id.fabMain);
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                intent.putExtra("User", mUser);
                startActivity(intent);

            }
        });



        FloatingActionButton fab =  findViewById(R.id.sendEmail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                mReference = FirebaseDatabase.getInstance().getReference().child("Advertisements").child(neededID).child("Publisher's mail");
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mail = dataSnapshot.getValue(String.class);
                        String[] to = { mail, ""};
                        Intent intent;
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL , to);
                        intent.putExtra(Intent.EXTRA_SUBJECT , "BILVEND Advertisement");
                        intent.putExtra(Intent.EXTRA_TEXT, "");
                        intent.setType("message/rfc822");
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Connection timed out", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        setAdvertisementDetails();

        mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Favourites");
        mReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Connection timed out", Toast.LENGTH_SHORT).show();

            }
        });
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    if(ds.getValue(String.class).equals(id + ""))
                    {
                        isFavourite = true;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Connection timed out", Toast.LENGTH_SHORT).show();
            }
        });
        FloatingActionButton fab2 =  findViewById(R.id.addfavorite);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReference = FirebaseDatabase.getInstance().getReference().child("Advertisements").child(neededID).child("Image");
                mReference.setValue(urii);
                if(isFavourite){
                    Toast.makeText(getApplicationContext(), "Advertisement removed from favorites", Toast.LENGTH_SHORT).show();
                    mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Favourites").child(id);
                    mReference.removeValue();
                }
                else {
                    Toast.makeText(getBaseContext(), "Added to Favourites" , Toast.LENGTH_SHORT).show();
                    mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Favourites").child(id);
                    mReference.setValue(id + "");
                }
            }
        });
    }
    private void setAdvertisementDetails()
    {
        mReference = FirebaseDatabase.getInstance().getReference().child("Advertisements").child(neededID);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // set title
                title = dataSnapshot.child("Title").getValue(String.class);
                mNameView.setText(title);


                // set description
                description = dataSnapshot.child("Description").getValue(String.class);
                mDescriptionView.setText("Description : " + description);
                mDescriptionView.setTextColor(getResources().getColor(android.R.color.white));

                // price
                price = dataSnapshot.child("Price").getValue(String.class);
                mPriceView.setText("Price : " + price + " ₺");
                mPriceView.setTextColor(getResources().getColor(android.R.color.white));
                //Publisher
                publisher = dataSnapshot.child("Publisher").getValue(String.class);
                mPublisherView.setText("Publisher : " + publisher);
                mPublisherView.setTextColor(getResources().getColor(android.R.color.white));

                // Date
                mDateView.setText("Upload Date: " + dataSnapshot.child("Upload date").getValue(String.class));
                mDateView.setTextColor(getResources().getColor(android.R.color.white));
                id = dataSnapshot.child("ID").getValue(String.class);

                ID.setText("ID: #" + id);
                ID.setTextColor(getResources().getColor(android.R.color.white));
                // email
                mail = dataSnapshot.child("Publisher's mail").getValue(String.class);
                // Contact details
                contact.setText("Number: " + dataSnapshot.child("Contact phone").getValue(String.class));
                contact.setTextColor(getResources().getColor(android.R.color.white));

                //Photo
                urii = dataSnapshot.child("Image").getValue(String.class);
                Picasso.get().load(urii).fit().into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder imageDialog = new AlertDialog.Builder(AdPage.this);
                        View imageLayout = getLayoutInflater().inflate(R.layout.image_dialog, null);

                        ImageView image = imageLayout.findViewById(R.id.imageView);
                        Picasso.get().load(urii).fit().into(image);

                        imageDialog.setView(imageLayout);
                        AlertDialog dialog = imageDialog.create();
                        dialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Connection timed out", Toast.LENGTH_SHORT).show();
            }
        });
    }
}