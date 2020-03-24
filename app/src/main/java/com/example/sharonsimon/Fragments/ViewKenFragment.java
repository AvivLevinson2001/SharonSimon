package com.example.sharonsimon.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.Adapters.TaskAdapter;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.Interfaces.FirebaseChangesListener;
import com.example.sharonsimon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewKenFragment extends Fragment {

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    Ken ken;
    boolean isAdmin;
    ArrayList<Task> tasks;
    Uri[] videosUri;

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

        tasks = ken.getTasks();
        if(tasks == null) tasks = new ArrayList<>();

        videosUri = new Uri[tasks.size()];

        adapter = new TaskAdapter(tasks,videosUri,getActivity());

        adapter.setListener(new TaskAdapter.myTaskAdapterListener() {
            @Override
            public void onTaskClick(int position, View v) {

            }

            @Override
            public void onTaskLongClick(int position, View v) {

            }

            @Override
            public void onCheckBoxClick(int position, View v) {
                if(!tasks.get(position).isCompleted()){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/*");
                    getActivity().getIntent().putExtra("taskDesc",tasks.get(position).getDesc());
                    getActivity().getIntent().putExtra("kenName",ken.getName());
                    getActivity().startActivityForResult(Intent.createChooser(intent,"select video"), 1);
                    tasks.get(position).setCompleted(true);
                    ken.setPoints(ken.getPoints() + tasks.get(position).getPoints());
                }
                else{
                    tasks.get(position).setCompleted(false);
                    ken.setPoints(ken.getPoints() - tasks.get(position).getPoints());
                }
                adapter.notifyDataSetChanged();
                listener.saveKenToFirebase(ken);
                myKenPointsTV.setText(ken.getPoints() + "");
            }

            @Override
            public void onVideoClick(int position, View v) {
                Log.d("test","video clicked: videos/" + ken.getName() + "/" + tasks.get(position));
            }
        });

        adapter.setCheckBoxIsClickable(isAdmin);

        recycler.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recycler.setLayoutManager(mLayoutManager);

        myKenNameTv.setText(ken.getName());
        myKenPointsTV.setText(ken.getPoints() + "");

        for(final Task task1 : tasks){
            if(task1.isCompleted()) {
                final Task task2 = task1;
                storageReference.child("videos/" + ken.getName() + "/" + task2.getDesc()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        videosUri[tasks.indexOf(task2)] = uri;
                        adapter.setVideoUri(tasks.indexOf(task2),uri);
                    }
                });
            }
        }

        return viewGroup;
    }

    public void addVideoUri(String taskDesc, Uri uri){
        for(Task task : tasks){
            if(task.getDesc().equals(taskDesc)){
                videosUri[tasks.indexOf(task)] = uri;
                adapter.setVideoUri(tasks.indexOf(task),uri);
            }
        }
    }
}
