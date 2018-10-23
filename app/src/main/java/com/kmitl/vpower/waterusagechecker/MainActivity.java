package com.kmitl.vpower.waterusagechecker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth userAuth;
    private FirebaseUser user;
    private FirebaseFirestore mdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userAuth = FirebaseAuth.getInstance();
        user = userAuth.getCurrentUser();
        mdb = FirebaseFirestore.getInstance();

        if (savedInstanceState == null) {
            if (user == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new LoginFragment())
                        .commit();
            } else {
                DocumentReference docRef = mdb.collection("users").document(user.getUid());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User FSuser = documentSnapshot.toObject(User.class);
                        if (FSuser.getType().equals("Water Meter Reader")) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_view, new WaterRecordFragment())
                                    .commit();
                        }
                    }
                });
            }
        }

    }
}
