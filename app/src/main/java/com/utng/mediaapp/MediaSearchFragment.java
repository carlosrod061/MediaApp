package com.utng.mediaapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.leanback.app.SearchFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SpeechRecognitionCallback;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MediaSearchFragment extends SearchFragment implements SpeechRecognitionCallback, SearchFragment.SearchResultProvider, OnItemViewClickedListener {

    public static final int SPEECH_REQUEST_CODE = 42;
    private ArrayObjectAdapter mRowsAdapter;
    private List<Video> mVideos;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSearchResultProvider(this);
        setSpeechRecognitionCallback(this);
        setOnItemViewClickedListener(this);
        loadData();
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
    }

    private void loadData() {
        String json = Utils.loadJSONFromResource(getActivity(), R.raw.videos);
        Type collection = new TypeToken<ArrayList<Video>>() {
        }.getType();
        Gson gson = new Gson();
        mVideos = gson.fromJson(json, collection);
    }

    private void loadQuery(String query) {
        if (mRowsAdapter != null) mRowsAdapter.clear();
        if (query == null || query.length() == 0) return;
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (Video video : mVideos) {
            if (video.getTitle() != null && video.getTitle().toLowerCase().contains(query.toLowerCase())) {
                listRowAdapter.add(video);
            }
        }
        if (listRowAdapter.size() == 0) return;
        HeaderItem header = new HeaderItem("Results");
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Video) {
            Video video = (Video) item;
            Intent intent = new Intent(getActivity(), VideoDetailsActivity.class);
            intent.putExtra(VideoDetailsFragment.EXTRA_VIDEO, video);
            startActivity(intent);
        }
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        loadQuery(newQuery);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        loadQuery(query);
        return true;
    }

    @Override
    public void recognizeSpeech() {
        startActivityForResult(getRecognizerIntent(), SPEECH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setSearchQuery(data, true);
        }
    }
}