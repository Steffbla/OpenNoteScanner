package com.todobom.opennotescanner.helpers;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todobom.opennotescanner.R;

import java.util.ArrayList;

// https://blog.mindorks.com/exploring-android-view-pager2-in-android
public class ImageSwipeAdapter extends RecyclerView.Adapter<ImageSwipeAdapter.ImageViewHolder> {

    private static final String TAG = "ImageSwipeAdapter";
    OnImageSwipeListener listener;
    int reversePos;
    private ArrayList<String> fileUris;

    public ImageSwipeAdapter(OnImageSwipeListener listener, ArrayList<String> fileUris) {
        Log.d(TAG, "ImageSwipeAdapter: init");
        this.listener = listener;
        this.fileUris = fileUris;
//        Collections.reverse(this.fileUris);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_page, parent, false);
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_page, parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        reversePos = getItemCount() - position - 1;
        holder.setImageView(fileUris.get(reversePos));
//        listener.onSwipeImage(reversePos);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ImageViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        listener.onSwipeImage(getItemCount() - holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return fileUris.size();
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.preview_image);
        }

        public void setImageView(String fileUri) {
            imageView.setImageURI(Uri.parse(fileUri));
        }
    }
}
