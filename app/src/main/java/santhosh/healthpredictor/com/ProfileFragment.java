package santhosh.healthpredictor.com;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import santhosh.healthpredictor.com.data.Constants;
import santhosh.healthpredictor.com.data.MyFireBaseDB;
import santhosh.healthpredictor.com.data.UserProfile;


public class ProfileFragment extends Fragment{

    private UserProfile mUserProfile;
    private AutoCompleteTextView mRaceView;
    private AutoCompleteTextView mFoodHabitView;
    private AutoCompleteTextView mFoodSupplementView;
    private AutoCompleteTextView mExerciseView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_profile, container, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.profile));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        mRaceView = setAdapter(v, R.id.profile_race, R.array.race_array);
        mRaceView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mRaceView.isPerformingCompletion() && s.toString().equalsIgnoreCase("European")) {
                    showSubRaceDlg();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mFoodHabitView = setAdapter(v, R.id.profile_food, R.array.food_habit_array);
        mFoodSupplementView = setAdapter(v, R.id.profile_supplement, R.array.food_supplement_array);
        mExerciseView = setAdapter(v, R.id.profile_excercise, R.array.exercise_array);

        //save button
        FloatingActionButton fab_save = (FloatingActionButton) v.findViewById(R.id.fab_save_profile);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        //create user profile instance
        mUserProfile = new UserProfile();

        //stub
        mRaceView.setText("Swedish");
        mFoodHabitView.setText("LCHF");
        mFoodSupplementView.setText("Vitamin D3");
        mExerciseView.setText("Yoga");
        return v;
    }

    private AutoCompleteTextView setAdapter(View v, int id, int arrayId) {
        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView = (AutoCompleteTextView) v.findViewById(id);
        // Get the string array
        String[] strArr = getResources().getStringArray(arrayId);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, strArr);
        textView.setAdapter(adapter);

        return textView;
    }

    private void saveUserProfile() {
        String race = mRaceView.getText().toString();
        String habit = mFoodHabitView.getText().toString();
        String supplement = mFoodSupplementView.getText().toString();
        String exercise = mExerciseView.getText().toString();

        //check if any field is empty
        if(TextUtils.isEmpty(race) || TextUtils.isEmpty(habit)
                || TextUtils.isEmpty(supplement) || TextUtils.isEmpty(exercise)){
            Toast.makeText(getActivity(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
        }else{
            mUserProfile.setRace(race);
            mUserProfile.setFoodHabit(habit);
            mUserProfile.setFoodSupplement(supplement);
            mUserProfile.setFoodExercise(exercise);

            Log.d(Constants.TAG, "UserProfile: "+mUserProfile);
            MyFireBaseDB.getInstance().saveUserProfile(mUserProfile, new MyFireBaseDB.OnCallback() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(getActivity(), "UserProfile saved successfully.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    private void showSubRaceDlg(){
        final CharSequence[] items = { "Scandinavian", "Mediterranean" };
        final int[] inputSelection = {-1};

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    paramDialogInterface.dismiss();
                    mUserProfile.setSubRace(items[inputSelection[0]].toString());
                }
            };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Sub-Race");
        builder.setSingleChoiceItems(items, inputSelection[0],
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        inputSelection[0] = item;
                    }
                });
        builder.setPositiveButton("Ok", listener);
        builder.setNegativeButton("Cancel", listener);
        builder.show();
    }
}
