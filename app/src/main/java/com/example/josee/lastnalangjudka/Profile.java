package com.example.josee.lastnalangjudka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private TextView WholeName, eMail, contactNumber, address, beerDay;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference profileUserRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WholeName = (TextView) findViewById(R.id.wholeName);
        eMail = (TextView) findViewById(R.id.EmailAdd);
        contactNumber = (TextView) findViewById(R.id.Contact);
        profileImage = (CircleImageView) findViewById(R.id.imageClicked);
        address = (TextView) findViewById(R.id.addressTextView);
        beerDay = (TextView) findViewById(R.id.birthdayTextView);

        mAuth = FirebaseAuth.getInstance();
        currentUserID =mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String myFirstName = dataSnapshot.child("firstName").getValue().toString();
                    String myLastName = dataSnapshot.child("lastName").getValue().toString();
                    String myEmail =  dataSnapshot.child("email").getValue().toString();
                    String myContact = dataSnapshot.child("contactNumber").getValue().toString();
                    String lugar = dataSnapshot.child("address").getValue().toString();
                    String image = dataSnapshot.child("profileImage").getValue().toString();
                    String bDay = dataSnapshot.child("birthDay").getValue().toString();

                    WholeName.setText(myFirstName+ " " + myLastName + " ");
                    eMail.setText(" " + myEmail);
                    contactNumber.setText(" " + myContact);
                    address.setText(" " + lugar);
                    beerDay.setText(" " + bDay);
                    if(!image.isEmpty())
                    {
                        Picasso.get().load(image).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}