package com.utng.mediaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.leanback.app.DetailsFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SparseArrayObjectAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VideoDetailsFragment extends DetailsFragment implements OnItemViewClickedListener, OnActionClickedListener {

    public static final String EXTRA_VIDEO = "extra_video";

    public static final long ACTION_WATCH = 1;
    private Video mVideo;
    private DetailsOverviewRow mRow;
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mRow.setImageBitmap(getActivity(), bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideo = (Video) getActivity().getIntent().getSerializableExtra(EXTRA_VIDEO);
        mRow = new DetailsOverviewRow(mVideo);
        initActions();
        ClassPresenterSelector presenterSelector = createDetailsPresenter();
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
        adapter.add(mRow);
        loadRelatedMedia(adapter);
        setAdapter(adapter);
        Picasso.with(getActivity()).load(mVideo.getPoster()).resize(274, 274).into(target);
        setOnItemViewClickedListener(this);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Video) {
            Video video = (Video) item;
            Intent intent = new Intent(getActivity(),
                    VideoDetailsActivity.class);
            intent.putExtra(VideoDetailsFragment.EXTRA_VIDEO, video);
            startActivity(intent);
        }

    }

    @Override
    public void onActionClicked(Action action) {
        if (action.getId() == ACTION_WATCH) {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra(VideoDetailsFragment.EXTRA_VIDEO, mVideo);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Action", Toast.LENGTH_SHORT).show();
        }
    }

    private void initActions() {
        mRow.setActionsAdapter(new SparseArrayObjectAdapter() {
            @Override
            public int size() {
                return 3;
            }

            @Override
            public Object get(int position) {
                if (position == 0) {
                    return new Action(ACTION_WATCH, "Watch", "");
                } else if (position == 1) {
                    return new Action(42, "Rent", "Line 2");
                } else if (position == 2) {
                    return new Action(42, "Preview", "");
                } else return null;
            }
        });
    }

    private ClassPresenterSelector createDetailsPresenter() {
        ClassPresenterSelector presenterSelector = new ClassPresenterSelector();
        FullWidthDetailsOverviewRowPresenter presenter = new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        presenter.setOnActionClickedListener(this);
        presenterSelector.addClassPresenter(DetailsOverviewRow.class, presenter);
        presenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        return presenterSelector;
    }

    private void loadRelatedMedia(ArrayObjectAdapter adapter) {
        String json = Utils.loadJSONFromResource(getActivity(), R.raw.videos);
        Gson gson = new Gson();
        Type collection = new TypeToken<ArrayList<Video>>() {
        }.getType();
        List<Video> videos = gson.fromJson(json, collection);
        if (videos == null) return;
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (Video video : videos) {
            if (video.getCategory().equals(mVideo.getCategory()) && !video.getTitle().equals(mVideo.getTitle())) {
                listRowAdapter.add(video);
            }
        }
        HeaderItem header = new HeaderItem(0, "Related");
        adapter.add(new ListRow(header, listRowAdapter));
    }
}