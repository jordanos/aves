package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.aves.Helper.EncryptedFileDataSourceFactory;
import com.example.aves.Helper.Util;
import com.example.aves.Interface.Api;
import com.example.aves.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

public class PreviewActivity extends AppCompatActivity {

    private String url;

    private DefaultBandwidthMeter bandwidthMeter;
    private LoadControl loadControl ;
    private RenderersFactory renderersFactory;
    private ExtractorsFactory extractorsFactory;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");
        url = Util.getMediaUrl(url);
        playerView = findViewById(R.id.previewPlayer);
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView = findViewById(R.id.previewPlayer);
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.addMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    private void releasePlayer(){
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (com.google.android.exoplayer2.util.Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (com.google.android.exoplayer2.util.Util.SDK_INT < 24 || player == null) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        if(com.google.android.exoplayer2.util.Util.SDK_INT<24){
            releasePlayer();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(com.google.android.exoplayer2.util.Util.SDK_INT>=24){
            releasePlayer();
        }
        super.onStop();
    }
}