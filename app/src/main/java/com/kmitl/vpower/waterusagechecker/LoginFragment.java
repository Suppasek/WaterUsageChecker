package com.kmitl.vpower.waterusagechecker;
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

    FirebaseAuth userAuth;
    FirebaseFirestore mdb;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        userAuth = FirebaseAuth.getInstance();
        mdb = FirebaseFirestore.getInstance();
        initLoginBTN();
        super.onActivityCreated(savedInstanceState);
    }

    void initLoginBTN() {
        Button btn_login = getView().findViewById(R.id.login_login_btn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userId =  getView().findViewById(R.id.login_userId);
                EditText pass =  getView().findViewById(R.id.login_pass);

                String userID_str = userId.getText().toString();
                String pass_str = pass.getText().toString();

                Log.d("LOGIN", "USER ID = " + userID_str);
                Log.d("LOGIN", "PASSWORD = " + pass_str);

                if (userID_str.isEmpty() || pass_str.isEmpty()) {
                    Toast.makeText(getActivity(),
                            "กรุณาระบุ USER OR PASSWORD",
                            Toast.LENGTH_SHORT
                    ).show();

                    Log.d("USER", "USER OR PASSWORD IS EMPTY");
                }
                else {
                    userAuth.signInWithEmailAndPassword(userID_str, pass_str).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //if(userAuth.getCurrentUser().isEmailVerified()){
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.main_view,new MenuFragment())
                                        .commit();
                            //}

                            setUserProfile();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),
                                    "กรุณาระบุ USER OR PASSWORD ให้ถูกต้อง",
                                    Toast.LENGTH_SHORT
                            ).show();
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
            }
        });

    }

}
