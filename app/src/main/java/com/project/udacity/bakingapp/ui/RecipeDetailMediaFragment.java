package com.project.udacity.bakingapp.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaCodec;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.JsonArray;
import com.project.udacity.bakingapp.MainActivity;
import com.project.udacity.bakingapp.R;
import com.project.udacity.bakingapp.Step;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;

/**
 * Created by mehseti on 12.5.2018.
 */

public class RecipeDetailMediaFragment extends Fragment
{
    public RecipeDetailMediaFragment(){}
    @BindView(R.id.player_view)
    SimpleExoPlayerView simpleExoPlayerView;
    @BindView(R.id.step_instruction)
    TextView stepInstruction;
    private SimpleExoPlayer player;
    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    Step step;
    private static final String SELECTED_POSITION = "position";
    private ImageView ivHideControllerButton;
    private SimpleExoPlayer exoPlayer;
    private long exo_current_position = 0;
    private Unbinder unbinder;
    private boolean playerStopped = false;
    private long playerStopPosition;
    private int RENDERER_COUNT = 300000;
    private int minBufferMs =    250000;
    private final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private final int BUFFER_SEGMENT_COUNT = 256;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe_detail_media,container,false);
        ButterKnife.bind(this,view);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView textView = toolbar.findViewById(R.id.txt_toolbar);
        textView.setText("Recipe Detail Media");
        toolbar.setNavigationIcon(R.drawable.ic_android_navigation_black_24dp);
        final MainActivity activity = (MainActivity) getActivity();
        setRetainInstance(true);
        if (toolbar != null)
        {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    activity.onBackPressed();
                }
            });
        }
        try
        {
            step = getArguments().getParcelable("Step");
            stepInstruction.setText(step.getDescription());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if (savedInstanceState != null) {
            playerStopPosition = savedInstanceState.getLong(SELECTED_POSITION);
            view.findViewById(R.id.step_instruction_lbl).setVisibility(View.GONE);
            view.findViewById(R.id.step_instruction).setVisibility(View.GONE);
        }
        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
       // window = new Timeline.Window();
        //initializePlayer();
        //initializePlayera(Uri.parse("http://www.sample-videos.com/video/mp4/480/big_buck_bunny_480p_5mb.mp4"));
        Player();
        return view;
    }

    private void initializePlayer()
    {
        simpleExoPlayerView.requestFocus();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(getString(R.string.app_name));
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        simpleExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(true);
    }

    private void Player()
    {
        simpleExoPlayerView.requestFocus();
        TrackSelection.Factory videoTrackSelectionFactory =  new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        simpleExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(shouldAutoPlay);
        //"http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        if(step!= null) {
            if(step.getVideoURL() != "")
            {
                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(step.getVideoURL()),
                mediaDataSourceFactory, extractorsFactory, null, null);
                player.prepare(mediaSource);
            }
           /* else
            {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(Uri.parse(step.getThumbnailURL()).getPath());
                try {
                    Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(player.getCurrentPosition());
                    simpleExoPlayerView.setBackgroundDrawable(new BitmapDrawable(bitmap));
                } catch (OutOfMemoryError outOfMemoryError) {
                    //Not entirely sure if this will ever be thrown but better safe than sorry.
                    exoPlayer.seekTo(player.getCurrentPosition());
                }
            } */
        }
        if (exo_current_position != 0 && !playerStopped){
            player.seekTo(exo_current_position);
        } else {
            player.seekTo(playerStopPosition);
        }
        if(playerStopPosition != 0)
        {
            player.seekTo(playerStopPosition);
        }
    }

    private void releasePlayer(){
        if(player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            playerStopPosition = player.getCurrentPosition();
            player.stop();
            player.release();
            player = null;
            trackSelector = null;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
           // Player();
        }
       // initializePlayera(Uri.parse("http://www.sample-videos.com/video/mp4/480/big_buck_bunny_480p_5mb.mp4"));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(playerStopPosition != 0)
        {
            player.seekTo(playerStopPosition);
        }
        else if((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            playerStopPosition = player.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(player != null) {
            playerStopPosition = player.getCurrentPosition();
            playerStopped = true;
        }
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        if(player != null)
        {
            playerStopPosition = player.getCurrentPosition();
        }
        outState.putLong(SELECTED_POSITION, playerStopPosition);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        releasePlayer();
    }

}
