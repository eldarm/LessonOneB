package com.eldar.lessononeb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

/**
 * Created by EldarM on 10/21/2014.
 */
public class DatesList extends ActionBarActivity {
    private Timer timer;
    private final Handler timeHandler = new Handler();

    //Fragment that displays the meme.
    public static class DatesListFragment extends Fragment {

        @Override
        public View onCreateView(
                LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_dates_list,
                    container, false);
        }
    }

    private ItemAdapter itemAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dates_list);
        ListView listView = (ListView)findViewById(R.id.listView);
        itemAdapter = new ItemAdapter(this);
        listView.setAdapter(itemAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dates_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        itemAdapter.notifyDataSetChanged();
                    }
                });
            }
        },500, 1000 );
    }

    @Override
    protected void onPause(){
        super.onPause();
        timer.cancel();
    }
}
