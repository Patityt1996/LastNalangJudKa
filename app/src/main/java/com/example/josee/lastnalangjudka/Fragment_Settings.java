package com.example.josee.lastnalangjudka;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.telephony.CellIdentityCdma;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Fragment_Settings extends Fragment {

    private CircleImageView imageView;
    private EditText firstName, lastName, contact, password, address, beerDay;
    private Button updateBtn, birthdayBtn;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;
    private StorageReference UserProfileImageRef;
    String currentUserID;
    Calendar mCurrentDate;
    int day, month, year;
    final static int GALLERY_PICK = 1;


    public Fragment_Settings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        getActivity().setTitle("Account Settings");
        View v = inflater.inflate(R.layout.fragment_fragment__settings, container, false);

        imageView = (CircleImageView) v.findViewById(R.id.addUpdateImage);
        firstName = (EditText) v.findViewById(R.id.firstName);
        lastName = (EditText) v.findViewById(R.id.lastName);
        contact = (EditText) v.findViewById(R.id.kontakNumber);
        password = (EditText) v.findViewById(R.id.newPassword);
        address = (EditText) v.findViewById(R.id.addressEditText);
        updateBtn = (Button) v.findViewById(R.id.updateButton);
        beerDay = (EditText) v.findViewById(R.id.bDayEditText);
        birthdayBtn = (Button) v.findViewById(R.id.beerDayBtn);

        mCurrentDate = Calendar.getInstance();

        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCurrentDate.get(Calendar.MONTH);
        year = mCurrentDate.get(Calendar.YEAR);

        month = month + 1;

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference();

        birthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        beerDay.setText(dayOfMonth + "/" + month + "/" + year );
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                /*Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);*/

                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_PICK);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                validateAccountInfo();
            }
        });

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String image = dataSnapshot.child("profileImage").getValue().toString();

                if(!image.isEmpty())
                {
                    Picasso.get().load(image).into(imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    private void validateAccountInfo()
    {
        String FirstName = firstName.getText().toString();
        String LastName = lastName.getText().toString();
        String Password = password.getText().toString();
        String ContactNumber = contact.getText().toString();
        String Lugar = address.getText().toString();
        String Birthday = beerDay.getText().toString();


        updateAccountInformation(FirstName, LastName, Password, ContactNumber, Lugar, Birthday);
    }

    private void updateAccountInformation(String firstName, String lastName, String password, String contactNumber, String lugar, String burpday)
    {
        HashMap userMap = new HashMap();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("password", password);
        userMap.put("contactNumber", contactNumber);
        userMap.put("address", lugar);
        userMap.put("birthDay", burpday);


        usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(getActivity(), "Account settings updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null)
        {
            Uri image = data.getData();
            imageView.setImageURI(image);

            if(resultCode == RESULT_OK)
            {

                //loadingBar.setTitle("Saving Information");
                //loadingBar.setMessage("Please wait while updating your account");
                //loadingBar.show();
                //loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = image;

                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getActivity(), "Profile image stored successfully to firebase storage", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            usersRef.child("profileImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(getActivity(), "Profile image stored to firebase database successfully", Toast.LENGTH_SHORT).show();
                                                //loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                                                //loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(getActivity(), "Error occured: Image can not be cropped. Please try again", Toast.LENGTH_SHORT).show();
                //loadingBar.dismiss();
            }
//            Toast.makeText(getActivity(), "" + "true", Toast.LENGTH_SHORT).show();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getActivity());
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {

                loadingBar.setTitle("Saving Information");
                loadingBar.setMessage("Please wait while updating your account");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getActivity(), "Profile image stored successfully to firebase storage", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            usersRef.child("profileImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(getActivity(), "Profile image stored to firebase database successfully", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(getActivity(), "Error occured: Image can not be cropped. Please try again", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }
}
