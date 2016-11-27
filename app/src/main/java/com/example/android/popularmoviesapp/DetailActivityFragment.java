package com.example.android.popularmoviesapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private Intent receiveIntent;
    private ListView mTrailerList;

    private TrailerAdapter mTrailerAdapter;

    private Movie receiveMovie;

    private static final int MOVIE_RUNTIME_LOADER_ID = 1;
    private static final int MOVIE_TRAILERS_LOADER_ID = 2;
    private static final int MOVIE_OVERVIEW_LOADER_ID = 3;
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private LoaderCallbacks<String> runtimeLoaderListener =
            new LoaderCallbacks<String>() {
                @Override
                public Loader<String> onCreateLoader(int id, Bundle args) {
                    if (receiveMovie != null) {
                        return new MovieRuntimeLoader(getActivity(), receiveMovie.getId());
                    }
                    return null;
                }

                @Override
                public void onLoadFinished(Loader<String> loader, String length) {
                    if (!TextUtils.isEmpty(length)) {
                        receiveMovie.setLength(length);
                        mTrailerAdapter.clear();
                        mTrailerAdapter.add(new Trailer("initial", "moveInfo"));
                    }
                }

                @Override
                public void onLoaderReset(Loader<String> loader) {
                    mTrailerAdapter.clear();
                }
            };
    private LoaderCallbacks<List<Trailer>> trailerLoaderListener
            = new LoaderCallbacks<List<Trailer>>() {
        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
            if (receiveMovie != null) {
                return new MovieTrailerLoader(getActivity(), receiveMovie.getId());
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> trailers) {
            if (trailers != null && !trailers.isEmpty()) {
                mTrailerAdapter.addAll(trailers);
            }
            Log.i(LOG_TAG, "mTrailerAdapter " + mTrailerAdapter.getCount());
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {
                mTrailerAdapter.clear();
        }
    };
    private LoaderCallbacks<String[]> reviewLoaderListener
            = new LoaderCallbacks<String[]>() {
        @Override
        public Loader<String[]> onCreateLoader(int id, Bundle args) {
            if (receiveMovie != null) {
                return new MovieReviewLoader(getActivity(), receiveMovie.getId());
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<String[]> loader, String[] reviews) {
            if (reviews != null && reviews.length != 0) {
            }
            Log.i(LOG_TAG, "mTrailerAdapter " + mTrailerAdapter.getCount());
        }

        @Override
        public void onLoaderReset(Loader<String[]> loader) {

        }
    };

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        setView(rootView);

        receiveIntent = getActivity().getIntent();
        if (receiveIntent != null) {
            receiveMovie = receiveIntent.getParcelableExtra("movie_info");
        }

        LoaderManager loaderManager = getActivity().getLoaderManager();
        loaderManager.initLoader(MOVIE_RUNTIME_LOADER_ID, null, runtimeLoaderListener);
        loaderManager.initLoader(MOVIE_TRAILERS_LOADER_ID, null, trailerLoaderListener);

        ArrayList<Trailer> trailers = new ArrayList<>();
        mTrailerAdapter = new TrailerAdapter(getActivity(), trailers, receiveMovie);

        mTrailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = mTrailerAdapter.getItem(position).getmUrl();
                if (position != 0) {
                    Uri link = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, link);
                    startActivity(intent);
                }
            }
        });
        mTrailerList.setAdapter(mTrailerAdapter);

        return rootView;
    }

    private void setView(View rootView) {
        mTrailerList = (ListView) rootView.findViewById(R.id.trailers_list);
    }
}
