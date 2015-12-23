package barqsoft.footballscores.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.activities.CustomizeActivity;
import barqsoft.footballscores.adapters.MainScreenAdapter;
import barqsoft.footballscores.data.FootballDataLoader;
import barqsoft.footballscores.data.FootballScoresContract;
import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.syncadapters.FootballScoresSyncAdapter;
import barqsoft.footballscores.ui.CardCompatMarginItemDecoration;
import barqsoft.footballscores.utils.ScreenUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<BaseFootballData>>,
        MainScreenAdapter.OnCardMenuClickListener, View.OnClickListener {

    private static final int LOADER_ID = 0;
    private OnScrollListener onScrollListener;
    private RecyclerView scoresRecycler;
    private View noneWatchedView;
    private View nonePlaysView;
    private MainScreenAdapter adapter;
    private BroadcastReceiver syncStateChangedBroadcastReceiver;
    private boolean loaderFinished;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onScrollListener = (OnScrollListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnMovieSelectListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncStateChangedBroadcastReceiver = new SyncStateChangedBroadcastReceiver();
    }

    @Override
    public void onStart() {
        super.onStart();
        getContext().registerReceiver(syncStateChangedBroadcastReceiver,
                new IntentFilter(FootballScoresSyncAdapter.ACTION_SYNC_STATE_CHANGED));
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(syncStateChangedBroadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_main_list, container, false);

        noneWatchedView = fragmentView.findViewById(R.id.none_watched_view);
        nonePlaysView = fragmentView.findViewById(R.id.none_plays_view);
        View customizeButton = fragmentView.findViewById(R.id.empty_state_customize_button);
        customizeButton.setOnClickListener(this);

        // Recycler initialization
        adapter = new MainScreenAdapter(getActivity(), new ArrayList<BaseFootballData>());
        adapter.setOnCardMenuClickListener(this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                getActivity().getResources().getInteger(R.integer.main_screen_span_count),
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        RecyclerView.ItemDecoration decoration = new CardCompatMarginItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.matches_recycler_card_margins));

        scoresRecycler = (RecyclerView) fragmentView.findViewById(R.id.scores_recycler_view);
        scoresRecycler.setAdapter(adapter);
        scoresRecycler.setLayoutManager(layoutManager);
        scoresRecycler.addItemDecoration(decoration);
        scoresRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (onScrollListener != null)
                    onScrollListener.onScrolled(recyclerView, dx, dy);
            }
        });

        initRecyclerPadding();

        updateSyncState();

        return fragmentView;
    }

    private void initRecyclerPadding() {
        // Padding TOP
        int appBarFullHeight = getResources().getDimensionPixelSize(R.dimen.status_bar_height) +
                ScreenUtils.getActionBarHeight(getContext()) +
                getResources().getDimensionPixelSize(R.dimen.appbar_app_name_height);
        scoresRecycler.setPadding(
                scoresRecycler.getPaddingLeft(),
                appBarFullHeight,
                scoresRecycler.getPaddingRight(),
                scoresRecycler.getPaddingBottom());

        // Padding BOTTOM (optional)
        if (getResources().getBoolean(R.bool.translucent_navigation_bar)) {
            int naviBarHeight = ScreenUtils.getNavigationBarHeight(getActivity());
            scoresRecycler.setPadding(
                    scoresRecycler.getPaddingLeft(),
                    scoresRecycler.getPaddingTop(),
                    scoresRecycler.getPaddingRight(),
                    scoresRecycler.getPaddingBottom() + naviBarHeight);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        scoresRecycler.scrollToPosition(0);
    }

    @Override
    public Loader<List<BaseFootballData>> onCreateLoader(int id, Bundle args) {
        return new FootballDataLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<BaseFootballData>> loader, List<BaseFootballData> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
        loaderFinished = true;
        updateSyncState();
    }

    @Override
    public void onLoaderReset(Loader<List<BaseFootballData>> loader) {
    }

    @Override
    public void openMenu(@MainScreenAdapter.ViewType int viewType, BaseFootballData data) {
        CardMenuDialogFragment fragment = new CardMenuDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(CardMenuDialogFragment.ARGUMENT_VIEW_TYPE, viewType);
        arguments.putSerializable(CardMenuDialogFragment.ARGUMENT_MODEL_DATA, data);
        fragment.setArguments(arguments);
        fragment.show(getActivity().getSupportFragmentManager(), CardMenuDialogFragment.TAG);
    }

    private void updateSyncState() {
        int syncState = FootballScoresSyncAdapter.getSyncState(getContext());

        if (adapter.getItemCount() == 0 && loaderFinished) {
            switch (syncState) {
                case FootballScoresSyncAdapter.SYNC_OK:
                case FootballScoresSyncAdapter.SYNC_ERROR:
                    Cursor teamsCursor = getContext().getContentResolver().query(
                            FootballScoresContract.TeamsEntry.CONTENT_URI,
                            new String[]{FootballScoresContract.TeamsEntry._ID},
                            FootballScoresContract.TeamsEntry.SELECTED_COLUMN_NAME + "=?",
                            new String[]{Integer.toString(1)}, null);

                    Cursor leaguesCursor = getContext().getContentResolver().query(
                            FootballScoresContract.SoccerSeasonsEntry.CONTENT_URI,
                            new String[]{FootballScoresContract.SoccerSeasonsEntry._ID},
                            FootballScoresContract.SoccerSeasonsEntry.SELECTED_COLUMN_NAME + "=?",
                            new String[]{Integer.toString(1)}, null);

                    if (leaguesCursor.getCount() + teamsCursor.getCount() == 0) {
                        noneWatchedView.setVisibility(View.VISIBLE);
                        nonePlaysView.setVisibility(View.GONE);
                    } else {
                        noneWatchedView.setVisibility(View.GONE);
                        nonePlaysView.setVisibility(View.VISIBLE);
                    }

                    teamsCursor.close();
                    leaguesCursor.close();

                    break;

                case FootballScoresSyncAdapter.SYNC_NEVER_SYNCED:
                case FootballScoresSyncAdapter.SYNC_FIRST_SYNC_ERROR:
                    noneWatchedView.setVisibility(View.GONE);
                    nonePlaysView.setVisibility(View.GONE);
                    break;
            }
            scoresRecycler.setVisibility(View.INVISIBLE);
        } else {
            noneWatchedView.setVisibility(View.GONE);
            nonePlaysView.setVisibility(View.GONE);
            scoresRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.empty_state_customize_button) {
            Intent intent = new Intent(getActivity(), CustomizeActivity.class);
            startActivity(intent);
        }
    }

    class SyncStateChangedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateSyncState();
        }
    }

    public interface OnScrollListener {

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

    }

}
