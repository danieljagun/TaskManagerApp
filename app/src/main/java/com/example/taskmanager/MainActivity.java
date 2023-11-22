package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button logoutButton;
    private TextView userNameTextView,userPhoneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));*/

        auth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout_button);
        userNameTextView = findViewById(R.id.user_name_text_view);
        userPhoneTextView = findViewById(R.id.user_phone_text_view);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Assuming the user details are retrieved from Firestore after sign-in
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("name");
                        String userPhone = documentSnapshot.getString("phone");

                        // Set the user's name and phone in the TextViews
                        userNameTextView.setText(userName);
                        userPhoneTextView.setText(userPhone);
                    } else {
                        Log.d("MainActivity", "Document does not exist");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("MainActivity", "Error fetching user details: " + e.getMessage());
                }
            });
        }
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

}