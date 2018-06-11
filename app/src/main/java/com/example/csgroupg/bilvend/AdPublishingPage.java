package com.example.csgroupg.bilvend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * AdPublishingPage Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class AdPublishingPage extends AppCompatActivity{

    //parameters
    private final String[] CATEGORIES = {"Categories", "Books", "Notes","Household","Electronic devices", "Roommate","Others"};
    private final int REQUEST_CAMERA = 1;
    private final int SELECT_FILE = 0;
    private final int STORAGE_PERMISSION_CODE = 1;

    private FloatingActionButton camera;
    private ImageView imageOfItem;
    private EditText nameOfItem;
    private EditText priceOfItem;
    private EditText description;
    private View mDividerMode;
    private ArrayList<EditText> list;
    private Item mAdvertisement;
    private Spinner spinner;
    private User mUser;
    private int  ID;
    private Uri mUri;

    /**
     * This method starts the activity
     * @param savedInstanceState the instance of Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_publishing_page);
        Toolbar toolbar = findViewById(R.id.toolbar);

        Intent intent = getIntent();
        mUser =  intent.getParcelableExtra("User");


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Advertisements");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ID = Integer.parseInt(String.valueOf(dataSnapshot.child("Total number of advertisements").getValue(Long.class))) + 1;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });


        ImageButton publish = findViewById(R.id.finishPublish);
        camera = findViewById(R.id.camera);
        imageOfItem = findViewById(R.id.advertisementPublishingPhoto);
        nameOfItem = findViewById(R.id.adDescription);
        priceOfItem = findViewById(R.id.adTitle);
        description = findViewById(R.id.description);
        mDividerMode = findViewById(R.id.divider);
        list = new ArrayList<>();
        list.add(nameOfItem);
        list.add(priceOfItem);
        list.add(description);
        setSupportActionBar(toolbar);

        // configure ArrayAdapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);

        // set item selection to spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                {
                    imageOfItem.setVisibility(View.VISIBLE);
                    camera.setVisibility(View.VISIBLE);
                    nameOfItem.setVisibility(View.VISIBLE);
                    priceOfItem.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    mDividerMode.setVisibility(View.VISIBLE);
                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ((ContextCompat.checkSelfPermission(AdPublishingPage.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(AdPublishingPage.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                                selectImage();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    });
                }
                if (position == 0)
                {
                    imageOfItem.setVisibility(View.INVISIBLE);
                    camera.setVisibility(View.INVISIBLE);
                    nameOfItem.setVisibility(View.INVISIBLE);
                    priceOfItem.setVisibility(View.INVISIBLE);
                    description.setVisibility(View.INVISIBLE);
                    mDividerMode.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(),"asdasd",Toast.LENGTH_SHORT).show();
            }
        });

        // click event listener to finish publishing
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFilled(list))
                {
                    publishAdvertisement();
                    Intent intent1 = new Intent(getApplicationContext(),MainPage.class);
                    intent1.putExtra("User", mUser);
                    startActivity(intent1);
                }
            }
        });
    }
    public boolean checkFilled(ArrayList<EditText> list)
    {
        for(EditText e : list)
        {
            if(e.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please fully enter required information", Toast.LENGTH_SHORT).show();
                e.requestFocus();
                return false;
            }
            else if ( e.getText().toString().length() > 300)
            {
                Toast.makeText(this, "Maximum 300 charachters", Toast.LENGTH_SHORT).show();
                e.requestFocus();
                return false;
            }
        }
        if ( mUri == null)
        {
            Toast.makeText(this, "Please load an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( Long.parseLong(list.get(1).getText().toString()) > Integer.MAX_VALUE){
            Toast.makeText(this, "Please enter right value for the price", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public void publishAdvertisement() {

            mAdvertisement = new Item(nameOfItem.getText().toString(), mUser.getName() + " " + mUser.getSurname(), ID, spinner.getSelectedItemPosition(), description.getText().toString(), getCurrentDate(),
                Integer.parseInt(priceOfItem.getText().toString()), mUri.toString());
            final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("Advertisements/" + mAdvertisement.getID() + "/" + mAdvertisement.getTitle() + ".jpg");
            mStorageRef.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mAdvertisement.setAdvertisementImage(uri);
                        mUser.publishAdvertisement(mAdvertisement, ID);
                    }
                });

                 }
        });
    }

    public String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = simpleDateFormat.format(calendar.getTime());
        return date;
    }
    public void selectImage()
    {
        final String[] items = {"Camera", "Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add image of the item");
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
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select picture"), SELECT_FILE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), mUri);
                    ExifInterface ei = new ExifInterface(getRealPathFromURI(mUri));
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    Bitmap rotatedBitmap ;
                    switch (orientation) {
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
                    imageOfItem.setImageBitmap(rotatedBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE) {
                mUri = data.getData();
                imageOfItem.setImageURI(mUri);
                setPhoto(mUri);
            }
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
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
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AdPublishingPage.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void setPhoto(Uri uri) {
        Picasso.get().load(uri).resize(imageOfItem.getWidth(), imageOfItem.getHeight()).into(imageOfItem);
    }
}