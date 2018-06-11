package com.example.csgroupg.bilvend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * RegisterPage Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class RegisterPage extends AppCompatActivity {

    //parameters
    private EditText name;
    private EditText surname;
    private EditText email;
    private EditText password;
    private  EditText repassword;
    private CardView register;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    //constructor
    public RegisterPage() {
    }

    /**
     * This method starts the activity
     * @param savedInstanceState instance of Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.re_enter_password);
        register = findViewById(R.id.sendCard);
        dialog = new ProgressDialog(RegisterPage.this);
        register.setOnClickListener(new View.OnClickListener() {

            /**
             * eThis method xecutes on main thread after user presses button
             * @param view the view
             */
            @Override
            public void onClick(View view) {
                dialog.show();
                createAccount();
            }
        });
    }

    /**
     * This method creates a new account
     */
    public void createAccount()
    {
        ArrayList<EditText> list = new ArrayList<>();
        list.add(name);
        list.add(surname);
        list.add(email);
        list.add(password);
        list.add(repassword);
        if (!checkFilled(list))
        {
            register.setEnabled(true);
            return;
        }
        mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    /**
                     * This method
                     * @param task the task
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            sendEmailVerification();
                            String current_user_id = mAuth.getUid();
                            assert current_user_id != null;
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                            Map<String, String> newPost = new HashMap<>();
                            newPost.put("Name", name.getText().toString());
                            newPost.put("Surname", surname.getText().toString());
                            newPost.put("Username", email.getText().toString().substring(0,email.getText().toString().indexOf('@')));
                            newPost.put("Number", "No number");
                            newPost.put("Email", email.getText().toString());
                            databaseReference.setValue(newPost);
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                        }
                        else
                        {
                            Toast.makeText(RegisterPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    /**
     * This method verifies the given email for registration
     */
    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if ( user != null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Verification email has been sent to " + email.getText().toString(), Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }

    }

    /**
     * This method checks the whether all tcriterias are satisfied or not
     * @param list the list
     * @return truth value of being registered
     */
    public boolean checkFilled( ArrayList<EditText> list)
    {
        for(EditText e : list)
        {
            if(e.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please fully enter required information", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                e.requestFocus();
                return false;
            }
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
        {
            Toast.makeText(getApplicationContext(), "Please enter a valid e-mail", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            email.requestFocus();
            return false;
        }
        if ( !email.getText().toString().substring(email.getText().toString().length() - 14, email.getText().toString().length()).equals("bilkent.edu.tr"))
        {
            Toast.makeText(getApplicationContext(), "Please enter your Bilkent e-mail address", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            email.requestFocus();
            return false;
        }
        mAuth.fetchProvidersForEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    /**
                     * This method
                     * @param task the task
                     */
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        boolean check = Objects.requireNonNull(task.getResult().getProviders()).isEmpty();
                        if (!check)
                        {
                            Toast.makeText(getApplicationContext(), "Already registered with this e-mail", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            email.requestFocus();
                        }
                    }
                });
        return true;
    }
}
