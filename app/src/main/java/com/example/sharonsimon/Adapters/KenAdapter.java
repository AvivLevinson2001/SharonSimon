package com.example.sharonsimon.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sharonsimon.R;
import com.example.sharonsimon.Classes.Ken;

import java.util.ArrayList;

/**
 * Created by ronto on 18-Dec-18.
 */

public class KenAdapter extends RecyclerView.Adapter<KenAdapter.CreateKenViewHolder>
{

    private ArrayList<Ken> kens;
    private myKenAdapterListener listener;
    Context context;

    public interface myKenAdapterListener
    {
        void onKenClick(int position, View v);
    }

    public void setListener(myKenAdapterListener listener)
    {
        this.listener = listener;
    }

    public KenAdapter(ArrayList<Ken> kens, Context context)
    {
        this.kens = kens;
        if(this.kens == null)
            this.kens = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getItemCount()
    {
        if(kens == null) return 0;
        return kens.size();
    }

    public class CreateKenViewHolder extends RecyclerView.ViewHolder
    {

        TextView nameTv;
        TextView pointsTv;
        ImageView kenImageIV;
        ProgressBar progressBar;
        LottieAnimationView animationView;
        RelativeLayout animationBackgroundRL;
        View separationView;

        public CreateKenViewHolder(final View itemView)
        {
            super(itemView);

            nameTv = itemView.findViewById(R.id.card_view_ken_name_tv);
            pointsTv = itemView.findViewById(R.id.card_view_ken_points_tv);
            kenImageIV = itemView.findViewById(R.id.card_view_ken_image);
            progressBar = itemView.findViewById(R.id.card_view_ken_image_progress);
            animationView = itemView.findViewById(R.id.fireworks_animation_view);
            animationBackgroundRL = itemView.findViewById(R.id.fireworks_animation_background_rl);
            separationView = itemView.findViewById(R.id.separation_view);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(listener != null)
                        listener.onKenClick(getAdapterPosition(), view);
                }
            });
        }
    }

    @Override
    public CreateKenViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_kens, parent, false);
        CreateKenViewHolder createKenViewHolder = new CreateKenViewHolder(view);
        return createKenViewHolder;
    }

    @Override
    public void onBindViewHolder(final CreateKenViewHolder holder, int position)
    {
        Ken ken = kens.get(position);
        holder.nameTv.setText(ken.getName());
        holder.pointsTv.setText(ken.getPoints() + "");
        if(kens.get(position).getAnimalImageUrl() != null && !kens.get(position).getAnimalImageUrl().equals(""))
            Glide.with(context).load(ken.getAnimalImageUrl()).listener(new RequestListener<Drawable>()
            {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)
                {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource)
                {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.kenImageIV.setVisibility(View.VISIBLE);
                    return false;
                }
            }).into(holder.kenImageIV);
        else{
            holder.kenImageIV.setImageResource(R.drawable.semel_hash);
        }
        if(position <= 2){
            if(position == 0)
                holder.animationBackgroundRL.setBackgroundColor(context.getResources().getColor(R.color.colorGold));
            else if(position == 1)
                holder.animationBackgroundRL.setBackgroundColor(context.getResources().getColor(R.color.colorSilver));
            else if(position == 2)
                holder.animationBackgroundRL.setBackgroundColor(context.getResources().getColor(R.color.colorBronze));
            holder.separationView.setVisibility(View.GONE);
            holder.animationView.setVisibility(View.VISIBLE);
            holder.animationView.setAnimation("fireworks_animation.json");
        }
        else{
            holder.animationBackgroundRL.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
            holder.separationView.setVisibility(View.VISIBLE);
            holder.animationView.setVisibility(View.GONE);
        }
    }

    public void setKens(ArrayList<Ken> kens) {
        this.kens = kens;
    }
}
