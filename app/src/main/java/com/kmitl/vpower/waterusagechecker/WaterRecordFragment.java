package com.kmitl.vpower.waterusagechecker;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class WaterRecordFragment extends Fragment {

    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private Spinner roomSpinner;

    private ProgressBar progressBar;
    private FrameLayout progressScreen;
    private ConstraintLayout entireScreen;
    private ImageButton logoutBtn;

    private FirebaseFirestore mdb = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private CollectionReference colRef;

    private Dialog dialog;
    private Dialog errorDialog;
    private Dialog verifyDialog;
    private Dialog logoutDialog;
    private TextView errorText;

    private ArrayList<String> monthList = new ArrayList<>();
    private ArrayList<String> yearList = new ArrayList<>();
    private ArrayList<String> roomList = new ArrayList<>();

    private WaterRecord record;

    private String unitStr;
    private String room;
    private String month;
    private String year;
    private int recordNo;
    private int rate;
    private int previousUnit = 0;
    private int previousRecordNo = 0;
    private boolean recordExist = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_water_record, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = getView().findViewById(R.id.water_record_toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("บันทึกหน่วยค่าน้ำ");

        monthSpinner = getView().findViewById(R.id.water_record_month);
        yearSpinner = getView().findViewById(R.id.water_record_year);
        roomSpinner = getView().findViewById(R.id.water_record_room);

        progressBar = getView().findViewById(R.id.water_record_progressBar);
        progressScreen = getView().findViewById(R.id.water_record_progressBarHolder);
        entireScreen = getView().findViewById(R.id.water_record_entire);

        setDialog();

        setLogoutBtn();

        createData();

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, monthList);
        monthSpinner.setAdapter(adapterMonth);

        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, yearList);
        yearSpinner.setAdapter(adapterYear);

        ArrayAdapter<String> adapterRoom = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, roomList);
        roomSpinner.setAdapter(adapterRoom);

        getRate();

        initAddBtn();

    }

    private void initAddBtn() {
        Button btnAdd = getView().findViewById(R.id.water_record_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText meter = getView().findViewById(R.id.water_record_meter);
                unitStr = meter.getText().toString();
                month = monthSpinner.getSelectedItem().toString();
                year = yearSpinner.getSelectedItem().toString();
                room = roomSpinner.getSelectedItem().toString();

                TextView roomText = dialog.findViewById(R.id.record_dialog_room);
                TextView monthText = dialog.findViewById(R.id.record_dialog_month);
                TextView unitText = dialog.findViewById(R.id.record_dialog_unit);

                roomText.setText(room);
                monthText.setText(year + "/" + month);
                unitText.setText(unitStr + " หน่วย");

                dialog.show();

            }
        });
    }

    private void getPreviousRecord() {
        if (recordExist) {
            Log.wtf("WRF", "create new record");
            createRecord();
        } else {
            colRef = mdb.collection("rooms").document("house_no " + room).collection("water_usage");
            colRef.whereEqualTo("year", year).whereEqualTo("month", month).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<QuerySnapshot> task1) {
                    if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                        //Record already exist
                        previousRecordNo = Integer.parseInt(task1.getResult().getDocuments().get(0).get("recordNo").toString());
                        verifyDialog.show();
                    } else if (task1.isSuccessful()) {
                        createRecord();
                    }
                }
            });

        }
    }

    private void createRecord() {
        colRef.orderBy("recordNo", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        int previousMonth = Integer.parseInt(task.getResult().getDocuments().get(0).get("month").toString());
                        int previousYear = Integer.parseInt(task.getResult().getDocuments().get(0).get("year").toString());
                        int thisMonth = Integer.parseInt(month);
                        int thisYear = Integer.parseInt(year);
                        if ((thisMonth == previousMonth + 1 && thisYear == previousYear) || ((thisMonth == 1 && previousMonth == 12) && (thisYear == previousYear + 1))) {
                            recordNo = Integer.parseInt(task.getResult().getDocuments().get(0).get("recordNo").toString()) + 1;
                            previousUnit = Integer.parseInt(task.getResult().getDocuments().get(0).get("recordUnit").toString());
                            pushRecord();
                            Log.wtf("WRF", "record = " + recordNo + " new record");
                        } else {
                            errorText.setText("กรุณาเลือกเดือนและปีให้ถูกต้อง");
                            errorDialog.show();
                            setProgressBar(false);
                        }
                    } else {
                        recordNo = 1;
                        previousUnit = 0;
                        pushRecord();
                        Log.wtf("WRF", "record = " + recordNo + " new record new collection");
                    }
                    recordExist = false;
                }

            }
        });
    }

    private void setDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.record_dialog);

        Button acceptButton = dialog.findViewById(R.id.record_dialog_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressBar(true);
                dialog.dismiss();
                getPreviousRecord();
            }
        });

        Button cancelButton = dialog.findViewById(R.id.record_dialog_decline);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        errorDialog = new Dialog(getActivity());
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.setContentView(R.layout.error_dialog);

        Button acceptButton2 = errorDialog.findViewById(R.id.error_dialog_accept);
        acceptButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.dismiss();
            }
        });

        errorText = errorDialog.findViewById(R.id.error_dialog_text);

        verifyDialog = new Dialog(getActivity());
        verifyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verifyDialog.setContentView(R.layout.verify_dialog);
        verifyDialog.getWindow().setLayout(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels /2);

        Button verifyButton = verifyDialog.findViewById(R.id.verify_dialog_accept);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdb.collection("rooms").document("house_no " + room).collection("water_usage").document(year + "_" + month).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        recordExist = true;
                        Log.wtf("WRF", "record = " + recordNo + " delete previous record");
                        verifyDialog.dismiss();
                        setProgressBar(false);
                        getPreviousRecord();
                    }
                });

            }
        });

        Button cancelButton2 = verifyDialog.findViewById(R.id.verify_dialog_cancel);
        cancelButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressBar(false);
                verifyDialog.dismiss();
            }
        });

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

        Button cancelButton3 = logoutDialog.findViewById(R.id.logout_dialog_cancel);
        cancelButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });

    }


    private void getRate() {
        setProgressBar(true);
        mdb.collection("Water Rates").orderBy("recordNo", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    rate = Integer.parseInt(task.getResult().getDocuments().get(0).get("rate").toString());
                    setProgressBar(false);
                }
            }
        });
    }

    private void pushRecord() {
        String monthFormat = year + "_" + month;
        int unit = Integer.parseInt(unitStr);
        int price = (unit - previousUnit) * rate;
        Log.wtf("WRF", unit + " - " + previousUnit + " * " + rate);
        if (recordNo == 1 || (unit >= previousUnit)) {
            if (previousRecordNo != 0) {
                record = new WaterRecord(unit, year, month, room, user.getDisplayName(), previousRecordNo, price);
                previousRecordNo = 0;
            } else {
                record = new WaterRecord(unit, year, month, room, user.getDisplayName(), recordNo, price);
            }
            mdb.collection("rooms")
                    .document("house_no " + room)
                    .collection("water_usage")
                    .document(monthFormat)
                    .set(record).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.wtf("WRF", "month " + month + " year " + year + " record no " + recordNo);
                    setProgressBar(false);
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            setProgressBar(false);
            errorText.setText("กรุณาใส่เลขมิเตอร์น้ำให้ถูกต้อง");
            errorDialog.show();
        }
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

    private void createData() {
        for (Integer i = 1; i < 13; i++) {
            monthList.add(i.toString());
        }
        for (Integer i = 2018; i < 2038; i++) {
            yearList.add(i.toString());
        }
        for (Integer i = 1; i < 21; i++) {
            roomList.add(i.toString());
        }
    }

}
