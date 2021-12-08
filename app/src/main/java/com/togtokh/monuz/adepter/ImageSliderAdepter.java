package com.togtokh.monuz.adepter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.togtokh.monuz.WebView;
import com.togtokh.monuz.MovieDetails;
import com.togtokh.monuz.WebSeriesDetails;
import com.makeramen.roundedimageview.RoundedImageView;

import com.togtokh.monuz.R;
import com.togtokh.monuz.list.ImageSliderItem;

import java.util.List;

public class ImageSliderAdepter extends RecyclerView.Adapter<ImageSliderAdepter.SliderViewHolder> {

    private List<ImageSliderItem> slider_items;
    private ViewPager2 viewPager2;

    Context context;

    public ImageSliderAdepter(List<ImageSliderItem> slider_items, ViewPager2 viewPager2) {
        this.slider_items = slider_items;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slider_item_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(slider_items.get(position));
        if(position == slider_items.size() - 2) {
            viewPager2.post(runnable);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(slider_items.get(position).getContent_Type() == 0) {
                    Intent intent = new Intent(context, MovieDetails.class);
                    intent.putExtra("ID", slider_items.get(position).getContent_ID());
                    context.startActivity(intent);
                }else if(slider_items.get(position).getContent_Type() == 1) {
                    Intent intent = new Intent(context, WebSeriesDetails.class);
                    intent.putExtra("ID", slider_items.get(position).getContent_ID());
                    context.startActivity(intent);
                }else if(slider_items.get(position).getContent_Type() == 2) {
                    Intent intent = new Intent(context, WebView.class);
                    intent.putExtra("URL", slider_items.get(position).getURL());
                    context.startActivity(intent);

                }else if(slider_items.get(position).getContent_Type() == 3) {
                    String URL = slider_items.get(position).getURL();
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return slider_items.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imageView;
        private TextView ImageSlider_Text;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ImageSlider);
            ImageSlider_Text = itemView.findViewById(R.id.ImageSlider_Text);
        }

        void setImage(ImageSliderItem image_slider_item) {
            Glide.with(context)
                    .load(image_slider_item.getImage())
                    .placeholder(R.drawable.poster_placeholder)
                    .into(imageView);
            ImageSlider_Text.setText(image_slider_item.getTitle());
        }
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            slider_items.addAll(slider_items);
            notifyDataSetChanged();
        }
    };
}
