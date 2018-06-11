package com.example.csgroupg.bilvend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * My profile activity that shows user info and enables user to edit it
 * @author BilVend
 * @version 11.05.2018
 */
public class MyProfileActivity extends AppCompatActivity implements EditProfileDialog.EditDialogListener {

    private final int REQUEST_CAMERA = 1;
    private final int SELECT_FILE = 0;
    private final int STORAGE_PERMISSION_CODE = 1;

    private User mUser;
    private Uri mUri;
    private ImageView profileImageView;
    private EditText email;
    private EditText username;
    private EditText userId;
    private EditText name;
    private EditText surname;

    /**
     * This method starts the activity
     * @param savedInstanceState the instance of Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Intent intent = getIntent();
        mUser = intent.getParcelableExtra("User");
        FloatingActionButton edit = findViewById(R.id.edit_user);
        username =  findViewById(R.id.uaername_user);
        email =  findViewById(R.id.email_user);
        userId = findViewById(R.id.id_user);
        profileImageView =  findViewById(R.id.imageUser);
        FloatingActionButton camera = findViewById(R.id.profile_camera);
        name = findViewById(R.id.name_User);
        surname = findViewById(R.id.user_surname);
        setDataToView(mUser);
        setProfilePic();

        FloatingActionButton fabMain = findViewById(R.id.fabMain);
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                intent.putExtra("User", mUser);
                startActivity(intent);

            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(MyProfileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(MyProfileActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    selectImage();
                } else {
                    requestStoragePermission();
                }
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    public void setPhoto(final User user) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("Photos/" + user.getUsername() + ".jpg");
        mStorageRef.putFile(mUri);
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

                    Bitmap rotatedBitmap = null;
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
                    profileImageView.setImageBitmap(rotatedBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if ( requestCode == SELECT_FILE)
            {
                mUri = data.getData();
                saveProfilePic();
                profileImageView.setImageURI(mUri);
                setPhoto(mUser);
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
    @SuppressLint("SetTextI18n")
    private void setDataToView(User user) {
        email.setText("User Email: " + user.getEmail());
        username.setText("Username: " + user.getUsername());
        userId.setText("Number: " + user.getNumber());
        name.setText("Name: " + user.getName());
        surname.setText("Surname: " + user.getSurname());
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MyProfileActivity.this,
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
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void openDialog()
    {
        EditProfileDialog editProfileDialog = new EditProfileDialog();
        editProfileDialog.show(getSupportFragmentManager(), "edit dialog");
    }

    @Override
    public void applyTexts(String name, String surname, String number) {
        DatabaseReference mReference;

        mReference =  FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Name");
        if ( !name.equals(""))
            mReference.setValue(name);
        mReference =  FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Surname");
        if ( !surname.equals(""))
            mReference.setValue(surname);
        mReference =  FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Number");
        if ( !number.equals(""))
            mReference.setValue(number);
    }
    private void saveProfilePic()
    {
        final StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("Photos/" + mUser.getUsername() + ".jpg");
        mStorage.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setProfilePic() {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("Photos/" + mUser.getUsername()+".jpg");
        mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if ( uri != null)
                    Picasso.get().load(uri).resize(profileImageView.getWidth(), profileImageView.getHeight()).into(profileImageView);
            }
        });
    }

}