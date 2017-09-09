package santhosh.healthpredictor.com.data;

/**
 * East Sweden Hack 2017.
 */


public class UserProfile {
    private String mName;
    private String mRace;
    private String mSubRace;
    private String mFoodHabit;
    private String mFoodSupplement;
    private String mFoodExercise;

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setRace(String mRace) {
        this.mRace = mRace;
    }

    public void setSubRace(String mSubRace) {
        this.mSubRace = mSubRace;
    }

    public void setFoodHabit(String mFoodHabit) {
        this.mFoodHabit = mFoodHabit;
    }

    public void setFoodExercise(String mFoodExcercise) {
        this.mFoodExercise = mFoodExcercise;
    }

    public void setFoodSupplement(String mFoodSupplement) {
        this.mFoodSupplement = mFoodSupplement;
    }

    public String getFoodExercise() {
        return mFoodExercise;
    }

    public String getFoodHabit() {
        return mFoodHabit;
    }

    public String getFoodSupplement() {
        return mFoodSupplement;
    }

    public String getName() {
        return mName;
    }

    public String getRace() {
        return mRace;
    }

    public String getSubRace() {
        return mSubRace;
    }

    @Override
    public String toString() {
        String text = "UserProfile: name="+mName+", habit="+mFoodHabit
                +", supplement="+mFoodSupplement+", race="+mRace+", sub-race="+mSubRace
                +", exercise="+mFoodExercise;
        return text;
    }
}
