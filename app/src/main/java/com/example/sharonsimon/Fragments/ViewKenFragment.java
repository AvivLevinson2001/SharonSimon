package com.example.sharonsimon.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.example.sharonsimon.Activities.MainActivity;
import com.example.sharonsimon.Adapters.TaskAdapter;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.Dialogs.TrophyAnimationDialog;
import com.example.sharonsimon.Interfaces.FirebaseChangesListener;
import com.example.sharonsimon.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ViewKenFragment extends Fragment {

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    Ken ken;
    boolean isAdmin;

    CircleImageView myKenImage;
    TextView myKenPointsTV;
    TextView myKenNameTv;
    RecyclerView recycler;
    TaskAdapter adapter;

    FirebaseChangesListener firebaseChangesListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity)context;
        try{
            firebaseChangesListener = (FirebaseChangesListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("Activity: " + activity.toString() + " must implement FirebaseChangesListener and ViewKenFragmentInterface");
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

        if(ken == null){
            Snackbar.make(container, "משהו השתבש", BaseTransientBottomBar.LENGTH_LONG).show();
            return viewGroup;
        }

        myKenImage = viewGroup.findViewById(R.id.my_ken_image);
        myKenPointsTV = viewGroup.findViewById(R.id.my_ken_points_tv);
        myKenNameTv = viewGroup.findViewById(R.id.my_ken_name_tv);
        recycler = viewGroup.findViewById(R.id.tasks_rv);

        if(ken.getTasks() == null) ken.setTasks(new ArrayList<Task>());
        adapter = new TaskAdapter(ken.getTasks());

        adapter.setListener(new TaskAdapter.myTaskAdapterListener() {
            @Override
            public void onTaskClick(int position, View v) {
                if(!isAdmin && getActivity().getSharedPreferences("user",MODE_PRIVATE).getString("ken","").equals(ken.getName())){
                    if(ken.getTasks().get(position).isCompleted()){
                        Snackbar.make(container,"המשימה בוצעה!", BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                    else{
                        Snackbar.make(container,"סיימתם את המשימה? שלחו סרטון שלכם מבצעים את המשימה לקומונר/ית", BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onTaskLongClick(final int position, View v) {
                if(isAdmin){
                    PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.task_long_click_menu_admin, popupMenu.getMenu());
                    popupMenu.getMenu().setGroupVisible(ken.getTasks().get(position).isCompleted() ? R.id.action_set_task_is_not_completed_group : R.id.action_set_task_is_completed_group, true);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Task task = ken.getTasks().get(position);
                            switch (menuItem.getItemId()){
                                case R.id.action_set_task_is_completed:
                                    task.setCompleted(true);
                                    ken.setPoints(ken.getPoints() + task.getPoints());
                                    ken.sortTasks();
                                    final int movedToPosition1 = ken.getTasks().indexOf(task);
                                    firebaseChangesListener.saveKenToFirebase(ken);
                                    myKenPointsTV.setText(ken.getPoints() + "");
                                    adapter.setTasks(ken.getTasks());
                                    adapter.notifyItemMoved(position,movedToPosition1);
                                    adapter.notifyItemChanged(movedToPosition1);
                                    break;
                                case R.id.action_set_task_is_not_completed:
                                    task.setCompleted(false);
                                    ken.setPoints(ken.getPoints() - task.getPoints());
                                    ken.sortTasks();
                                    final int movedToPosition2 = ken.getTasks().indexOf(task);
                                    firebaseChangesListener.saveKenToFirebase(ken);
                                    myKenPointsTV.setText(ken.getPoints() + "");
                                    adapter.setTasks(ken.getTasks());
                                    adapter.notifyItemMoved(position,movedToPosition2);
                                    adapter.notifyItemChanged(movedToPosition2);
                                    break;
                                case R.id.action_add_task_to_highlights:
                                    if(ken.getTasks().get(position).isCompleted()) {
                                        firebaseChangesListener.addTaskToHighlights(ken.getTasks().get(position).getDesc(), ken.getName());
                                    }
                                    else{
                                        Snackbar.make(container,"המשימה לא בוצעה", BaseTransientBottomBar.LENGTH_LONG).show();
                                    }
                                    break;
                            }
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

        SharedPreferences sp = getActivity().getSharedPreferences("user",MODE_PRIVATE);
        int oldPoints = sp.getInt("kenPoints", -1); //  get the points of the last login to the ken, -1 is for first login to the ken

        if(sp.getString("ken","").equals(ken.getName())) { // if im viewing my ken
            if (oldPoints != -1 && ken.getPoints() > oldPoints) { // if the old points is not -1 (this is not first login to the ken) and current points is bigger than old points, show animation

                final TrophyAnimationDialog dialog = new TrophyAnimationDialog();
                dialog.show(getActivity().getSupportFragmentManager(),"TrophyAnimationDialog");

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                },5000);
            }
            sp.edit().putInt("kenPoints", ken.getPoints()).apply();
        }

        return viewGroup;
    }
}
