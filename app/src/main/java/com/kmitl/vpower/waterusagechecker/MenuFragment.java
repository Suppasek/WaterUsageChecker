package com.kmitl.vpower.waterusagechecker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Penporn Pettammarot 59070123 IT KMITL
 */

public class MenuFragment extends Fragment {

    ArrayList<String> menu;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_menu, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        menu = new ArrayList<>();

        menu.add("Record");
        menu.add("Sign Out");

        final ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                menu
        );

        ListView menuList = (ListView) getView().findViewById(R.id.menu_List);
        menuList.setAdapter(menuAdapter);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("MENU", "Click on menu = "+menu.get(i));
                //menu.add("new Value");
                menuAdapter.notifyDataSetChanged();

                if (menu.get(i).equals("Record")) {

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new WaterRecordFragment())
                            .addToBackStack(null)
                            .commit();
                }

                else if(menu.get(i).equals("Sign Out")){
                    FirebaseAuth userAuth = FirebaseAuth.getInstance();
                    userAuth.signOut();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new LoginFragment())
                            .addToBackStack(null)
                            .commit();
                }


            }
        });

    }


}