package Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.se302.photonest.R;

import DataModels.Hashtag;

import java.util.List;


public class HashListAdapter extends ArrayAdapter<Hashtag> {
    private Context mContext;
    private int layoutResource;

    public HashListAdapter(@NonNull Context context, int resource, @NonNull List<Hashtag> objects) {
        super(context, resource, objects);
        mContext = context;
        layoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(layoutResource, parent, false);
        TextView hashtags = convertView.findViewById(R.id.search_hashtag);
        ImageView photohash = convertView.findViewById(R.id.search_photo_hash);
        final Hashtag hashtag = getItem(position);
        photohash.setImageDrawable(mContext.getResources().getDrawable(R.drawable.hash_photo));
        hashtags.setText(hashtag.getHashTags());

        return convertView;
    }
}

