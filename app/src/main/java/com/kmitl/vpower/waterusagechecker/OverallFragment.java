package com.kmitl.vpower.waterusagechecker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OverallFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private List<Room> rooms;

    public OverallFragment() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.rooms = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overall, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getValueDB();

    }

    public void getValueDB() {


//        ListView weightList = getView().findViewById(R.id.fragment_weight_history_list);

//        final WeightAdapter weightAdapter = new WeightAdapter(
//                getActivity(),
//                R.layout.fragment_weight_item,
//                weights
//        );
//
//        weightList.setAdapter(weightAdapter);
//        weights.clear();

        firebaseFirestore
                .collection("rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.wtf("Overall", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.wtf("Overall", "Error getting documents: ", task.getException());
                        }
                    }
                });
//                .collection("rooms")
//                .document(firebaseAuth.getUid())
//                .collection("weight")
//                .orderBy("date", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                            weights.add(doc.toObject(Weight.class));
//                        }
//                        weightAdapter.notifyDataSetChanged();
//                        Log.d("HISTORY", "Query data from Firestore and set to ArrayList");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("HISTORY", "Query data from Firestore error!!!");
//                Toast.makeText(getActivity(), "Firestore Error!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
