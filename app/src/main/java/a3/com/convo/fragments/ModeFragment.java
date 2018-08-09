package a3.com.convo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import a3.com.convo.Constants;
import a3.com.convo.R;
import a3.com.convo.activities.LoginActivity;
import a3.com.convo.activities.PlayGameActivity;

/**
 * This class is the fragment in PlayGameActivity where the user selects the mode
 * they would like to play in and can click a button to start the game.
 **/
public class ModeFragment extends Fragment {
    private static final String FRIEND_TAG = "friend";

    private String friend;
    private boolean freestyleSelected;
    private boolean timedSelected;
    private int minutes;
    private int seconds;
    private int numTopics;

    public ModeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) friend = Parcels.unwrap(savedInstanceState.getParcelable(FRIEND_TAG));
        Toast.makeText(getContext(), friend, Toast.LENGTH_SHORT).show();
        System.out.println(friend);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mode, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(FRIEND_TAG, Parcels.wrap(friend));
    }

    // TODO: move inner onClicks out from other onClicks
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
        final TextView tvPickTime = (TextView) view.findViewById(R.id.tvPickTime);
        final EditText timeInput = (EditText) view.findViewById(R.id.etPickTime);
        final Button playButton = (Button) view.findViewById(R.id.playButton);
        final TextView tvPickNumTopics = (TextView) view.findViewById(R.id.tvPickNumTopics);
        final EditText etPickNumTopics = (EditText) view.findViewById(R.id.etPickNumTopics);

        // If user selects to play Freestyle
        final Button freestyleMode = (Button) view.findViewById(R.id.freestyle_mode);
        freestyleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freestyleSelected = true;
                timedSelected = false;

                tvPickNumTopics.setVisibility(View.INVISIBLE);
                etPickNumTopics.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.VISIBLE);
                tvPickTime.setText(getString(R.string.pick_game_time));
                timeInput.setHint(getString(R.string.game_time_mins));

                timeInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (timeInput.getText().toString().trim().length() > 0 && freestyleSelected) {
                            minutes = Integer.parseInt(timeInput.getText().toString());
                            if (minutes > 0) {
                                playButton.setEnabled(true);
                            }
                        }
                        else {
                            playButton.setEnabled(false);
                        }
                    }

                    public void afterTextChanged(Editable s) {

                    }
                });

                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getContext() instanceof PlayGameActivity) {
                            ((PlayGameActivity) getContext()).goToGame(friend, Constants.FREESTYLE, minutes, 0);
                        }
                    }
                });
                layout.setVisibility(View.VISIBLE);
            }
        });

        // If user selects to play Timed
        Button timedMode = (Button) view.findViewById(R.id.timed_mode);
        timedMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freestyleSelected = false;
                timedSelected = true;

                tvPickNumTopics.setVisibility(View.VISIBLE);
                etPickNumTopics.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.VISIBLE);
                tvPickTime.setText(getString(R.string.pick_card_time));
                timeInput.setHint(R.string.topic_time_secs);

                timeInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (timeInput.getText().toString().trim().length() > 0 && timedSelected) {
                            seconds = Integer.parseInt(timeInput.getText().toString());
                            if (minutes > 0 && numTopics > 0) {
                                playButton.setEnabled(true);
                            }
                            else {
                                playButton.setEnabled(false);
                            }
                        }
                    }
                    public void afterTextChanged(Editable s) {

                    }
                });

                etPickNumTopics.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (etPickNumTopics.getText().toString().trim().length() > 0 && timedSelected) {
                            numTopics = Integer.parseInt(etPickNumTopics.getText().toString());
                            if (minutes > 0 && numTopics > 0 && timedSelected) {
                                playButton.setEnabled(true);
                            }
                            else {
                                playButton.setEnabled(false);
                            }
                        }

                    }
                    public void afterTextChanged(Editable s) {

                    }
                });

                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getContext() instanceof PlayGameActivity) {
                            ((PlayGameActivity) getContext()).goToGame(friend, Constants.TIMED, seconds, numTopics);
                        }
                    }
                });
                layout.setVisibility(View.VISIBLE);
            }
        });

        // If user selects to play Basic
        Button basicMode = (Button) view.findViewById(R.id.basic_mode);
        basicMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PlayGameActivity.class);
                i.putExtra(Constants.GUEST, true);
                startActivity(i);
            }
        });
    }

    public void setFriend(String selectedFriend) {
        friend = selectedFriend;
    }

}
