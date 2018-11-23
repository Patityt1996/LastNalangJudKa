package com.example.josee.lastnalangjudka;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button logInBtn;
    private TextInputEditText emailEditTxt, passwordEditTxt;
    private TextView forgotPasswordTxtView, registerTxtView;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private Boolean emailAddressChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logInBtn = (Button) findViewById(R.id.logInButton);
        emailEditTxt = findViewById(R.id.emailEditText);
        passwordEditTxt = findViewById(R.id.passwordEditText);
        forgotPasswordTxtView = (TextView) findViewById(R.id.forgotTextView);
        registerTxtView = (TextView) findViewById(R.id.registerTextView);

        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        registerTxtView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });


        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowingUserToLogIn();
            }//when user clicks login button, if user is already registered it directs to home layout
//            otherwise it sends an email for the user to verify
        });

        forgotPasswordTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ResetPassword.class));
            }
        });//when user clicks the reset password, it directs to reset password layout
    }

    private void allowingUserToLogIn()
    {
        String email = emailEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEditTxt.setError("Please enter a valid email address");
        }//a warning message that prompts the user to enter a valid email address
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mProgressDialog.setTitle("Logging in...");
            mProgressDialog.setMessage("Please wait");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();//loading dialog of the system when logging in
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
//                            sendUserToLogInActivity();
                        Toast.makeText(MainActivity.this, "You are logged in successfully", Toast.LENGTH_LONG);

                        verifyEmailAddress();//if the user has just registered, it sends a message to his/her email inorder for the user to verify its email account
                        mProgressDialog.dismiss();
                    }
                    else
                    {
                        String message = task.getException().toString();
                        Toast.makeText(MainActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();//this message will prompt to the user if something wrong happened
                    }
                }
            });
        }
    }

    private void sendUserToInnerAccountActivity()
    {
        Intent mainIntent = new Intent(MainActivity.this, Home.class);
        Toast.makeText(MainActivity.this, "You are logged in successfully", Toast.LENGTH_LONG);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();//if the user has successfully registered and verified the email address it directs to the inner(Home) acount
    }

    private void sendUserToRegisterActivity()
    {
        Intent registerIntent = new Intent(MainActivity.this, Registration.class);
        startActivity(registerIntent);
    }//directs to register layout

    private void verifyEmailAddress()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        emailAddressChecker = currentUser.isEmailVerified();

        if(emailAddressChecker)
        {
            sendUserToInnerAccountActivity();//if the user is done registering, it sends a message to his/her email address

        }
        else
        {
            Toast.makeText(this, "Please verify your account first...", Toast.LENGTH_SHORT).show();
            mAuth.signOut();//if the user is done registering and attempts to log in without verifying his/her email address
//            this message will prompt to the user
        }
    }
}
