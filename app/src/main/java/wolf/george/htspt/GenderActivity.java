package wolf.george.htspt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class GenderActivity extends Activity implements View.OnTouchListener {

    private String gender;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.gender_activity_activity);
        ImageView iv = (ImageView) findViewById (R.id.image);
        if (iv != null) {
            iv.setOnTouchListener (this);
        }
    }

    @Override
    public boolean onTouch (View v, MotionEvent ev) {

        boolean handledHere = false;
        final int action = ev.getAction();
        final int evX = (int) ev.getX();
        final int evY = (int) ev.getY();
        int nextImage = -1;
        ImageView imageView = (ImageView) v.findViewById (R.id.image);
        if (imageView == null) return false;

        Integer tagNum = (Integer) imageView.getTag ();
        int currentResource = (tagNum == null) ? R.drawable.combine : tagNum.intValue ();

        switch (action) {
            case MotionEvent.ACTION_DOWN :
                if (currentResource == R.drawable.combine) {
                    //nextImage = R.drawable.p2_ship_pressed;
                    handledHere = true;
                }
                break;
            case MotionEvent.ACTION_UP :
                // On the UP, we do the click action.
                // The hidden image (image_areas) has three different hotspots on it.
                // The colors are red, blue, and yellow.
                // Use image_areas to determine which region the user touched.
                // (2)
                int touchColor = getHotspotColor (R.id.image_areas, evX, evY);
                // Compare the touchColor to the expected values.
                // Switch to a different image, depending on what color was touched.
                // Note that we use a Color Tool object to test whether the
                // observed color is close enough to the real color to
                // count as a match. We do this because colors on the screen do
                // not match the map exactly because of scaling and
                // varying pixel density.
                ColorTool ct = new ColorTool();
                int tolerance = 5;
                //nextImage = R.drawable.p2_ship_default;
                // (3)
                if (ct.closeMatch (Color.parseColor("#0000ff"), touchColor, tolerance)) {
                    // Do the action associated with the RED region
                    //nextImage = R.drawable.p2_ship_alien;
                    Log.d("Clicked", "Male was clicked");
                    prefs = getSharedPreferences("SharedPreferences",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("gender_is", "male");
                    editor.apply();
                    Intent nextPage = new Intent(GenderActivity.this, MainActivity.class);
                    GenderActivity.this.startActivity(nextPage);
                    finish();
                }
                if (ct.closeMatch (Color.parseColor("#e4ff00"), touchColor, tolerance)) {
                    // Do the action associated with the RED region
                    //nextImage = R.drawable.p2_ship_alien;
                    Log.d("Clicked", "Female was clicked");
                    prefs = getSharedPreferences("SharedPreferences",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("gender_is", "female");
                    editor.apply();
                    Intent nextPage = new Intent(GenderActivity.this, MainActivity.class);
                    GenderActivity.this.startActivity(nextPage);
                    finish();
                } else {
                    //...
                }
                break;
        }
        return true;
    }

    public int getHotspotColor (int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById (hotspotId);
        img.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
        img.setDrawingCacheEnabled(false);
        return hotspots.getPixel(x, y);
    }

    public boolean closeMatch (int color1, int color2, int tolerance) {
        if ((int) Math.abs (Color.red(color1) - Color.red (color2)) > tolerance )
            return false;
        if ((int) Math.abs (Color.green (color1) - Color.green (color2)) > tolerance )
            return false;
        if ((int) Math.abs (Color.blue (color1) - Color.blue(color2)) > tolerance )
            return false;
        return true;
    }

}
