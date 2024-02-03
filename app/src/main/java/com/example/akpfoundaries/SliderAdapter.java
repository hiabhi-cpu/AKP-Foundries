package com.example.akpfoundaries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    // creating a variable for
    // context and array list.
    private int sliderItemChoice;
    private Context context;
    private List<SliderData> mSliderItems = new ArrayList<>();

    // constructor for our adapter class.
    public SliderAdapter(Context context, List<SliderData> mSliderItems,int sliderItemChoice) {
        this.context = context;
        this.mSliderItems = mSliderItems;
        this.sliderItemChoice = sliderItemChoice;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        // inside the on Create view holder method we are
        // inflating our layout file which we have created.
        View inflate;
        if(sliderItemChoice == 1){
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, null);
        }
        else{
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item2, null);
        }
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        // inside on bind view holder method we are
        // getting url of image from our modal class
        // and setting that url for image inside our
        // image view using Picasso.
        final SliderData sliderItem = mSliderItems.get(position);
        Glide.with(context).load(sliderItem.getUri()).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        // returning the size of our array list.
        return mSliderItems.size();
    }

    // view holder class for initializing our view holder.
    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        // variables for our view and image view.
        View itemView;
        ImageView imageView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            // initializing our views.
            if(sliderItemChoice == 1){
                imageView = itemView.findViewById(R.id.slider_imageView);
            }
            else{
                imageView = itemView.findViewById(R.id.slider_imageView2);
            }
            this.itemView = itemView;
        }
    }


}
