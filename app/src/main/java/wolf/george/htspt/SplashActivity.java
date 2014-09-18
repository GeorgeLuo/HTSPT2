package wolf.george.htspt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends Activity {
    SharedPreferences prefs;
    private Intent nextPage;
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                prefs = getSharedPreferences("SharedPreferences",
                        MODE_PRIVATE);
                int newid = prefs.getInt("Login_ID",
                        10000000);
                Log.d("Login_ID", Integer.toString(newid));

                if(newid == 10000000)
                {
                    nextPage = new Intent(SplashActivity.this, GenderActivity.class);
                    SplashActivity.this.startActivity(nextPage);

                }
                else
                {
                    nextPage = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(nextPage);
                }

                /*
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("check_on_resume", "first_time");
                editor.apply();
                */
                SplashActivity.this.startActivity(nextPage);
                finish();

            }
        }, SPLASH_TIME_OUT);
        /*

        prefs = getSharedPreferences(GENDER_PREF,
                MODE_PRIVATE);
        String string = prefs.getString("FIRST_LOGIN?",
                "first_time");
        if(string.equals("first_time"))
        {
            nextPage = new Intent(SplashActivity.this, GenderActivity.class);
            SplashActivity.this.startActivity(nextPage);

        }
        else
        {
            nextPage = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(nextPage);
        }


        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("check_on_resume", "first_time");
        editor.apply();

        */
    }

}
