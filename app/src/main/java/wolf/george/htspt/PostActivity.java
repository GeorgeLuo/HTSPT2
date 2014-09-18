package wolf.george.htspt;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class PostActivity extends ActionBarActivity {
    private static final String TAG = "wolf.george.htspt";
    static final int NEW_POST = 23;
    private EditText postText;
    static ArrayList<String> spots;
    private MenuItem mSpinnerItem1 = null;
    static ArrayAdapter<String> ad1;
    private String feedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        spots = new ArrayList<String>();
        /*
        SharedPreferences prefs = getSharedPreferences("htspt_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("check_on_resume", "skip_create");
        editor.apply();
        String string = prefs.getString("check_on_resume",
                "oncreate fine");
        */
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("feedType")!= null)
        {
            if(!bundle.getString("currentSpot").equals("not_marked"))
            {
                spots.add(bundle.getString("currentSpot"));
                feedType = bundle.getString("currentSpot");
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_layout);
        postText = (EditText) findViewById(R.id.to_post);
        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#222326")));
        actionBar.setTitle("");
        spots.add("Main Feed");
        ad1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spots);



    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_menu, menu);
        spots = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spots);
        Spinner spinner = (Spinner)getActionView(R.id.spot_spinner);
        spinner.setAdapter(adapter);
        return true;
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.post_menu, menu);
        mSpinnerItem1 = menu.findItem( R.id.spot_spinner);
        View view1 = mSpinnerItem1.getActionView();
        if (view1 instanceof Spinner)
        {
            final Spinner spinner = (Spinner) view1;
            spinner.setAdapter(ad1);


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int pos, long id) {
                    feedType = (String)parent.getItemAtPosition(pos);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.post_up) {
            String post = postText.getText().toString();
            Log.d("TO POST: ", post);
            Intent data = new Intent();
            data.putExtra("post_data", post);
            data.putExtra("what_feed", feedType);
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
