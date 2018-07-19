package com.project.udacity.bakingapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.project.udacity.bakingapp.DetailActivity;
import com.project.udacity.bakingapp.R;
import com.project.udacity.bakingapp.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mehseti on 12.5.2018.
 */

public class RecipeDetailMediaFragment extends Fragment {
    public RecipeDetailMediaFragment() {
    }

    @BindView(R.id.player_view)
    SimpleExoPlayerView simpleExoPlayerView;

    @BindView(R.id.step_instruction)
    TextView stepInstruction;

    @BindView(R.id.stepNumber)
    TextView stepNumber;

    @BindView(R.id.previousButton)
    AppCompatButton previousButton;

    @BindView(R.id.nextButton)
    AppCompatButton nextButton;

    static int currentStepNo = -1;

    private SimpleExoPlayer player;
    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;

    public static void setStep(Step step) {
        RecipeDetailMediaFragment.step = step;
    }

    static Step step;
    private long exo_current_position = 0;
    private boolean playerStopped = false, nextButtonVisible = true, previousButtonVisible = true;
    private static long playerStopPosition;
    DetailActivity activity = null;
    Toolbar toolbar;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recipe_detail_media, container, false);
        activity = (DetailActivity) getActivity();
        ButterKnife.bind(this, view);
        toolbar = activity.findViewById(R.id.toolbar);
        TextView textView = toolbar.findViewById(R.id.txt_toolbar);
        textView.setText("Recipe Detail Media");
        setRetainInstance(true);

        toolbar.setNavigationIcon(R.drawable.ic_android_navigation_black_24dp);

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        checkConnection();
        updateViews();
        checkNextPreviousButton();

        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
        Player();

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.goStep(currentStepNo - 1);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.goStep(currentStepNo + 1);
            }
        });

        return view;
    }

    private void checkNextPreviousButton() {
        try {
            nextButtonVisible = step.getId() != RecipeDetailFragment.recipe.getSteps().size() - 1;

            previousButtonVisible = step.getId() != 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!nextButtonVisible) {
            hideNextButton();
        }

        if (!previousButtonVisible) {
            hidePreviousButton();
        }
    }

    private void hidePreviousButton() {
        view.findViewById(R.id.previousButton).setVisibility(View.INVISIBLE);
    }


    private void updateViews() {
        try {
            step = getArguments().getParcelable("Step");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (step != null) {
            if (step.getId() != currentStepNo) {
                playerStopPosition = 0;

                Log.i("HELLO", "ASDASD");
            }

            stepInstruction.setText(step.getDescription());

            int stepNo = step.getId();
            if (stepNo == 0) {
                stepNumber.setText("Introduction");
            } else {
                stepNumber.setText("Step " + step.getId());
            }

            currentStepNo = stepNo;
            checkLandScapeOrPotrait();

        }
    }

    private void checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() == null) {
            hidePlayer();
            Log.i("WHY", "MATE");
        }
    }

    private void checkLandScapeOrPotrait() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isVideoExist = !step.getVideoURL().equals("");
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!isTablet && isVideoExist) {

                WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);

                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                simpleExoPlayerView.setLayoutParams(new LinearLayout.LayoutParams(width, height));

                activity.getSupportActionBar().hide();

                View decorView = getActivity().getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);

                view.findViewById(R.id.step_container).setVisibility(View.GONE);
                view.findViewById(R.id.navigator).setVisibility(View.GONE);
                view.findViewById(R.id.player_view).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.step_container).setVisibility(View.VISIBLE);
                view.findViewById(R.id.navigator).setVisibility(View.VISIBLE);
                view.findViewById(R.id.player_view).setVisibility(View.GONE);
            }
        } else {
            view.findViewById(R.id.step_container).setVisibility(View.VISIBLE);
            view.findViewById(R.id.navigator).setVisibility(View.VISIBLE);
            view.findViewById(R.id.player_view).setVisibility(View.VISIBLE);
        }
    }

    public void hidePlayer() {
        if (view != null)
            view.findViewById(R.id.player_view).setVisibility(View.GONE);
    }

    public void showPlayer() {
        if (view != null)
            view.findViewById(R.id.player_view).setVisibility(View.VISIBLE);
    }

    private void initializePlayer() {
        simpleExoPlayerView.requestFocus();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(getString(R.string.app_name));
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        simpleExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(true);
    }

    private void Player() {
        simpleExoPlayerView.requestFocus();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        simpleExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(shouldAutoPlay);
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        if (step != null) {
            if (!step.getVideoURL().equals("")) {
                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(step.getVideoURL()),
                        mediaDataSourceFactory, extractorsFactory, null, null);
                player.prepare(mediaSource);

                simpleExoPlayerView.setVisibility(View.VISIBLE);
            } else {
                simpleExoPlayerView.setVisibility(View.GONE);
            }
        }
        if (exo_current_position != 0 && !playerStopped) {
            player.seekTo(exo_current_position);
        } else {
            player.seekTo(playerStopPosition);
        }
        if (playerStopPosition != 0) {
            player.seekTo(playerStopPosition);
        }
    }

    public void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            playerStopPosition = player.getCurrentPosition();
            player.stop();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        activity.registerReceiver(networkChangeReceiver, intentFilter);
        if (playerStopPosition != 0) {
            player.seekTo(playerStopPosition);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(networkChangeReceiver);
        if (player != null) {
            playerStopPosition = player.getCurrentPosition();
            playerStopped = true;
        }
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            playerStopPosition = player.getCurrentPosition();
            playerStopped = true;
        }
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (player != null) {
            playerStopPosition = player.getCurrentPosition();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }

    public void hideNextButton() {
        view.findViewById(R.id.nextButton).setVisibility(View.INVISIBLE);
    }

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm.getActiveNetworkInfo() == null) {

                openDialog();
                hidePlayer();

            } else {

                if (step != null && !step.getVideoURL().equals("")) {
                    showPlayer();
                }

                activity.setConnWarning(false);
            }
        }

    };

    private void openDialog() {
        if (!activity.isConnWarning()) {
            activity.setConnWarning(true);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
            builder1.setMessage("No Internet Connection. You can not view videos.");
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });


            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }
}
