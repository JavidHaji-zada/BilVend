package com.example.csgroupg.bilvend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * MysPostsAdPage Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class MyPostsAdPage extends AppCompatActivity implements View.OnClickListener, EditAdvertisement.EditDialogListener{

    //parameters
    private final int REQUEST_CAMERA = 1;
    private final int SELECT_FILE = 0;
    private Boolean isFabOpen = false;
    private FloatingActionButton fabOpen, fabEdit, fabDelete, fabMain, camera;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private TextView editAdPageTitle, editAdPagePrice, editAdPageDescription;
    private User mUser;
    private String neededId;
    private Uri mUri;
    private ImageView mImageView;
    private String oldTitle;

    /**
     * This method starts the activity
     * @param savedInstanceState the instance of Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts_ad_page);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mUser = getIntent().getParcelableExtra("User");
        neededId = getIntent().getStringExtra("ID");
        oldTitle = getIntent().getStringExtra("Title");

        fabOpen = findViewById(R.id.openingFab);
        fabEdit = findViewById(R.id.edit);
        fabDelete = findViewById(R.id.delete);
        fabMain = findViewById(R.id.fabMain);
        camera = findViewById(R.id.changePic);
        mImageView = findViewById(R.id.editAdPageImage);
        editAdPageTitle = findViewById(R.id.editAdPageTitle);
        editAdPageDescription = findViewById(R.id.editAdPageDescription);
        editAdPagePrice = findViewById(R.id.editAdPagePrice);

        setAdvertisementDetails();

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fabOpen.setOnClickListener(this);
        fabEdit.setOnClickListener(this);
        fabDelete.setOnClickListener(this);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    /**
     * This method sets the details of the advertisement
     */
    private void setAdvertisementDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Advertisements").child(neededId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    editAdPageTitle.setText("Title: " + dataSnapshot.child("Title").getValue(String.class));
                    editAdPageDescription.setText("Description: " + dataSnapshot.child("Description").getValue(String.class));
                    editAdPagePrice.setText("Price: " + dataSnapshot.child("Price").getValue().toString());
                    Picasso.get().load((dataSnapshot.child("Image").getValue(String.class))).fit().into(mImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.openingFab:
                animateFAB();
                break;
            case R.id.edit:
                openDialog();
                break;
            case R.id.delete:
                buildAlert();
                break;
            case R.id.fabMain:
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                intent.putExtra("User", mUser);
                startActivity(intent);
                break;
            case R.id.changePic:
                selectImage();
                break;
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            fabOpen.startAnimation(rotate_backward);
            fabEdit.startAnimation(fab_close);
            fabDelete.startAnimation(fab_close);
            fabEdit.setClickable(false);
            fabDelete.setClickable(false);
            isFabOpen = false;
        }
        else
            {
            fabOpen.startAnimation(rotate_forward);
            fabEdit.startAnimation(fab_open);
            fabDelete.startAnimation(fab_open);
            fabEdit.setClickable(true);
            fabDelete.setClickable(true);
            isFabOpen = true;
        }
    }
    public void openDialog()
    {
        EditAdvertisement editAdvertisement = new EditAdvertisement();
        editAdvertisement.show(getSupportFragmentManager(), "edit dialog");
    }

    @Override
    public void applyTexts(String title, String description, String price) {
        mUser.editAdvertisement(title,description,price, neededId, mUri);
    }
    public void buildAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPostsAdPage.this);
        builder.setCancelable(true);
        builder.setTitle("Title");
        builder.setMessage("Are you sure you want to delete this advertisement?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUser.removeAdvertisement(neededId,editAdPageTitle.getText().toString());
                        Intent intent = new Intent(MyPostsAdPage.this, MainPage.class);
                        intent.putExtra("User", mUser);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void selectImage()
    {
        final String[] items = {"Camera", "Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select profile pic");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    mUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
                else if(which == 1)
                {
                    Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                }
                else if(which == 2)
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == Activity.RESULT_OK )
        {
            if ( requestCode == REQUEST_CAMERA)
            {
                try {
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), mUri);
                    ExifInterface ei = new ExifInterface(getRealPathFromURI(mUri));
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap;
                    switch(orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(thumbnail, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(thumbnail, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(thumbnail, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = thumbnail;
                    }
                    mImageView.setImageBitmap(rotatedBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if ( requestCode == SELECT_FILE)
            {
                mUri = data.getData();
                mImageView.setImageURI(mUri);
            }
            mUser.editAdvertisement(editAdPageTitle.getText().toString().substring(7),editAdPageDescription.getText().toString().substring(12),editAdPagePrice.getText().toString().substring(8), neededId, mUri);
        }
    }
    public  Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}