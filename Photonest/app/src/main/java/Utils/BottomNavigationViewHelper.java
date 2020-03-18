package Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.se302.photonest.MainActivity;
import com.se302.photonest.NotificationActivity;
import com.se302.photonest.PostActivity;
import com.se302.photonest.ProfileActivity;
import com.se302.photonest.R;
import com.se302.photonest.SearchActivity;

public class BottomNavigationViewHelper {


    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    //Goes to feed. ACTIVITY_NUM = 0
                    case R.id.ic_feed:
                        Intent intent1 = new Intent(context, MainActivity.class);
                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    //Goes to search. ACTIVITY_NUM = 1
                    case R.id.ic_search:
                        Intent intent2  = new Intent(context, SearchActivity.class);
                        context.startActivity(intent2);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    //Goes to add post. ACTIVITY_NUM = 2. Nav bar should disappear.
                    case R.id.ic_addPost:
                        Intent intent3 = new Intent(context, PostActivity.class);
                        context.startActivity(intent3);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    //Goes to notifications. ACTIVITY_NUM = 3
                    case R.id.ic_notification:
                        Intent intent4 = new Intent(context, NotificationActivity.class);
                        context.startActivity(intent4);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    //Goes to profile. ACTIVITY_NUM = 4
                    case R.id.ic_profile:
                        Intent intent5 = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent5);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                }


                return false;
            }
        });
    }
}
