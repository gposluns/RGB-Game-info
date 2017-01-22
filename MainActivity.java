package ghotioninabarrel.rgbgame;

/**
 * Created by Gil Posluns on 2016-01-12.
 */
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class MainActivity extends ActionBarActivity implements GameFragment.App, SensorEventListener{
    public static final String TEST_DEVICE_ID = "0675C1AF0A8CD58F49363BCDE78D6E4E";
    public static int uiMode = 1;
    protected InfoFragment info;
    protected SensorManager sManager;
    protected Sensor acc;
    protected Sensor gyr;
    protected GameFragment game;
    public static boolean hasGyroScope;
    protected float prevy = 0;
    protected boolean ignoreNextTurn = false;
    protected Timer timer;
    protected InterstitialAd ad;
    protected GestureDetector detector;
    private float distx, disty;

    protected void onStart (){
        super.onStart();
        sManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        acc = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyr = sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        hasGyroScope = gyr != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = (InfoFragment)getFragmentManager().findFragmentById(R.id.info);
        game = (GameFragment)getFragmentManager().findFragmentById(R.id.game);
        if (uiMode == 1){
            detector = new GestureDetector(this, new GestureListener());
        }

        getSupportActionBar().setLogo(R.drawable.launcher_icon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        ad = new InterstitialAd(this);
        ad.setAdUnitId (getString(R.string.interstitial_ad_id));
        ad.loadAd(new AdRequest.Builder().addTestDevice(TEST_DEVICE_ID).build());
    }

    protected void onResume(){
        super.onResume();
        timer = new Timer(true);
        if (uiMode == 1) return;
        sManager.registerListener(this, acc, 1000000);
        if (hasGyroScope){
            sManager.registerListener(this, gyr, 1000000);
        }
    }

    protected void onPause(){
        super.onPause();
        timer.cancel();
        if (uiMode == 1) return;
        sManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            //case R.id.action_settings :
            //return true;
            case R.id.action_help :
                startActivity(new Intent(this, InstructionsActivity.class));
                return true;
            case R.id.action_restart :
                if (ad.isLoaded()){
                    ad.show();
                    ad.loadAd(new AdRequest.Builder().addTestDevice(TEST_DEVICE_ID).build());
                }
                game.restart();
                info.update(0,5,1);
                info.clearTile();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateInfo (Tile tile, int score, int goal){
        info.update(tile, score, goal);
    }

    public void updateInfo (Tile tile){
        info.update(tile);
    }

    public void updateInfo (int score, int goal){
        info.update(score, goal);
    }

    public void updateInfo (Tile start, Tile end){
        info.update (start, end);
    }

    public void clearTile (){
        info.clearTile();
    }

    public void onSensorChanged (SensorEvent e){
        if (uiMode == 1) return;
        //Log.d("MainActivity", Arrays.toString(e.values));
        if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER && Math.abs(e.values[0]) >= 2){
            game.scrollX((int)e.values[0]/2);
        }
        if (hasGyroScope){
            if (e.sensor.getType() == Sensor.TYPE_GYROSCOPE){
                //Log.d("MainActivity", Arrays.toString(e.values));
                if (!ignoreNextTurn && game.scrollY(-(int)e.values[1])){
                    ignoreNextTurn = true;
                    timer.schedule(new TimerTask(){
                        public void run(){
                            ignoreNextTurn = false;
                        }}, 1000);
                }
            }
        }
        else{
            float dy = e.values[1] - prevy;
            prevy = e.values[1];
            //Log.d("MainActivity", "dy="+dy);
            if (Math.abs(dy) > 1){
                if (!ignoreNextTurn && game.scrollY(-(int)dy)){
                    ignoreNextTurn = true;
                    timer.schedule(new TimerTask(){
                        public void run(){
                            ignoreNextTurn = false;
                        }}, 1000);
                }
            }
        }
    }

    public void onAccuracyChanged (Sensor s, int a){

    }

    protected void onStop(){
        super.onStop();
        game.save();
    }

    public void updateInfo (int size){
        info.update(size);
    }

    public void updateInfo (int score, int goal, int size){
        info.update (score, goal, size);
    }

    public boolean onTouchEvent (MotionEvent m){
        if (uiMode == 1) detector.onTouchEvent(m);
        if (m.getActionMasked() == MotionEvent.ACTION_UP){
            ignoreNextTurn = true;
        }
        return super.onTouchEvent (m);
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        public boolean onDown (MotionEvent m){
            return true;
        }

        public boolean onScroll (MotionEvent m1, MotionEvent m2, float dx, float dy){
            distx += dx;
            disty += dy;
            //Log.d("GameFragment.Gesturelistener","Scroll:" + distx + " " + disty);
            if (uiMode == 1 && !ignoreNextTurn){
                game.deselectStart();
                if (Math.abs(distx) > 15) {
                    game.scrollX ((int)dx/15);
                    distx -= distx/15;
                }
                if (Math.abs(disty) > 15) {
                    game.scrollY ((int)dy/15);
                    disty -= disty/15;
                }
            }
            else if (ignoreNextTurn){
                ignoreNextTurn = false;
            }
            return true;
        }
    }
}

