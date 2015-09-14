package com.example.karhades_pc.tag_it;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.karhades_pc.floating_action_button.ActionButton;
import com.example.karhades_pc.nfc.NfcHandler;

/**
 * Created by Karhades on 11-Sep-15.
 */
public class CreateTagFragment extends Fragment {
    public static final String EXTRA_TAG_ID = "com.example.karhades_pc.nfctester.tag_id";

    private Button cancelButton;
    private Button tagItButton;
    private Spinner difficultySpinner;
    private ActionButton actionButton;
    private Dialog alertDialog;
    private Toolbar toolbar;

    private NfcTag nfcTag;
    private String difficulty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String tagId = getActivity().getIntent().getStringExtra(EXTRA_TAG_ID);
        nfcTag = MyTags.get(getActivity()).getTag(tagId);
    }

    @Override
    public void onResume() {
        super.onResume();

        startupAnimation();
    }

    private void startupAnimation() {
        // Floating Action Button animation on show after a period of time.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (actionButton.isHidden()) {
                    actionButton.setShowAnimation(ActionButton.Animations.SCALE_UP);
                    actionButton.show();
                    actionButton.setShowAnimation(ActionButton.Animations.ROLL_FROM_DOWN);
                }
            }
        }, 750);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_tag, container, false);

        setupToolbar(view);
        setupFloatingActionButton(view);
        initializeWidgets(view);

        return view;
    }

    private void setupToolbar(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.create_tag_tool_bar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            // Display the caret for an ancestral navigation.
            if (NavUtils.getParentActivityName(getActivity()) != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
            if (nfcTag != null)
                actionBar.setTitle(nfcTag.getTitle());
        }
    }

    private void setupFloatingActionButton(View view) {
        actionButton = (ActionButton) view.findViewById(R.id.full_screen_floating_action_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Camera!", Toast.LENGTH_SHORT).show();
            }
        });
        actionButton.setHideAnimation(ActionButton.Animations.SCALE_DOWN);
        actionButton.setShowAnimation(ActionButton.Animations.SCALE_UP);
    }

    private void initializeWidgets(View view) {
        setupSpinner(view);
        setupTagItButton(view);

        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void setupSpinner(View view) {
        difficultySpinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.difficulty, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(arrayAdapter);
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                difficulty = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // NOT IMPLEMENTED
            }
        });
        if (nfcTag != null) {
            int position = 0;
            switch (nfcTag.getDifficulty()) {
                case "Easy":
                    position = 0;
                    break;
                case "Medium":
                    position = 1;
                    break;
                case "Hard":
                    position = 2;
                    break;
            }

            difficultySpinner.setSelection(position);
        }
    }

    private void setupTagItButton(View view) {
        tagItButton = (Button) view.findViewById(R.id.tag_it_button);
        tagItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NfcHandler.toggleTagWriteMode(true);
                NfcHandler nfcHandler = ((CreateTagActivity) getActivity()).nfcHandler;
                nfcHandler.setOnTagWriteListener(new NfcHandler.OnTagWriteListener() {
                    @Override
                    public void onTagWritten(int status) {
                        Log.d("CreateTagFragment", "onTagWritten!");
                        alertDialog.dismiss();

                        if (status == NfcHandler.OnTagWriteListener.STATUS_OK) {
                            Toast.makeText(getActivity(), "Nfc Tag was successfully written!", Toast.LENGTH_SHORT).show();
                            MyTags.get(getActivity()).getNfcTags().add(new NfcTag("Red", "Nulla et lacus quis erat luctus elementum. Mauris...", difficulty, false, "04BCE16AC82980"));
                        } else if (status == NfcHandler.OnTagWriteListener.STATUS_ERROR) {
                            Toast.makeText(getActivity(), "Could not write to nfc tag!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alertDialog = onCreateDialog();
                alertDialog.show();
            }
        });
    }

    private Dialog onCreateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Nfc Write Mode")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        NfcHandler.toggleTagWriteMode(false);
                    }
                })
                .setMessage("Touch the nfc tag to write the information inserted.")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NfcHandler.toggleTagWriteMode(false);
                    }
                });

        return alertDialog.create();
    }
}
