package com.kmitl.vpower.waterusagechecker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new LoginFragment())
                            .commit();
        //

//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            firebase = FirebaseAuth.getInstance();
//            user = firebase.getCurrentUser();
//            setContentView(R.layout.activity_main);
//
//            if (savedInstanceState == null){
//                if(user == null){
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.main_view, new LoginFragment())
//                            .commit();
//                }
//                else{
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.main_view, new MenuFragment())
//                            .commit();
//                }
//            }

    }
}
