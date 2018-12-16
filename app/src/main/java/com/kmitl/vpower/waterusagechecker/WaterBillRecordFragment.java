package com.kmitl.vpower.waterusagechecker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class WaterBillRecordFragment extends Fragment {

    private String room;
    private ArrayList<String> yearList = new ArrayList<>();
    private ArrayList<WaterRecord> bills = new ArrayList<>();
    private Spinner yearSpinner;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private RecyclerView billList;
    private WaterBillAdapter waterBillAdapter = new WaterBillAdapter();
    private ImageButton logoutBtn;
    private Dialog logoutDialog;
    private FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private TextView exception;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_water_bill, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        assignId();
        setToolBar();
        setLogoutBtn();
        setLogoutDialog();

        getRoom();
        createData();
        setRecyclerView();
        setSpinner();
        setOnYearSelected();

    }

    private void getRoom() {
        room = getArguments().getString("room");
    }

    private void createData() {
        for (Integer i = 2018; i < 2038; i++) {
            yearList.add(i.toString());
        }
    }

    private void assignId() {
        yearSpinner = getView().findViewById(R.id.water_bill_year_spinner);
        billList = getView().findViewById(R.id.water_bill_list);
        exception = getView().findViewById(R.id.water_bill_nothing);
        logoutBtn = getView().findViewById(R.id.toolbar_logout);
    }

    private void setRecyclerView() {
        billList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        waterBillAdapter.setItemList(bills);
        billList.setAdapter(waterBillAdapter);
    }

    private void setSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, yearList);
        yearSpinner.setAdapter(spinnerAdapter);
    }

    private void setOnYearSelected() {
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bills.clear();
                waterBillAdapter.notifyDataSetChanged();
                getData(yearList.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getData(final String year) {
        firestore.collection("rooms")
                .document("house_no " + room)
                .collection("water_usage")
//                .whereEqualTo("year", year)
//                .orderBy("month")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot bill : task.getResult()) {
                            if (bill.get("year").equals(year)) {
                                bills.add(bill.toObject(WaterRecord.class));
                            }
                        }
                        if (bills.isEmpty()) {
                            setTextVisibility(exception, true);
                        }
                        else {
                            Collections.reverse(bills);
                            setTextVisibility(exception, false);
                        }
                        waterBillAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void setLogoutBtn() {
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.show();
            }
        });
    }

    private void setLogoutDialog() {
        logoutDialog = new Dialog(getActivity());
        logoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutDialog.setContentView(R.layout.logout_dialog);
        logoutDialog.getWindow().setLayout(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels /2);

        Button logoutButton = logoutDialog.findViewById(R.id.logout_dialog_accept);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAuth.signOut();
                logoutDialog.dismiss();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button cancelButton = logoutDialog.findViewById(R.id.logout_dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });
    }

    private void setToolBar() {
        Toolbar toolbar = getView().findViewById(R.id.water_bill_toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("ค่าน้ำประปา");
    }

    private void setTextVisibility(TextView textView, Boolean visibility) {
        if (visibility) {
            textView.setVisibility(TextView.VISIBLE);
        }
        else {
            textView.setVisibility(TextView.GONE);
        }
    }

}
