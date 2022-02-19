package com.darwin.fastclicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MY_LOG";
    private RewardedAd mRewardedAd;
    private long backPressedTime;
    CountDownTimer timer;
    ImageView circle;
    ImageView setting_image;
    ImageView rating_image;
    TextView start_tv;
    TextView score_tv;
    TextView score_tv2;
    TextView bestScore_tv;
    TextView bestScore_tv2;
    TextView delay_tv;
    int click = 0;
    long time = 100 * 1000 * 60;
    int reward = 50;
    ConstraintLayout layout;
    LinearLayout linearLayout;
    FragmentContainerView fragmentContainerView;
    MediaPlayer mp_long;
    MediaPlayer mp_short;
    MediaPlayer mp_final;
    AudioManager amanager;
    Context context;
    private GoogleSignInClient mGoogleSignInClient;
    private LeaderboardsClient mLeaderboardsClient;
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

        context = this;
        layout =  findViewById(R.id.layout);
        start_tv = findViewById(R.id.start_tv);
        circle = findViewById(R.id.circle);
        score_tv = findViewById(R.id.score_tv);
        score_tv2 = findViewById(R.id.score_tv2);
        bestScore_tv = findViewById(R.id.bestScore_tv);
        bestScore_tv2 = findViewById(R.id.bestScore_tv2);
        delay_tv = findViewById(R.id.delay_tv);
        setting_image = findViewById(R.id.settings_image);
        rating_image = findViewById(R.id.rating_image);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        linearLayout = findViewById(R.id.linearLayout);

        score_tv.setVisibility(View.INVISIBLE);
        score_tv2.setVisibility(View.INVISIBLE);
        bestScore_tv.setVisibility(View.VISIBLE);
        bestScore_tv2.setVisibility(View.VISIBLE);
        start_tv.setVisibility(View.VISIBLE);
        start_tv.setClickable(true);
        delay_tv.setVisibility(View.INVISIBLE);
        circle.setVisibility(View.INVISIBLE);
        mp_final = MediaPlayer.create(context, R.raw.glass_sound);
        amanager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setNormalCircleSize();
        setPrefs();
        fragmentContainerView.setVisibility(View.INVISIBLE);
        mp_long = MediaPlayer.create(context, R.raw.ball_sound);
        setTheme(Preferences.getTheme(this));

        //
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                        loadAd();
                    }
                });
