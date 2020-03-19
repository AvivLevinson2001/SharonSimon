package com.example.sharonsimon.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.Activities.MainActivity;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.Dialogs.LoadingDialogBuilder;
import com.example.sharonsimon.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyKenFragment extends Fragment {

    ImageView myKenImage;
    TextView myKenPointsTV;
    TextView myKenNameTv;
    RecyclerView recycler;

    SharedPreferences sp;
    Dialog loadingDialog;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference();

    Ken myKen;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.my_ken_fragment,container,false);

        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);

        myKenImage = viewGroup.findViewById(R.id.my_ken_image);
        myKenPointsTV = viewGroup.findViewById(R.id.my_ken_points_tv);
        myKenNameTv = viewGroup.findViewById(R.id.my_ken_name_tv);
        recycler = viewGroup.findViewById(R.id.tasks_rv);

        getMyKenFromFirebase();

        return viewGroup;
    }

    private void getMyKenFromFirebase(){
        loadingDialog = LoadingDialogBuilder.createLoadingDialog(getActivity());
        loadingDialog.show();

        final String myKenName = sp.getString("ken","");
        reference.child("kens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    final String[] kensNames = new String[]
                            {"מקורות", "המעפיל", "מעיין", "העוגן", "רמות חפר", "יקום", "געש", "גן שמואל",
                                    "להבות חביבה", "מבואות עירון", "כרכור", "כפס מרום", "הרצליה", "חריש"};

                    ArrayList<Ken> kens = new ArrayList<>();
                    for(String kenName : kensNames){
                        Ken newKen = new Ken(kenName, new ArrayList<Task>(), new ArrayList<Task>(), 0);
                        kens.add(newKen);
                        if(kenName.equals(myKenName))
                            myKen = newKen;
                    }

                    reference.child("kens").setValue(kens);
                }
                else{
                    GenericTypeIndicator<ArrayList<Ken>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Ken>>() {};
                    ArrayList<Ken> kens = dataSnapshot.getValue(genericTypeIndicator);
                    for(Ken ken : kens){
                        if(ken.getName().equals(myKenName)){
                            myKen = ken;
                        }
                    }
                }
                updateKenInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Something went wrong... Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateKenInfo()
    {
        myKenNameTv.setText(myKen.getName());
        myKenPointsTV.setText(myKen.getPoints() + "");
        //Todo Add Image

        loadingDialog.dismiss();
    }
}
