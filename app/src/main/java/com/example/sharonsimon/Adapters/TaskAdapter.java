package com.example.sharonsimon.Adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sharonsimon.R;
import com.example.sharonsimon.Classes.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by ronto on 18-Dec-18.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.CreateTaskViewHolder>
{

    private boolean isFirstOnBind = true;
    private ArrayList<Task> tasks;
    private myTaskAdapterListener listener;

    public interface myTaskAdapterListener
    {
        void onTaskClick(int position, View v);
        void onTaskLongClick(int position, View v);
        void firstOnBindCompleted(CreateTaskViewHolder holder);
    }

    public void setListener(myTaskAdapterListener listener)
    {
        this.listener = listener;
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
        LinearLayout titleLL;
        TextView descTV;
        TextView pointsTV;
        ImageView doneIV;

        public CreateTaskViewHolder(final View itemView) {
            super(itemView);

            titleLL = itemView.findViewById(R.id.card_view_task_title_ll);
            descTV = itemView.findViewById(R.id.card_view_task_desc_tv);
            pointsTV = itemView.findViewById(R.id.card_view_task_points_tv);
            doneIV = itemView.findViewById(R.id.card_view_task_done_iv);

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
    public void onBindViewHolder(final CreateTaskViewHolder holder, int position)
    {
        Task task = tasks.get(position);
        holder.titleLL.setBackgroundResource(task.isCompleted() ? R.drawable.task_background_completed : R.drawable.task_background_not_completed);
        holder.descTV.setText(task.getDesc());
        holder.pointsTV.setText(task.getPoints() + "");

        if (!task.isCompleted())
        {
            holder.doneIV.setVisibility(View.INVISIBLE);
        }
        else
        {
            AnimationSet starAnimations = new AnimationSet(true);

            AlphaAnimation  blink= new AlphaAnimation(0, 1); // Change alpha from fully visible to invisible
            blink.setDuration(600); // duration
            blink.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            blink.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            blink.setRepeatMode(Animation.REVERSE);

            starAnimations.addAnimation(blink);
            holder.doneIV.startAnimation(starAnimations);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(holder.doneIV, "scaleX", 1.5f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(holder.doneIV, "scaleY", 1.5f);
            scaleDownX.setDuration(600);
            scaleDownY.setDuration(600);

            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);

            scaleDown.start();
        }

        if (isFirstOnBind)
        {

            listener.firstOnBindCompleted(holder);
            isFirstOnBind = false;
        }
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

}
