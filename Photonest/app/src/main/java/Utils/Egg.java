package Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class Egg {
    private static final String TAG = "Egg";

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    public ImageView unlikedEgg, likedEgg;

    public Egg(ImageView unlikedEgg, ImageView likedEgg) {
        this.unlikedEgg = unlikedEgg;
        this.likedEgg = likedEgg;
    }

    public Egg() {

    }


    public void toggleLike(ImageView unlikedEgg, ImageView likedEgg){
        Log.d(TAG, "toggleLike: toggling Egg.");

        AnimatorSet animationSet =  new AnimatorSet();


        if(likedEgg.getVisibility() == View.VISIBLE){
            Log.d(TAG, "toggleLike: toggling red heart off.");
            likedEgg.setScaleX(0.1f);
            likedEgg.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(likedEgg, "scaleY", 1f, 0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(likedEgg, "scaleX", 1f, 0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

            likedEgg.setVisibility(View.GONE);
            unlikedEgg.setVisibility(View.VISIBLE);

            animationSet.playTogether(scaleDownY, scaleDownX);
        }

        else if(likedEgg.getVisibility() == View.GONE){
            Log.d(TAG, "toggleLike: toggling red heart on.");
            likedEgg.setScaleX(0.1f);
            likedEgg.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(likedEgg, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(likedEgg, "scaleX", 0.1f, 1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECCELERATE_INTERPOLATOR);

            likedEgg.setVisibility(View.VISIBLE);
            unlikedEgg.setVisibility(View.GONE);

            animationSet.playTogether(scaleDownY, scaleDownX);
        }

        animationSet.start();

    }
}
