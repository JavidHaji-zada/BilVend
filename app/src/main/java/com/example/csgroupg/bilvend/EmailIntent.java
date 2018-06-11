package com.example.csgroupg.bilvend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


/**
 * EmailIntent Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class EmailIntent extends AppCompatActivity {

    //parameters
    private TextView to;
    private EditText subject;
    private EditText message;

    /**
     * This method starts the activity
     * @param savedInstanceState the Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_intent);
        to = findViewById(R.id.to);
        to.setText(getIntent().getStringExtra("Recipient"));
        subject = findViewById(R.id.subject);
        message = findViewById(R.id.message);
        CardView send = findViewById(R.id.sendCard);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
                startActivity(new Intent(getApplicationContext(), MainPage.class));
                finish();
            }
        });
    }

    /**
     * This method send the email to the recipient
     */
    public void sendMail()
    {
        String subjectText = subject.getText().toString();
        String messageText = message.getText().toString();
        String recipients[] = {to.getText().toString()};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subjectText);
        intent.putExtra(Intent.EXTRA_TEXT, messageText);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose an email client"));
    }
}
