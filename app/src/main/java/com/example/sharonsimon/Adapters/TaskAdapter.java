package com.example.sharonsimon.Adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.R;
import com.example.sharonsimon.Classes.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ronto on 18-Dec-18.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.CreateTaskViewHolder>
{

    private ArrayList<Task> tasks;
    private myTaskAdapterListener listener;
    private boolean checkBoxIsClickable = false;

    public interface myTaskAdapterListener
    {
        void onTaskClick(int position, View v);
        void onTaskLongClick(int position, View v);
        void onCheckBoxClick(int position, View v);
    }

    public void setListener(myTaskAdapterListener listener)
    {
        this.listener = listener;
    }

    public void setCheckBoxIsClickable(boolean isClickable){
        checkBoxIsClickable = isClickable;
    }

    public TaskAdapter(ArrayList<Task> tasks)
    {
        this.tasks = tasks;
        if(this.tasks == null)
            this.tasks = new ArrayList<>();
    }

    @Override
    public int getItemCount()
    {
        if(tasks == null) return 0;
        return tasks.size();
    }

    public class CreateTaskViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox isCompletedCB;
        TextView descTV;
        TextView pointsTV;
        ImageView starIv;

        public CreateTaskViewHolder(final View itemView) {
            super(itemView);

            isCompletedCB = itemView.findViewById(R.id.card_view_task_is_completed_cb);
            descTV = itemView.findViewById(R.id.card_view_task_desc_tv);
            pointsTV = itemView.findViewById(R.id.card_view_task_points_tv);
            starIv = itemView.findViewById(R.id.card_view_task_star_iv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onTaskClick(getAdapterPosition(), view);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null)
                        listener.onTaskLongClick(getAdapterPosition(), view);
                    return false;
                }
            });

            if (checkBoxIsClickable) {
                isCompletedCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onCheckBoxClick(getAdapterPosition(), view);
                    }
                });
            }
            else{
                isCompletedCB.setClickable(false);
            }
        }
    }

    @Override
    public CreateTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_tasks, parent, false);
        CreateTaskViewHolder createTaskViewHolder = new CreateTaskViewHolder(view);
        return createTaskViewHolder;
    }

    @Override
    public void onBindViewHolder(CreateTaskViewHolder holder, int position)
    {
        Task task = tasks.get(position);
        holder.isCompletedCB.setChecked(task.isCompleted());
        holder.descTV.setText(task.getDesc());
        holder.pointsTV.setText(task.getPoints() + "");

        if (task.isCompleted())
        {
            holder.starIv.setVisibility(View.INVISIBLE);
        }
        else
        {

            AnimationSet starAnimations = new AnimationSet(true);

            AlphaAnimation  blink= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            blink.setDuration(600); // duration
            blink.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            blink.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            blink.setRepeatMode(Animation.REVERSE);

            starAnimations.addAnimation(blink);
            holder.starIv.startAnimation(starAnimations);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(holder.starIv, "scaleX", 1.5f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(holder.starIv, "scaleY", 1.5f);
            scaleDownX.setDuration(600);
            scaleDownY.setDuration(600);

            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);

            scaleDown.start();

        }

    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
