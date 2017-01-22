package ghotioninabarrel.rgbgame;

/**
 * Created by Gil Posluns on 2016-01-12.
 */
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InfoFragment extends Fragment {
    protected TextView redView, greenView, blueView, scoreView, goalView, hscoreView, sizeView;
    protected int hscore;
    protected View thisView;

    public InfoFragment() {
    }

    public void onStart(){
        super.onStart();
        SharedPreferences pref = getActivity().getSharedPreferences("game", 0);
        hscore = pref.getInt("hscore", 0);
        hscoreView.setText ("Best:" + hscore);
    }

    public void onStop(){
        super.onStop();
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("game",  0).edit();
        editor.putInt("hscore", hscore);
        editor.commit();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        redView = (TextView)view.findViewById(R.id.redView);
        greenView = (TextView)view.findViewById(R.id.greenView);
        blueView = (TextView)view.findViewById(R.id.blueView);
        scoreView = (TextView)view.findViewById(R.id.scoreView);
        goalView = (TextView)view.findViewById(R.id.goalView);
        hscoreView = (TextView)view.findViewById(R.id.highScoreView);
        sizeView = (TextView)view.findViewById(R.id.sizeView);

        scoreView.setText ("Score:0");
        goalView.setText ("Goal:5");

        thisView = view;

        return view;
    }

    public void update (Tile tile, int score, int goal){
        update(tile);
        update(score, goal);
    }

    public void clearTile (){
        redView.setText("");
        redView.setVisibility (View.INVISIBLE);
        redView.invalidate();

        greenView.setText("");
        greenView.setVisibility (View.INVISIBLE);
        greenView.invalidate();

        blueView.setText("");
        blueView.setVisibility (View.INVISIBLE);
        blueView.invalidate();
    }

    public void update(Tile tile){
        int barSpace = (thisView.getMeasuredWidth() - sizeView.getMeasuredWidth())/2;
        //Log.d ("InfoFragment", "barspace="+barSpace);

        redView.setTextColor (Color.BLACK);
        greenView.setTextColor (Color.BLACK);
        blueView.setTextColor(Color.BLACK);

        redView.setText("" + tile.getR());
        RelativeLayout.LayoutParams rparams = (RelativeLayout.LayoutParams)redView.getLayoutParams(); //Why do I only need to do this for 1 view?
        rparams.width = barSpace*tile.getR()/tile.getMax();
        redView.setVisibility(View.VISIBLE);
        redView.setLayoutParams(rparams);
        redView.invalidate();
        //Log.d ("InfoFragment", "rwidth = " + redView.getLayoutParams().width);

        greenView.setText("" + tile.getG());
        greenView.getLayoutParams().width= barSpace*tile.getG()/tile.getMax();
        greenView.setVisibility(View.VISIBLE);
        greenView.invalidate();

        blueView.setText("" + tile.getB());
        blueView.getLayoutParams().width =barSpace*tile.getB()/tile.getMax();
        blueView.setVisibility(View.VISIBLE);
        blueView.invalidate();

        thisView.invalidate();
    }

    public void update(int score, int goal){
        scoreView.setText("Score:" + score);
        goalView.setText("Goal:" + goal);
        if (score > hscore){
            hscore = score;
            hscoreView.setText ("Best:" + hscore);
        }
    }

    public void update (int size){
        sizeView.setText("" + size);
    }

    public void update (int score, int goal, int size){
        update (score, goal);
        update (size);
    }

    public void update (Tile start, Tile end){
        int barSpace = (thisView.getMeasuredWidth() - sizeView.getMeasuredWidth())/2;

        if (start.getR() + end.getR() == start.getG() + end.getG() && start.getG() + end.getG() == start.getB() + end.getB()){
            redView.setTextColor (Color.WHITE);
            greenView.setTextColor (Color.WHITE);
            blueView.setTextColor (Color.WHITE);
        }

        redView.setText ("" + (start.getR() +  end.getR()));
        RelativeLayout.LayoutParams rparams = (RelativeLayout.LayoutParams)redView.getLayoutParams();
        rparams.width = barSpace*(start.getR() + end.getR())/start.getMax();
        redView.setVisibility(View.VISIBLE);
        redView.setLayoutParams (rparams);
        redView.invalidate();

        greenView.setText ("" + (start.getG() +  end.getG()));
        greenView.getLayoutParams().width = barSpace*(start.getG() + end.getG())/start.getMax();
        greenView.setVisibility(View.VISIBLE);
        greenView.invalidate();

        blueView.setText ("" + (start.getB() +  end.getB()));
        blueView.getLayoutParams().width = barSpace*(start.getB() + end.getB())/start.getMax();
        greenView.setVisibility(View.VISIBLE);
        blueView.invalidate();
    }
}

