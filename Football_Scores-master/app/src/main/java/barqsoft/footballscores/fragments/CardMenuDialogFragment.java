package barqsoft.footballscores.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.activities.CustomizeActivity;
import barqsoft.footballscores.adapters.MainScreenAdapter;
import barqsoft.footballscores.data.FootballScoresContract;
import barqsoft.footballscores.model.BaseFootballData;
import barqsoft.footballscores.model.TeamFixtureComing;
import barqsoft.footballscores.model.TeamFixtureScore;
import barqsoft.footballscores.syncadapters.FootballScoresSyncAdapter;
import barqsoft.footballscores.utils.ModelParser;

/**
 * Created by Paulina on 2015-12-07.
 */
public class CardMenuDialogFragment extends DialogFragment
        implements View.OnClickListener {

    public static final String TAG = CardMenuDialogFragment.class.getName();
    public static final String ARGUMENT_VIEW_TYPE = "argument_view_type";
    public static final String ARGUMENT_MODEL_DATA = "argument_model_data";

    @MainScreenAdapter.ViewType
    private int viewType;
    private BaseFootballData data;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.dialog_card_menu, container, false);

        Bundle args = getArguments();
        if (args != null) {
            //noinspection ResourceType
            viewType = args.getInt(ARGUMENT_VIEW_TYPE);
            data = (BaseFootballData) args.getSerializable(ARGUMENT_MODEL_DATA);
        }

        TextView shareView = (TextView) fragmentView.findViewById(R.id.card_menu_share_view);
        shareView.setOnClickListener(this);

        TextView customizeView = (TextView) fragmentView.findViewById(R.id.card_menu_customize_view);
        customizeView.setOnClickListener(this);

        TextView removeFromObservedView = (TextView) fragmentView.findViewById(R.id.card_menu_remove_from_observed_view);
        removeFromObservedView.setOnClickListener(this);
        // Filling 'Remove ... from observed' with name
        String name;
        switch (viewType) {
            case MainScreenAdapter.VIEW_TYPE_TEAM_FIXTURES_COMING:
                TeamFixtureComing teamFixtureComing = (TeamFixtureComing) data;
                name = teamFixtureComing.watchedTeam.getName();
                break;
            case MainScreenAdapter.VIEW_TYPE_TEAM_FIXTURES_SCORE:
                TeamFixtureScore teamFixtureScore = (TeamFixtureScore) data;
                name = teamFixtureScore.watchedTeam.getName();
                break;
            default:
                throw new UnsupportedOperationException(
                        "Getting name is not supported for this viewType: " + viewType);
        }
        removeFromObservedView.setText(getString(R.string.card_menu_remove_from_observed_action, name));

        return fragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_menu_share_view:
                startActivity(createShareIntent());
                dismiss();
                break;
            case R.id.card_menu_customize_view:
                Intent intent = new Intent(getActivity(), CustomizeActivity.class);
                startActivity(intent);
                dismiss();
                break;
            case R.id.card_menu_remove_from_observed_view:
                unwatchTeam(getTeamId());
                broadcastUpdate();
                dismiss();
                break;
        }
    }

    private Intent createShareIntent(){
        String sharingText;
        if (viewType == MainScreenAdapter.VIEW_TYPE_TEAM_FIXTURES_COMING) {
            sharingText = ModelParser.toString((TeamFixtureComing) data, getActivity());
        } else if (viewType == MainScreenAdapter.VIEW_TYPE_TEAM_FIXTURES_SCORE) {
            sharingText = ModelParser.toString((TeamFixtureScore) data, getActivity());
        } else {
            throw new UnsupportedOperationException(
                    "Sharing is not supported for this viewType: " + viewType);
        }

        Intent shareIntent = ShareCompat.IntentBuilder
                .from(getActivity())
                .setText(sharingText)
                .setType("text/plain")
                .createChooserIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        return shareIntent;
    }

    private void unwatchTeam(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FootballScoresContract.TeamsEntry.SELECTED_COLUMN_NAME, false);
        getContext().getContentResolver().update(
                FootballScoresContract.TeamsEntry.CONTENT_URI,
                contentValues,
                FootballScoresContract.TeamsEntry._ID + "=?",
                new String[]{Integer.toString(id)});
    }

    private int getTeamId() {
        switch (viewType) {
            case MainScreenAdapter.VIEW_TYPE_TEAM_FIXTURES_COMING:
                TeamFixtureComing teamFixtureComing = (TeamFixtureComing) data;
                return teamFixtureComing.watchedTeam.id;
            case MainScreenAdapter.VIEW_TYPE_TEAM_FIXTURES_SCORE:
                TeamFixtureScore teamFixtureScore = (TeamFixtureScore) data;
                return teamFixtureScore.watchedTeam.id;
            default:
                throw new UnsupportedOperationException(
                        "Getting 'team.id' is not supported for this viewType: " + viewType);
        }
    }

    private void broadcastUpdate() {
        getContext().sendBroadcast(new Intent(FootballScoresSyncAdapter.ACTION_SYNC_STATE_CHANGED));
    }
}
