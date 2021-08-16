package com.utng.mediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BrowseFragment;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends BrowseSupportFragment implements OnItemViewClickedListener {
    private List<Video> mVideos = new ArrayList<Video>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
        setTitle("UTNG Media Player");
        setHeadersState(HEADERS_HIDDEN);
        setHeadersTransitionOnBackEnabled(true);

        loadRows();
        setOnItemViewClickedListener(this);
        initSearchOrb();
    }

    private void initSearchOrb() {
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_button_color));
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MediaSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadData() {
        String json = Utils.loadJSONFromResource(getActivity(), R.raw.videos);
        Type collection = new TypeToken<ArrayList<Video>>() {
        }.getType();
        Gson gson = new Gson();
        mVideos = gson.fromJson(json, collection);
    }


    private void loadRows() {
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter presenter = new CardPresenter();
        List<String> categories = getCategories();
        if (categories == null || categories.isEmpty())
            return;
        for (String category : categories) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
            for (Video movie : mVideos) {
                if (category.equalsIgnoreCase(movie.getCategory())) listRowAdapter.add(movie);
            }
            if (listRowAdapter.size() > 0) {
                HeaderItem header = new HeaderItem(adapter.size() - 1, category);
                adapter.add(new ListRow(header, listRowAdapter));
            }
        }
        setAdapter(adapter);
        setupPreferences(adapter);
    }

    private void setupPreferences(ArrayObjectAdapter adapter) {
        HeaderItem gridHeader = new HeaderItem(adapter.size(), "Preferences");
        PreferenceCardPresenter presenter = new PreferenceCardPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(presenter);
        gridRowAdapter.add("Settings");
        adapter.add(new ListRow(gridHeader, gridRowAdapter));
    }


    private List<String> getCategories() {
        if (mVideos == null) return null;
        List<String> categories = new ArrayList<String>();
        for (Video movie : mVideos) {
            if (!categories.contains(movie.getCategory())) {
                categories.add(movie.getCategory());
            }
        }
        return categories;
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object
            item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Video) {
            Video video = (Video) item;
            Intent intent = new Intent(getActivity(), VideoDetailsActivity.class);
            intent.putExtra(VideoDetailsFragment.EXTRA_VIDEO, video);
            startActivity(intent);
        } else if ("Settings".equals(item)) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
    }

}
