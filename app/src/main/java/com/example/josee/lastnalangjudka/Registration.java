package com.example.josee.lastnalangjudka;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration extends AppCompatActivity {

    private EditText firstNameEditTxt, lastNameEditTxt, emailEditTxt, passwordEditTxt, contactNo, address;
    private EditText bdayEditTxt;
    private TextView displayHashPassword;
    private Button registerBtn, beerDayBtn;
    String Date;
    Calendar mCurrentDate;
    int day, month, year;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    final static int GALLERY_PICK = 1;
    private ProgressDialog mProgressDialog;
    private StorageReference UserProfileImageRef;
    String outputString;
    String AES = "AES";
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +      // at least 1 digit
                    "(?=.*[a-z])" +      //at least 1 lower case letter
                    "(?=.*[A-Z])" +      //at least 1 upper case letter
                    "(?=\\S+$)" +        //no white space
                    ".{8,}" +             //at least 8 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firstNameEditTxt = (EditText) findViewById(R.id.firstName);
        lastNameEditTxt = (EditText) findViewById(R.id.lastName);
        emailEditTxt = (EditText) findViewById(R.id.email);
        passwordEditTxt = (EditText) findViewById(R.id.password);
        contactNo = (EditText) findViewById(R.id.contactNumber);
        address = (EditText) findViewById(R.id.addressEditText);
        //displayHashPassword = findViewById(R.id.passwordTextView);
        bdayEditTxt = (EditText) findViewById(R.id.birthdayEditText);
        registerBtn = (Button) findViewById(R.id.registerButton);
        beerDayBtn = (Button) findViewById(R.id.bdayButton);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        mCurrentDate = Calendar.getInstance();

        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCurrentDate.get(Calendar.MONTH);
        year = mCurrentDate.get(Calendar.YEAR);

        month = month + 1;

        beerDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        bdayEditTxt.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                computeMD5Hash(passwordEditTxt.toString());
                createNewAccount();
            }
        });

    }

    private void createNewAccount() {
        final String fName = firstNameEditTxt.getText().toString();
        final String lName = lastNameEditTxt.getText().toString();
        final String email = emailEditTxt.getText().toString();
        final String password = passwordEditTxt.getText().toString();
        final String contactNumero = contactNo.getText().toString();
        final String lugar = address.getText().toString();
        final String beerDay = bdayEditTxt.getText().toString();

        if (TextUtils.isEmpty(fName)) {
            Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(lName)) {
            Toast.makeText(this, "Please enter last number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditTxt.setError("Please enter a valid email address");
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            passwordEditTxt.setError("Password must contain 1 upper letter and 1 number");
        } else if (TextUtils.isEmpty(contactNumero)) {
            Toast.makeText(this, "Please enter contact number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(lugar)) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(lugar)) {
            Toast.makeText(this, "Please enter birthday", Toast.LENGTH_SHORT).show();
        } else {
            mProgressDialog.setTitle("Creating new account...");
            mProgressDialog.setMessage("Please wait");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                                sendUserToLogInActivity();
//                                Toast.makeText(Registration.this, "Account Created Successfuly", Toast.LENGTH_SHORT).show();
                                String currentUserID = mAuth.getCurrentUser().getUid();
//                                mDatabase.child("Users").child(currentUserID);
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("firstName", fName);
                                userMap.put("lastName", lName);
                                userMap.put("email", email);
                                userMap.put("password", password);
                                userMap.put("contactNumber", contactNumero);
                                userMap.put("address", lugar);
                                userMap.put("profileImage", "");
                                userMap.put("birthDay", beerDay);

                                mDatabase.setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProgressDialog.dismiss();
                                        sendEmailVerificationMessage();
                                        mProgressDialog.dismiss();
                                    }
                                });

                              /*  sendEmailVerificationMessage();
                                mProgressDialog.dismiss();*/
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(Registration.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void sendUserToLogInActivity() {
        Intent logInIntent = new Intent(Registration.this, MainActivity.class);
        logInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logInIntent);
        finish();
    }

    private void sendEmailVerificationMessage() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registration.this, "Account Created Successfuly. We've sent you a mail. Please check and verify your account...", Toast.LENGTH_SHORT).show();
                        sendUserToLogInActivity();
                        mAuth.signOut();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(Registration.this, "Error: " + error, Toast.LENGTH_LONG);
                        mAuth.signOut();
                    }
                }
            });
        }
    }

   /* public void computeMD5Hash(String password)
    {
        try
        {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for(int i = 0; i < messageDigest.length; i++)
            {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while(h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }
            displayHashPassword.setText(MD5Hash);
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }*/
}
