package santhosh.healthpredictor.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import santhosh.healthpredictor.com.data.Constants;
import santhosh.healthpredictor.com.service.HealthPredictionService;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private Button btn_service, btn_status;
    private TextView tv_lat, tv_lng, tv_act;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.frag_home, container, false);

        btn_service = (Button) v.findViewById(R.id.btn_start_service);
        btn_service.setOnClickListener(this);
        btn_status = (Button) v.findViewById(R.id.btn_status_service);
        btn_status.setOnClickListener(this);
        if(HealthPredictionService.isRunning){
            btn_service.setText(R.string.stop_learning);
        }
        tv_lat = (TextView) v.findViewById(R.id.tv_latitude);
        tv_lng = (TextView) v.findViewById(R.id.tv_longitude);
        tv_act = (TextView) v.findViewById(R.id.tv_activity);
        return v;
    }

    public void handleReceiver(boolean bStart){
        if(bStart) {
            //add activity recognition action
            IntentFilter filter = new IntentFilter(Constants.ACTION_ACTIVITY_RECOGNITION);
            //adding location updates
            filter.addAction(Constants.ACTION_LOCATION);
            getActivity().registerReceiver(actRecognitionReceiver, filter);
        }else{
            getActivity().unregisterReceiver(actRecognitionReceiver);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_service:
                if(HealthPredictionService.isRunning){
                    getActivity().stopService(new Intent(getActivity(), HealthPredictionService.class));
                    btn_service.setText(R.string.start_learning);
                }else{
                    getActivity().startService(new Intent(getActivity(), HealthPredictionService.class));
                    btn_service.setText(R.string.stop_learning);
                    handleReceiver(true);
                }
                break;
            case R.id.btn_status_service:
                Toast.makeText(getActivity(),
                        "Service Running Status: "+HealthPredictionService.isRunning,
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //restart the receiver
        handleReceiver(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Stop receiver
        handleReceiver(false);
    }

    /**
     * Listener for Activity Recognition(from intent service)
     */
    private BroadcastReceiver actRecognitionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(Constants.RECOGNISE_ACTIVITY)) {
                //set activity recognition as activity (confidence)
                String type = intent.getStringExtra(Constants.RECOGNISE_ACTIVITY);
                int value = intent.getIntExtra(Constants.CONFIDENCE_ACTIVITY, 0);
                if(!TextUtils.isEmpty(type)){
                    tv_act.setText(type + " (" + value + ")");
                }
            }else if(intent.hasExtra(Constants.LOC_LATITUDE)){
                tv_lat.setText(String.format("%s", intent.getDoubleExtra(Constants.LOC_LATITUDE, 0)));
                tv_lng.setText(String.format("%s", intent.getDoubleExtra(Constants.LOC_LONGITUDE, 0)));
            }
        }
    };
}
