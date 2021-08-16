package com.utng.mediaapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.VideoView;

public class PlayerActivity extends Activity implements PlayerControlsFragment.PlayerControlsListener {
    private VideoView mVideoView;
    private Video mVideo;

    public PlayerActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideo = (Video) getIntent().getSerializableExtra(VideoDetailsFragment.EXTRA_VIDEO);
        mVideoView.setVideoPath(mVideo.getVideoUrl());
    }

    @Override
    public void play() {
        mVideoView.start();
    }

    @Override
    public void pause() {
        mVideoView.pause();
    }
}
