package com.example.sharonsimon.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.Adapters.TaskAdapter;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.R;

import java.util.ArrayList;

public class ViewKenFragment extends Fragment {

    Ken ken;
    boolean isAdmin;

    ImageView myKenImage;
    TextView myKenPointsTV;
    TextView myKenNameTv;
    RecyclerView recycler;
    TaskAdapter adapter;

    public static ViewKenFragment newInstance(Ken ken, boolean isAdmin){
        ViewKenFragment fragment = new ViewKenFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("ken",ken);
        arguments.putBoolean("isAdmin",isAdmin);
        fragment.setArguments(arguments);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.view_ken_fragment,container,false);

        ken = (Ken)getArguments().getSerializable("ken");
        isAdmin = getArguments().getBoolean("isAdmin");

        myKenImage = viewGroup.findViewById(R.id.my_ken_image);
        myKenPointsTV = viewGroup.findViewById(R.id.my_ken_points_tv);
        myKenNameTv = viewGroup.findViewById(R.id.my_ken_name_tv);
        recycler = viewGroup.findViewById(R.id.tasks_rv);

        ArrayList<Task> tasks = ken.getTasks();
        adapter = new TaskAdapter(tasks);

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        myKenNameTv.setText(ken.getName());
        myKenPointsTV.setText(ken.getPoints() + "");

        return viewGroup;
    }
}
