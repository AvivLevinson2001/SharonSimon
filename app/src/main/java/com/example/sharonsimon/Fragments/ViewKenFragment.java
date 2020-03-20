package com.example.sharonsimon.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.example.sharonsimon.Interfaces.FirebaseChangesListener;
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

    FirebaseChangesListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity)context;
        try{
            listener = (FirebaseChangesListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("Activity: " + activity.toString() + " must implement KensRecyclerViewFragmentListener");
        }
    }

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

        final ArrayList<Task> tasks = ken.getTasks();
        adapter = new TaskAdapter(tasks);

        adapter.setListener(new TaskAdapter.myTaskAdapterListener() {
            @Override
            public void onTaskClick(int position, View v) {

            }

            @Override
            public void onTaskLongClick(int position, View v) {

            }

            @Override
            public void onCheckBoxClick(int position, View v) {
                tasks.get(position).setCompleted(!tasks.get(position).isCompleted());
                adapter.notifyDataSetChanged();
                ken.setTasks(tasks);
                if(tasks.get(position).isCompleted())
                    ken.setPoints(ken.getPoints() + tasks.get(position).getPoints());
                else
                    ken.setPoints(ken.getPoints() - tasks.get(position).getPoints());
                listener.saveKenToFirebase(ken);
                myKenPointsTV.setText(ken.getPoints() + "");
            }
        });

        adapter.setCheckBoxIsClickable(isAdmin);

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        myKenNameTv.setText(ken.getName());
        myKenPointsTV.setText(ken.getPoints() + "");

        return viewGroup;
    }
}
