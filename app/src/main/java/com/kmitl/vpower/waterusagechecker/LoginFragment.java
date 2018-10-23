package com.kmitl.vpower.waterusagechecker;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginFragment extends Fragment{

    private FirebaseAuth userAuth;
    private FirebaseFirestore mdb;
    private ProgressBar progressBar;
    private FrameLayout progressScreen;
    private ConstraintLayout entireScreen;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userAuth = FirebaseAuth.getInstance();
        mdb = FirebaseFirestore.getInstance();

        progressBar = getView().findViewById(R.id.login_progressBar);
        progressScreen = getView().findViewById(R.id.login_progressBarHolder);
        entireScreen = getView().findViewById(R.id.login_entire);
        setProgressBar(false);
        initLoginBTN();

    }

    void initLoginBTN() {
        Button btn_login = getView().findViewById(R.id.login_button);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressBar(true);
                EditText userId =  getView().findViewById(R.id.login_userId);
                EditText pass =  getView().findViewById(R.id.login_pass);

                String userID_str = userId.getText().toString();
                String pass_str = pass.getText().toString();

                Log.wtf("LOGIN", "USER ID = " + userID_str);
                Log.wtf("LOGIN", "PASSWORD = " + pass_str);

                if (userID_str.isEmpty() || pass_str.isEmpty()) {
                    Toast.makeText(getActivity(),
                            "กรุณาระบุ USER OR PASSWORD",
                            Toast.LENGTH_SHORT
                    ).show();
                    setProgressBar(false);
                    Log.d("USER", "USER OR PASSWORD IS EMPTY");
                }
                else {
                    Log.wtf("login", "not empty");
                    userAuth.signInWithEmailAndPassword(userID_str, pass_str).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            setUserProfile();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),
                                    "กรุณาระบุ USER OR PASSWORD ให้ถูกต้อง",
                                    Toast.LENGTH_SHORT
                            ).show();
                            setProgressBar(false);
                        }
                    });
                    Log.d("USER", "INVALID USER NAME OR PASSWORD");
                }

            }
        });
    }

    void setUserProfile() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = mdb.collection("users").document(firebaseUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(user.getName()).build();
                firebaseUser.updateProfile(profileUpdates);
                if (user.getType().equals("Water Meter Reader")) {

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view,new WaterRecordFragment())
                            .commit();
                    Log.wtf("login", "success");
                    //setProgressBar(false);
                }
            }
        });

    }

    private void setProgressBar(Boolean on) {
        if (on) {
            progressBar.setVisibility(View.VISIBLE);
            progressScreen.setVisibility(View.VISIBLE);
            entireScreen.setForeground(new ColorDrawable(0xFF000000));
            entireScreen.setAlpha((float) 0.4);

        } else {
            progressBar.setVisibility(View.GONE);
            progressScreen.setVisibility(View.GONE);
            entireScreen.setForeground(null);
            entireScreen.setAlpha((float) 1);
        }
    }


}
