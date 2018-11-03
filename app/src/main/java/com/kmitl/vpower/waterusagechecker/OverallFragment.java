package com.kmitl.vpower.waterusagechecker;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OverallFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<String> monthList = new ArrayList<>();
    private List<WaterRecord> waterRecords;
    private Spinner dateSpinner;
    private int checkData;

    private Dialog dialog;
    private Dialog errorDialog;
    private Dialog verifyDialog;
    private Dialog logoutDialog;
    private TextView errorText;

    private ImageButton logoutBtn;

    //merge complete
    public OverallFragment() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.waterRecords = new ArrayList<WaterRecord>();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permssion granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Permssion not granted!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        return inflater.inflate(R.layout.fragment_overall, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("overall", "In onActivityCreated");
//        setToolbarTitle();
//        setLogoutBtn();
//        setDialog();
        createMothSpinner();
//        initBackBtn();
        initShowBtn();
        initCSVBtn();
    }

    private void setToolbarTitle() {
        TextView toolbarTitle = getView().findViewById(R.id.toolbar_title);
        toolbarTitle.setText("สถิติการใช้น้ำ");
    }

    private void setLogoutBtn() {
        logoutBtn = getView().findViewById(R.id.toolbar_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.show();
            }
        });
    }

    private void initBackBtn() {
        Button Backbtn = getView().findViewById(R.id.fragment_overall_back_btn);

        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new MenuFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void initCSVBtn() {
//        ImageView CSVbtn = getView().findViewById(R.id.fragment_overall_csv_btn);
        Button CSVbtn = getView().findViewById(R.id.fragment_overall_csv_btn);
        CSVbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recDate = dateSpinner.getSelectedItem().toString();
                Log.d("CSV", "Before CsvFileWriter recDtae = " + recDate);
                if (checkData > 0) {
                    Log.d("CSV", "There are " + intToStr(checkData) + " records for CSV");
                    CsvFileWriter.writeCsvFile(recDate, waterRecords, getActivity(), getContext());
                    Toast.makeText(getActivity(),
                            "CSV file was be downloaded.",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Log.d("CSV", "There are no data for CSV");
                    Toast.makeText(getActivity(),
                            "There are no data for this month.",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            }
        });
    }

    private void initShowBtn() {
//        ImageView btnShow = getView().findViewById(R.id.fragment_overall_show_btn);
        Button btnShow = getView().findViewById(R.id.fragment_overall_show_btn);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValueDB();
            }
        });
    }

    private void createMothSpinner() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
        String[] currentYearMonth = sdf.format(Calendar.getInstance().getTime()).split("_");
        int currentYear = Integer.parseInt(currentYearMonth[0]);
        int currentMonth = Integer.parseInt(currentYearMonth[1]);
        dateSpinner = getView().findViewById(R.id.fragment_overall_date_spinner);
        for (Integer y = currentYear; y > 2017 ; y--) {
            for (Integer m = currentMonth; m > 0; m--) {
                monthList.add(intToStr(y) + "_" + intToStr(m));
            }
        }
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, monthList);
        dateSpinner.setAdapter(adapterMonth);
    }

    public void getValueDB() {
        Log.d("overall", "In getValueDB");

        String recDate = dateSpinner.getSelectedItem().toString();

        ListView recordTable = (ListView) getView().findViewById(R.id.fragment_overall_list);

        final WaterRecordAdapter recordAdapter = new WaterRecordAdapter(getActivity(), R.layout.fragment_overall_item, waterRecords);

        recordTable.setAdapter(recordAdapter);
        waterRecords.clear();
        checkData = 0;

        for (int i = 1; i < 41; i++ ) {
            Log.d("overall", "Before Loop " + intToStr(i));
            final int hN = i;

            firebaseFirestore
                    .collection("rooms")
                    .document("house_no " + intToStr(i))
                    .collection("water_usage")
                    .document(recDate)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        checkData++;
                        WaterRecord record = new WaterRecord(
                      Integer.parseInt(task.getResult().get("recordUnit").toString()),
                            task.getResult().get("year").toString(),
                            task.getResult().get("month").toString(),
                            task.getResult().get("houseNo").toString(),
                            task.getResult().get("signature").toString(),
                            Integer.parseInt(task.getResult().get("recordNo").toString()),
                            Integer.parseInt(task.getResult().get("price").toString())
                    );
                        waterRecords.add(record);
                        recordAdapter.notifyDataSetChanged();
                        Log.d("overall", "Have data from House No." + intToStr(hN));
                    }else {
                        Log.d("overall", "No Data for House No." + intToStr(hN));
                    }

                }
            });
//            if (checkData > 0) {
//                Toast.makeText(getActivity(),
//                        "No data.",
//                        Toast.LENGTH_SHORT
//                ).show();
//            } else if (checkData == 0) {
//                Toast.makeText(getActivity(),
//                        "Success " + intToStr(checkData) + " " + intToStr(0),
//                        Toast.LENGTH_SHORT
//                ).show();
//            }
        }
        Log.d("overall", "End getValueDB");
    }

    private void setDialog() {
        logoutDialog = new Dialog(getActivity());
        logoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutDialog.setContentView(R.layout.logout_dialog);
        logoutDialog.getWindow().setLayout(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels /2);


        Button logoutButton = logoutDialog.findViewById(R.id.logout_dialog_accept);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                logoutDialog.dismiss();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button cancelButton3 = logoutDialog.findViewById(R.id.logout_dialog_cancel);
        cancelButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });

    }

    public String intToStr(int number) {
        return Integer.toString(number);
    }

}