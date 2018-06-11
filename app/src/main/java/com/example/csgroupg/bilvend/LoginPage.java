package com.example.csgroupg.bilvend;

/* This page is used to publish an advertisement
 * @author Verdiyev Zulfugar, Javid Haji-zada
 * @version 10.03.2018
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * LoginPage Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class LoginPage extends AppCompatActivity implements ResetPasswordDialog.EditDialogListener {

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // variables
    private static final String PREFS_NAME = "PrefsFile";
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private DatabaseReference mDatabaseReference;
    private User mUser;
    private CheckBox mCheckBox;
    private SharedPreferences mPrefs;
    private TextView resetPassword;
    private String emailForReset;

    /**
     * This method starts the activity
     * @param savedInstanceState the instance of Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        mAuth = FirebaseAuth.getInstance();
        CardView login = findViewById(R.id.sendCard);
        TextView signUp = findViewById(R.id.signup);
        email = findViewById(R.id.Email);
        TextView noRegistration = findViewById(R.id.noRegistration);
        password = findViewById(R.id.password);
        mCheckBox = findViewById(R.id.rememberMe);

        dialog = new ProgressDialog(LoginPage.this);
        dialog.setMessage("Logging in...Please wait...");

        mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        getPreferencesData();
        resetPassword = findViewById(R.id.resetPassword);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        noRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // anonymousEntrance
                startActivity(new Intent(getApplicationContext(), MainPage.class));
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterPage.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param view
             */
            @Override
            public void onClick(View view) {


                dialog.show();
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter e-mail address", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    dialog.cancel();
                    return;
                }
                if (password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    dialog.cancel();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if( !haveNetwork())
                        {
                            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    /**
                     *
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                Toast.makeText(getApplicationContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                            else {
                                if (mCheckBox.isChecked())
                                {
                                    SharedPreferences.Editor editor = mPrefs.edit();
                                    editor.putString("pref_email", email.getText().toString());
                                    editor.putString("pref_pass", password.getText().toString());
                                    editor.putBoolean("pref_check", true);
                                    editor.apply();
                                }
                                else
                                {
                                    mPrefs.edit().clear().apply();

                                }
                                final FirebaseUser user = mAuth.getCurrentUser();
                                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                    /**
                                     *
                                     * @param dataSnapshot
                                     */
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        assert user != null;
                                        mUser = new User(dataSnapshot.child("Name").getValue(String.class), dataSnapshot.child("Surname").getValue(String.class), dataSnapshot.child("Username").getValue(String.class),
                                                dataSnapshot.child("Email").getValue(String.class), dataSnapshot.child("Number").getValue(String.class), user.getUid());
                                        Intent intent = new Intent(getApplicationContext(), MainPage.class);
                                        intent.putExtra("User", mUser);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {
                            dialog.cancel();
                            Toast.makeText(LoginPage.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * This method checks whether the system is connected to the internet or not
     * @return truth value of connection
     */
    private boolean haveNetwork(){
        boolean have_WIFI = false;
        boolean have_MobileData = false;

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = manager.getAllNetworkInfo();

        for(NetworkInfo info:infos){
            if(info.getTypeName().equalsIgnoreCase("WIFI"))
                if(info.isConnected())
                    have_WIFI = true;
            if(info.getTypeName().equalsIgnoreCase("MOBILE"))
                if(info.isConnected())
                    have_MobileData = true;
        }
        return have_MobileData || have_WIFI;
    }

    /**
     *
     */
    public void getPreferencesData() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        if (sp.contains("pref_email"))
        {
            String e = sp.getString("pref_email", "not found");
            email.setText(e);
        }
        if (sp.contains("pref_pass"))
        {
            String p = sp.getString("pref_pass","not found");
            password.setText(p);
        }
        if (sp.contains("pref_check"))
        {
            Boolean b = sp.getBoolean("pref_check", false);
            mCheckBox.setChecked(b);
        }
    }

    /**
     * This method sends an email for process of resetting password
     */
    public void sendResetPassword()
    {
        if (!emailForReset.equals(""))
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailForReset)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Email sent to your email",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * This method opens a new dialog for resetting password
     */
    public void openDialog()
    {
        ResetPasswordDialog resetPasswordDialog = new ResetPasswordDialog();
        resetPasswordDialog.show(getSupportFragmentManager(), "reset pass dialog");
    }

    /**
     * This method calls sendResetPassword method
     * @param email the email
     */
    @Override
    public void applyTexts(String email) {
        emailForReset = email;
        sendResetPassword();
    }
}
