package com.example.sharonsimon.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sharonsimon.Adapters.TaskAdapter;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.Interfaces.FirebaseChangesListener;
import com.example.sharonsimon.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewKenFragment extends Fragment {

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    Ken ken;
    boolean isAdmin;
    ArrayList<Task> tasks;

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
                             final ViewGroup container, Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.view_ken_fragment,container,false);

        ken = (Ken)getArguments().getSerializable("ken");
        isAdmin = getArguments().getBoolean("isAdmin");

        myKenImage = viewGroup.findViewById(R.id.my_ken_image);
        myKenPointsTV = viewGroup.findViewById(R.id.my_ken_points_tv);
        myKenNameTv = viewGroup.findViewById(R.id.my_ken_name_tv);
        recycler = viewGroup.findViewById(R.id.tasks_rv);

        tasks = ken.getTasks();
        if(tasks == null) tasks = new ArrayList<>();
        adapter = new TaskAdapter(tasks);

        adapter.setListener(new TaskAdapter.myTaskAdapterListener() {
            @Override
            public void onTaskClick(int position, View v) {

            }

            @Override
            public void onTaskLongClick(final int position, View v) {
                if(isAdmin){
                    PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.task_long_click_menu_admin, popupMenu.getMenu());
                    popupMenu.getMenu().setGroupVisible(tasks.get(position).isCompleted() ? R.id.action_set_task_is_not_completed_group : R.id.action_set_task_is_completed_group, true);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.action_set_task_is_completed:
                                    tasks.get(position).setCompleted(true);
                                    ken.setPoints(ken.getPoints() + tasks.get(position).getPoints());
                                    listener.saveKenToFirebase(ken);
                                    myKenPointsTV.setText(ken.getPoints() + "");
                                    break;
                                case R.id.action_set_task_is_not_completed:
                                    tasks.get(position).setCompleted(false);
                                    ken.setPoints(ken.getPoints() - tasks.get(position).getPoints());
                                    listener.saveKenToFirebase(ken);
                                    myKenPointsTV.setText(ken.getPoints() + "");
                                    break;
                                case R.id.action_add_task_to_highlights:
                                    if(tasks.get(position).isCompleted()) {
                                        listener.addTaskToHighlights(tasks.get(position).getDesc(), ken.getName());
                                    }
                                    else{
                                        Snackbar.make(container,"המשימה לא בוצעה", BaseTransientBottomBar.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            }
        });

        Glide.with(getActivity()).load(ken.getAnimalImageUrl()).into(myKenImage);

        recycler.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recycler.setLayoutManager(mLayoutManager);

        myKenNameTv.setText(ken.getName());
        myKenPointsTV.setText(ken.getPoints() + "");

        return viewGroup;
    }
}
