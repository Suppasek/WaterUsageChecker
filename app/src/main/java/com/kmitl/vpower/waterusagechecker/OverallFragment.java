package com.kmitl.vpower.waterusagechecker;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private StorageReference storageRef;
    private StorageReference imageRef;
    private UploadTask mUploadTask;
    private FirebaseStorage firebaseStorage;

    private static String downloadURL = "";

    private static Boolean NEW_URL_CHECK = false;

    //merge complete
    public OverallFragment() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.waterRecords = new ArrayList<WaterRecord>();
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageRef = firebaseStorage.getReference();
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

        return inflater.inflate(R.layout.fragment_overall_new, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("overall", "In onActivityCreated");
        setToolbarTitle();
        setLogoutBtn();
        setLogoutDialog();
        createMonthSpinner();
        setOnYearSelected();
        initChangeRateBtn();
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
        ImageView CSVbtn = getView().findViewById(R.id.fragment_overall_csv_btn);

        CSVbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File csvFile = null;
                String recDate = dateSpinner.getSelectedItem().toString();
                Log.d("CSV", "Before CsvFileWriter recDtae = " + recDate);
                if (checkData > 0 && NEW_URL_CHECK) {
                    Log.d("CSV", "There are " + intToStr(checkData) + " records for CSV");
                    csvFile = CsvFileWriter.writeCsvFile(recDate, waterRecords, getActivity(), getContext());
                    uploadFromStream(csvFile);
                } else {
                    initUrlDownloadDialog(downloadURL);
                }
            }
        });
    }

    private void uploadFromStream(File file) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageRef = storageRef.child("reports/" + file.getName());
        mUploadTask = imageRef.putStream(stream);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("CSV", exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadURL = taskSnapshot.getDownloadUrl().toString();
                Log.d("CSV", "Success URL = " + taskSnapshot.getDownloadUrl().toString());
                initUrlDownloadDialog(downloadURL);
                NEW_URL_CHECK = false;
            }
        });
    }

    private void initUrlDownloadDialog(final String downloadURL) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.url_dialog, null);
        builder.setView(view);

        final EditText waterRate = (EditText) view.findViewById(R.id.url_dialog_download_url);

        waterRate.setText(downloadURL);

        builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Url ", downloadURL);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(),
                        "Copied to Clipboard!!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void initChangeRateBtn() {
        ImageView btnShow = getView().findViewById(R.id.fragment_overall_change_rate_btn);

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();

                View view = inflater.inflate(R.layout.change_rate_dialog, null);
                builder.setView(view);

                final TextView dialogMessage = (TextView) view.findViewById(R.id.change_rate_dialog_message);
                final EditText waterRate = (EditText) view.findViewById(R.id.change_rate_water_rate);

                dialogMessage.setText("กรุณาใส่อัตราค่าน้ำที่ต้องการ");

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String waterRateStr = waterRate.getText().toString();
                        setRate(waterRateStr);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });
    }

    private void createMonthSpinner() {
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

    public void getValueDB(String recDate) {
        Log.d("overall", "In getValueDB\nrecDate = " + recDate);

        ListView recordTable = (ListView) getView().findViewById(R.id.fragment_overall_list);

        final WaterRecordAdapter recordAdapter = new WaterRecordAdapter(getActivity(), R.layout.fragment_overall_item, waterRecords);
        recordAdapter.sort(new Comparator<WaterRecord>() {
            @Override
            public int compare(WaterRecord o1, WaterRecord o2) {
                return o1.compare2To(o2);
            }
        });

        recordTable.setAdapter(recordAdapter);
        waterRecords.clear();
        checkData = 0;

        for (int i = 1; i < 41; i++ ) {
            Log.d("overall", "Before Loop " + intToStr(i));
            final int hN = i;
            DocumentReference docRef = firebaseFirestore
                    .collection("rooms")
                    .document("house_no " + intToStr(i))
                    .collection("water_usage")
                    .document(recDate);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        checkData++;
                        WaterRecord record = task.getResult().toObject(WaterRecord.class);
                        waterRecords.add(record);
                        recordAdapter.notifyDataSetChanged();
                        Log.d("overall", "There data from House No." + record.getHouseNo());
                    }else {
                        Log.d("overall", "No Data for House No." + intToStr(hN));
                    }
                    if (hN == 40) {
                        if (checkData == 0) {
                            Toast.makeText(getActivity(),
                                    "No Data",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else if (checkData > 0) {
                            Toast.makeText(getActivity(),
                                    "Success",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
            });
        }

        Log.d("overall", "End getValueDB");
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

    private void setOnYearSelected() {
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String recDate = monthList.get(position);
                Log.d("overall", "Date was selected -> " + recDate);
                NEW_URL_CHECK = true;
                getValueDB(recDate);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setRate(final String newRate) {
        firebaseFirestore.collection("Water Rates").orderBy("recordNo", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
//                    Integer rate = Integer.parseInt(task.getResult().getDocuments().get(0).get("rate").toString());
                    Integer recordNo = Integer.parseInt(task.getResult().getDocuments().get(0).get("recordNo").toString()) + 1;
                    String newRecordNo = recordNo.toString();

                    Map<String, Object> record = new HashMap<>();
                    record.put("rate", newRate);
                    record.put("recordNo", newRecordNo);

                    firebaseFirestore.collection("Water Rates").document("record " + newRecordNo).set(record)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getActivity(),
                                            "New water rate was saved",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),
                                    "Failed to save new water rate",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
                }
            }
        });
    }

    public String intToStr(int number) {
        return Integer.toString(number);
    }

}