package com.example.jonny.projectapp;

/**
 * Created by Jonny on 20/02/2016.
 */

        import android.app.ProgressDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.CountDownTimer;
        import android.support.design.widget.Snackbar;
        import android.support.design.widget.CoordinatorLayout;
        import android.support.v4.app.Fragment;
        import android.support.v7.app.AppCompatActivity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.w3c.dom.Text;

        import java.util.HashMap;
        import java.util.Timer;
        import java.util.TimerTask;
        import java.util.logging.Handler;
        import java.util.logging.LogRecord;
// import info.androidhive.materialtabs.R;


public class TapFragment extends Fragment {

    int taps = 1;
    TextView textCounter;
    boolean timerStarted = false;
    Snackbar snackbar;

    CountDownTimer down = new CountDownTimer(5000, 1) {

        public void onTick(long millisUntilFinished) {
            textCounter.setText(millisUntilFinished / 1000 + ":" + (millisUntilFinished-((millisUntilFinished / 1000 )*1000)));
        }

        public void onFinish() {
            //textCounter.setText(String.valueOf(taps));
            //Toast.makeText(getActivity(),"You got X taps",Toast.LENGTH_SHORT).show();
            textCounter.setText("DONE");
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "You got " + taps + " taps", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taps = 1;
                    timerStarted = false;
                    textCounter.setText(getString(R.string.startTap));
                }
            });
            snackbar.show();
            testUpdate();

            // Send number of taps to activity so it can be uploaded with other test data
//            Intent intent = new Intent(getActivity().getBaseContext(), TestScreen.class);
//            intent.putExtra("taps", taps);
//            getActivity().startActivity(intent);

        }
    };

    public TapFragment() {
        // Required empty public constructor
    }

    public static TapFragment newInstance() {
        TapFragment fragment = new TapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tap_screen, container, false);

        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        textCounter = (TextView)view.findViewById(R.id.timerValue);

        ImageButton tapButton = (ImageButton) view.findViewById(R.id.tapTestButton);

        //Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "No images.", Snackbar.LENGTH_LONG);

        tapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerStarted == false) {
                    down.start();
                    timerStarted = true;
                }
                else {
                    taps++;
                    //Snackbar.make(getActivity().findViewById(android.R.id.content), "No images.", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        //TextView myAwesomeTextView = (TextView)view.findViewById(R.id.timerValue);
        //myAwesomeTextView.setText("My Awesome Text");


        //down.start();

        //t = new Timer();
        //t.scheduleAtFixedRate(timer, 0 , 1000);

        //return inflater.inflate(R.layout.fragment_tap_screen, container, false);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        //Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), "No images.", Snackbar.LENGTH_LONG);
    }

    private void testUpdate() {

        class testUpdate extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getActivity.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                //            Toast.makeText(Jum.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("taps",String.valueOf(taps));
                //            params.put("grip",grip);
                //params.put("FT", String.valueOf(flightTime));
                //params.put("CT", String.valueOf(contractionTime));

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest("http://ec2-52-91-226-96.compute-1.amazonaws.com/TapsUpdate.php", params);
                return res;
            }
        }
        testUpdate tu = new testUpdate();
        tu.execute();
    }

}