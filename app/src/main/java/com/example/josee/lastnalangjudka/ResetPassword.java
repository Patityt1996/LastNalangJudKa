package com.example.josee.lastnalangjudka;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ResetPassword extends AppCompatActivity {

    private EditText emailEditTxt;
    private Button submittBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        submittBtn = (Button) findViewById(R.id.submittButton);
        emailEditTxt = (EditText) findViewById(R.id.emailEditText);

        submittBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userEmail = emailEditTxt.getText().toString();

                if(TextUtils.isEmpty(userEmail))
                {
                    Toast.makeText(ResetPassword.this, "Please enter your valid  email address", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                emailEditTxt.setText("");
                                Toast.makeText(ResetPassword.this, "Please check your email account to reset your password", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassword.this, MainActivity.class));
                            } else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPassword.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
