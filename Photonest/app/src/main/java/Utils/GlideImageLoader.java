package Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.se302.photonest.R;

public class GlideImageLoader {



    public static void loadImageWithOutTransition(Context mContext, String imageUrl, ImageView image) {
        Glide.with(mContext)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.place_holder_photo))
                .into(image);
    }


}