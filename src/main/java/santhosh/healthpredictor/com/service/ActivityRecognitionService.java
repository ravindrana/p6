package santhosh.healthpredictor.com.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import santhosh.healthpredictor.com.data.Constants;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ActivityRecognitionService extends IntentService {

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }

    public ActivityRecognitionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleMostProbableActivity(result);
        }
    }

    /**
     * Process the most probable activity of the user.
     *
     * @param result
     */
    private void handleMostProbableActivity(ActivityRecognitionResult result) {

        // Get the most probable activity from the list of activities in the update
        DetectedActivity mostProbableActivity = result.getMostProbableActivity();

        // Get the confidence percentage for the most probable activity
        int confidence = mostProbableActivity.getConfidence();

        // Get the type of activity
        int activityType = mostProbableActivity.getType();

        //ToDo: Send info to health prediction data population server

        //send the info to UI
        String mode = getNameFromType(activityType);
        Log.d(Constants.TAG, "ActivityRecognizedService: mode: " + mode + " confidence: " + confidence);
        Intent locIntent = new Intent(Constants.ACTION_ACTIVITY_RECOGNITION);
        locIntent.putExtra(Constants.RECOGNISE_ACTIVITY, mode);
        locIntent.putExtra(Constants.CONFIDENCE_ACTIVITY, confidence);
        sendBroadcast(locIntent);
    }

    /**
     * Check ON_FOOT activity is either walking or running
     *
     * @param probableActivities
     * @return DetectedActivity
     */
    private DetectedActivity walkingOrRunning(List<DetectedActivity> probableActivities) {
        DetectedActivity myActivity = null;
        int confidence = 0;
        for (DetectedActivity activity : probableActivities) {
            if (activity.getType() != DetectedActivity.RUNNING && activity.getType() != DetectedActivity.WALKING) {
                continue;
            }

            if (activity.getConfidence() > confidence) {
                myActivity = activity;
            }
        }

        return myActivity;
    }

    /**
     * Map detected activity types to strings
     *
     * @param activityType The detected activity type
     * @return A user-readable name for the type
     */
    private String getNameFromType(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "IN_VEHICLE";
            case DetectedActivity.ON_BICYCLE:
                return "ON_BICYCLE";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            case DetectedActivity.WALKING:
                return "WALKING";
            case DetectedActivity.ON_FOOT:
                return "ON_FOOT";
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.TILTING:
                return "TILTING";
            default:
                return "UNKNOWN";
        }
    }
}