//
        start_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click = 0;
                setNormalCircleSize();
                startGame();
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onZoomOut();
            }
        });

        setting_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingsClicked();
            }
        });

        rating_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRatingClicked();
            }
        });
    }

    public void startGame(){
        score_tv.setVisibility(View.INVISIBLE);
        score_tv2.setVisibility(View.VISIBLE);
        start_tv.setVisibility(View.INVISIBLE);
        bestScore_tv.setVisibility(View.INVISIBLE);
        bestScore_tv2.setVisibility(View.INVISIBLE);
        circle.setVisibility(View.VISIBLE);
        fragmentContainerView.setVisibility(View.INVISIBLE);
        setting_image.setVisibility(View.INVISIBLE);
        rating_image.setVisibility(View.INVISIBLE);

        score_tv2.setText("0");
        layout.setClickable(true);
        layout.setVisibility(View.VISIBLE);
        mp_short.start();
        deleteBrokenScreen();
        onZoomIn();
    }

    public void onZoomIn(){
        Log.i(TAG, "onZoomIn started... ");
       timer = new CountDownTimer(time, 1) {
            @Override
            public void onTick(long l) {
                if (isDone(screenWidth(), circleWidth())) {
                    timer.cancel();
                    showResult();
                } else {
                    int height = circle.getLayoutParams().height += 5;
                    int width = circle.getLayoutParams().width += 5;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                    circle.setLayoutParams(params);
                }
            }

            @Override
            public void onFinish() {

            }

        }; timer.start();
    }

    public void onZoomOut(){
        click+=1;
        score_tv2.setText(click + "");
        circle.requestLayout();
        int height = circle.getLayoutParams().height -= 25;
        int width = circle.getLayoutParams().width -= 25;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        circle.setLayoutParams(params);
        circle.requestLayout();
    }

    public float screenWidth(){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        Log.i(TAG, "screen width = " + dpWidth);
        Log.i(TAG, "circle width = " + circleWidth());
       return dpWidth;
    }

    public float circleWidth(){
        return circle.getLayoutParams().width;
    }

    public boolean isDone(float screenWidth, float circleWidth){
        if(Preferences.getTheme(this).equals("space")){
            return circleWidth() >= screenWidth * 4;
        } else {
            return circleWidth() >= screenWidth * 1.3;
        }
    }

    public void showResult(){
        mp_final.start();
        mp_short.stop();
        setBrokenScreen();
        start_tv.setVisibility(View.VISIBLE);
        layout.setClickable(false);
        score_tv.setVisibility(View.VISIBLE);
        score_tv2.setVisibility(View.VISIBLE);
        bestScore_tv.setVisibility(View.VISIBLE);
        bestScore_tv2.setVisibility(View.VISIBLE);
        setting_image.setVisibility(View.VISIBLE);
        rating_image.setVisibility(View.VISIBLE);
        blockScreen();
        if ((Integer.parseInt(score_tv2.getText().toString())) >=  Integer
                .parseInt(Preferences.getRecord(this))) {
            savePrefs(score_tv2.getText().toString());
            Toast.makeText(this, getString(R.string.record), Toast.LENGTH_SHORT).show();
        }
        setPrefs();
    }

    public void blockScreen(){
        CountDownTimer timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {
                start_tv.setClickable(false);
                delay_tv.setVisibility(View.VISIBLE);
                delay_tv.setText((l/1000) + "");
            }

            @Override
            public void onFinish() {
                start_tv.setClickable(true);
                delay_tv.setVisibility(View.INVISIBLE);
            }
        }; timer.start();
    }

    public void setNormalCircleSize(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(5, 5);
        circle.setLayoutParams(params);
    }

    public void setBrokenScreen(){
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            linearLayout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.screen) );
        } else {
            linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.screen));
        }
    }

    public void deleteBrokenScreen(){
       linearLayout.setBackgroundResource(0);
    }

    public void onSettingsClicked(){
        fragmentContainerView.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SettingsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("SETTING") // name can be null
                .commit();

    }

    public void onRatingClicked(){
        checkingSignIn();
    }

    public void setTheme(String theme){
        switch (theme){
            case ("default"): changeThemeToSpace();
            break;
            case ("space"): changeThemeToSpace();
            break;
            case ("baseball"): changeThemeToBaseball();
            break;
            case ("cowboy"): changeThemeToCowboy();
            break;
            default: changeThemeToSpace();
            break;
        }
    }
    public void changeThemeToSpace(){
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.space) );
        } else {
            layout.setBackground(ContextCompat.getDrawable(this, R.drawable.space));
        }
        circle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.black_hole2));
        Preferences.setTheme(this, "space");
        mp_long.reset();
        mp_long = MediaPlayer.create(context, R.raw.space_sound);
        mp_short = MediaPlayer.create(context,R.raw.ball_sound);
        mp_long.start();
        mp_long.setLooping(true);
    }

    public void changeThemeToBaseball(){
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.baseball) );
        } else {
            layout.setBackground(ContextCompat.getDrawable(this, R.drawable.baseball));
        }
        circle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ball));
        Preferences.setTheme(this, "baseball");
        mp_long.reset();
        mp_long = MediaPlayer.create(context, R.raw.stadium_sound);
        mp_short = MediaPlayer.create(context, R.raw.ball_sound);
        mp_long.start();
        mp_long.setLooping(true);
    }

    public void changeThemeToCowboy(){
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.cowboy) );
        } else {
            layout.setBackground(ContextCompat.getDrawable(this, R.drawable.cowboy));
        }
        circle.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bullet));
        Preferences.setTheme(this, "cowboy");
        mp_long.reset();
        mp_long = MediaPlayer.create(context, R.raw.cowboy_sound);
        mp_short = MediaPlayer.create(context, R.raw.bullet_sound);
        mp_long.start();
        mp_long.setLooping(true);
    }

    public void mute() {
        //mute audio
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                amanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        } else {
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }
    }

    public void unmute() {
        //unmute audio
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                amanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        } else {
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
    }


    public boolean checkMute() {
        amanager = (AudioManager) getSystemService(AUDIO_SERVICE);
       return amanager.isStreamMute(AudioManager.STREAM_MUSIC);
    }

    public void savePrefs(String clicks){
        Preferences.setRecord(this, clicks);
    }

    public void setPrefs(){
        bestScore_tv2.setText(Preferences.getRecord(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            showAd();
            super.onBackPressed();
            return;
        } else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.exit),
                    Toast.LENGTH_SHORT).show();

        }
        backPressedTime = System.currentTimeMillis();
    }

    public void checkingSignIn() {
        if (isSigned()) {
            updateLeaderboard();
            showLeaderboard();
        } else{
            signInSilently();
        }
    }

    public void updateLeaderboard(){
        Games.getLeaderboardsClient(this, Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(this)))
                .submitScore(getString(R.string.leaderboard_id), Long.valueOf(Preferences.getRecord(this)));
    }

    public void showLeaderboard() {
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getLeaderboardIntent(getString(R.string.leaderboard_id))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    private boolean isSigned() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    private void signInSilently() {
        Log.d(TAG, "signInSilently()");

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            updateLeaderboard();
                            showLeaderboard();
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                            startSignInIntent();
                        }
                    }
                });
    }

    private void startSignInIntent() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                checkingSignIn();
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = "error";
                }

            }
        }
    }


    public void loadAd(){
        // ad
        Log.i(TAG, "Ad is loading...");
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        RewardedAd.load(this,"ca-app-pub-2382402581294867/6586677810",
                adRequest,new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad (@NonNull LoadAdError loadAdError){
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        Log.i(TAG, "Failed to load ad.");
                        mRewardedAd = null;
                        loadAd();
                    }

                    @Override
                    public void onAdLoaded (@NonNull RewardedAd rewardedAd){
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");
                    }
                });
        //
    }

    @SuppressLint("SetTextI18n")
    public void showAd(){
        try {
            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad was shown.");
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.d(TAG, "Ad failed to show.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad was dismissed.");
                    mRewardedAd = null;
                }

            });
            loadAd();
        } catch (Exception e){
            Log.i(TAG, "showAd error");
            loadAd();
        }
        if (mRewardedAd!= null) {
            Activity activityContext = MainActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    int points = Integer.parseInt(Preferences.getRecord(MainActivity.this)) + reward;
                    Preferences.setRecord(MainActivity.this, points + "");
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            loadAd();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.tryLater),
                    Toast.LENGTH_SHORT).show();
        }
    }
}