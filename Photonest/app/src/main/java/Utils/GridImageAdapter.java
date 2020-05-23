package Utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.se302.photonest.R;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<String> {

        private Context Context;
        private LayoutInflater Inflater;
        private int layoutResource;
        private String append1;
        private ArrayList<String> imgURLs;

        public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs) {
            super(context, layoutResource, imgURLs);
            Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Context = context;
            this.layoutResource = layoutResource;
            append1 = append;
            this.imgURLs = imgURLs;
        }

        private static class ViewHolder{
            SquareImageView image;
            ProgressBar progressBar;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            final ViewHolder holder;
            if(convertView == null){
                convertView = Inflater.inflate(layoutResource, parent, false);
                holder = new ViewHolder();
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressbar);
                holder.image = (SquareImageView) convertView.findViewById(R.id.gridImageView);

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            String imgURL = getItem(position);

            final ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(Context));

            imageLoader.displayImage(append1 + imgURL, holder.image, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    if(holder.progressBar != null){
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    if(holder.progressBar != null){
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if(holder.progressBar != null){
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    if(holder.progressBar != null){
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }
            });

            return convertView;
        }
    }

