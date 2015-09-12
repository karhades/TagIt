package com.example.karhades_pc.tag_it;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karhades_pc.utils.FontCache;

import java.util.ArrayList;

/**
 * Created by Karhades - PC on 4/15/2015.
 */
public class TrackingGameFragment extends Fragment {

    private ArrayList<NfcTag> nfcTags;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nfcTags = MyTags.get(getActivity()).getNfcTags();

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_create_game, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_track:
                Toast.makeText(getActivity(), "Add menu button was pressed!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking_game, container, false);

        setupRecyclerView(view);

        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(new RiddleAdapter());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the NfcTag list.
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private class RiddleHolder extends RecyclerView.ViewHolder {
        private NfcTag nfcTag;
        private ImageView imageView;
        private TextView titleTextView;
        private TextView difficultyTextView;
        private CheckBox solvedCheckBox;

        public RiddleHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), TagPagerActivity.class);
                    intent.putExtra(TrackingTagFragment.EXTRA_TAG_ID, nfcTag.getTagId());
                    startActivity(intent);
                }
            });

            imageView = (ImageView) view.findViewById(R.id.image_view);
            titleTextView = (TextView) view.findViewById(R.id.list_item_title_text_view);
            difficultyTextView = (TextView) view.findViewById(R.id.list_item_difficulty_text_view);
            solvedCheckBox = (CheckBox) view.findViewById(R.id.list_item_solved_check_box);
        }

        public void bindRiddle(NfcTag nfcTag) {
            // Custom Fonts.
            Typeface typefaceBold = FontCache.get("fonts/Capture_it.ttf", getActivity());
            Typeface typefaceNormal = FontCache.get("fonts/amatic_bold.ttf", getActivity());

            this.nfcTag = nfcTag;

            titleTextView.setText(nfcTag.getTitle());
            titleTextView.setTypeface(typefaceBold);

            difficultyTextView.setText(nfcTag.getDifficulty());
            difficultyTextView.setTypeface(typefaceNormal);
            difficultyTextView.setTextColor(getResources().getColor(R.color.accent));

            solvedCheckBox.setChecked(nfcTag.isSolved());
        }
    }

    private class RiddleAdapter extends RecyclerView.Adapter<RiddleHolder> {
        @Override
        public RiddleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if(Build.VERSION.SDK_INT < 21)
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_pre_lollipop, viewGroup, false);
            else
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_tracking_game_fragment, viewGroup, false);

            return new RiddleHolder(view);
        }

        @Override
        public void onBindViewHolder(RiddleHolder riddleHolder, int i) {
            NfcTag nfcTag = nfcTags.get(i);
            riddleHolder.bindRiddle(nfcTag);
        }

        @Override
        public int getItemCount() {
            return nfcTags.size();
        }
    }
}
