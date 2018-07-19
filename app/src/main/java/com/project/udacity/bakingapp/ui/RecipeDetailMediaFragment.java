package com.project.udacity.bakingapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

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

    @BindView(R.id.step_container)
    CardView stepContainer;

    @BindView(R.id.navigator)
    LinearLayout navigator;

    @BindView(R.id.placeholderImage)
    ImageView placeholderImage;

    static int currentStepNo = -1;

    private SimpleExoPlayer player;
    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;

    public void setStep(Step step) {
        this.step = step;
    }

    Step step;
    private static final String SELECTED_POSITION = "position";
    private ImageView ivHideControllerButton;
    private long exo_current_position = 0;
    private Unbinder unbinder;
    private boolean playerStopped = false, nextButtonVisible = true, previousButtonVisible = true;
    private long playerStopPosition = 0;
    private boolean playerState = false;
    private int RENDERER_COUNT = 300000;
    private int minBufferMs = 250000;
    private final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private final int BUFFER_SEGMENT_COUNT = 256;
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
        shouldAutoPlay = true;
        if (savedInstanceState != null) {
            playerStopPosition = savedInstanceState.getLong("playerStopPosition");
            shouldAutoPlay = savedInstanceState.getBoolean("state");
        }

        checkConnection();
        updateViews();
        checkNextPreviousButton();


        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
        // window = new Timeline.Window();
        //initializePlayer();
        //initializePlayera(Uri.parse("http://www.sample-videos.com/video/mp4/480/big_buck_bunny_480p_5mb.mp4"));
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
            nextButtonVisible = step.getId() != activity.getRecipe().getSteps().size() - 1;

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
        previousButton.setVisibility(View.INVISIBLE);
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
        boolean isThumbnailExist = !step.getThumbnailURL().equals("");
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

                stepContainer.setVisibility(View.GONE);
                navigator.setVisibility(View.GONE);
                simpleExoPlayerView.setVisibility(View.VISIBLE);
                DetailActivity.setVideoStarted(true);
            } else {
                stepContainer.setVisibility(View.VISIBLE);
                navigator.setVisibility(View.VISIBLE);
                simpleExoPlayerView.setVisibility(View.GONE);
            }
        } else {
            stepContainer.setVisibility(View.VISIBLE);
            navigator.setVisibility(View.VISIBLE);
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            DetailActivity.setVideoStarted(true);
        }

        if (isThumbnailExist) {
            simpleExoPlayerView.setVisibility(View.GONE);
            placeholderImage.setVisibility(View.VISIBLE);
            String thumbnailUrl = step.getThumbnailURL();

            Log.i("thumbnailurl", thumbnailUrl);
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

            mediaMetadataRetriever .setDataSource(thumbnailUrl, new HashMap<String, String>());
            Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(1); //unit in microsecond
            placeholderImage.setImageBitmap(bmFrame);
        }
    }

    public void hidePlayer() {
        if (view != null)
            simpleExoPlayerView.setVisibility(View.GONE);
    }

    public void showPlayer() {
        if (view != null)
            simpleExoPlayerView.setVisibility(View.VISIBLE);
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
        //"http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
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
        Log.i("SEEKPOS", playerStopPosition + "/*/");
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
        } /*else if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
       /* if (Util.SDK_INT <= 23) {
            playerStopPosition = player.getCurrentPosition();
            releasePlayer();
        } */

        activity.unregisterReceiver(networkChangeReceiver);
        if (player != null) {
            playerStopped = true;
            playerStopPosition = player.getCurrentPosition();
            //getting play when ready so that player can be properly store state on rotation
            playerState = player.getPlayWhenReady();
            player.stop();
            player.release();
            player = null;
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

        outState.putLong("playerStopPosition", playerStopPosition);
        outState.putBoolean("state", playerState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }

    public void hideNextButton() {
        nextButton.setVisibility(View.INVISIBLE);
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
