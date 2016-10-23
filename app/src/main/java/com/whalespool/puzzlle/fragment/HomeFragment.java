package com.whalespool.puzzlle.fragment;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.whalespool.puzzlle.PuzzlleApplication;
import com.whalespool.puzzlle.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements DialogInterface.OnClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.gallery_recycler)
    RecyclerView mGalleryView;

    public static final int[] samples = {
            R.drawable.default1,
            R.drawable.default2,
            R.drawable.default3,
            R.drawable.default4,
            R.drawable.default5,
            R.drawable.default6
    };

    public static final String[] titles = {
            "Blue World",
            "Green Concept",
            "Night Lift",
            "Independent Day",
            "Birthday",
            "Organic Milk"
    };

    private int mLevel;
    private AlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);
        initGallery();
        mLevel = PuzzlleApplication.PreferencesUtil.getCurrentLevel();
        return root;
    }

    private void initGallery() {
        GalleryAdapter adapter = new GalleryAdapter();
        mGalleryView.setAdapter(adapter);
        mGalleryView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    public void onGalleryPick(int pos) {
        GameFragment game = GameFragment.newInstance(titles[pos], samples[pos], mLevel);
        if (Build.VERSION.SDK_INT > 21) {
            game.setEnterTransition(new Slide());
            game.setExitTransition(new Slide());
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.container, game)
                .addToBackStack("home")
                .commit();
    }

    @OnClick(R.id.fab)
    public void onLevelFab(View v) {
        if (mDialog == null) {
            mDialog = getLevelSelectDialog();
        }
        mDialog.show();
    }

    private AlertDialog getLevelSelectDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.level_selector, null);
        RadioGroup radioGroup = (RadioGroup) view.findViewWithTag("selector");
        RadioButton radioButton = (RadioButton) radioGroup.findViewWithTag(String.valueOf(mLevel));
        radioButton.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton3:
                        mLevel = 3;
                        break;
                    case R.id.radioButton4:
                        mLevel = 4;
                        break;
                    case R.id.radioButton5:
                        mLevel = 5;
                        break;
                    case R.id.radioButton6:
                        mLevel = 6;
                }
                PuzzlleApplication.PreferencesUtil.saveCurrentLevel(mLevel);
                if (mDialog != null)
                    mDialog.dismiss();
            }
        });

        return mDialog = new AlertDialog.Builder(getContext())
                .setSingleChoiceItems(R.array.level_selector, (mLevel - 3), this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mLevel = which + 3;
        PuzzlleApplication.PreferencesUtil.saveCurrentLevel(mLevel);
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /*
     * Adapter Impl
     */

    public static class GalleryItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_image)
        ImageView image;
        @BindView(R.id.card_title)
        TextView title;


        GalleryItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class GalleryAdapter extends RecyclerView.Adapter<GalleryItemHolder> {

        @Override
        public GalleryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GalleryItemHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_gallery, parent, false));
        }

        @Override
        public void onBindViewHolder(final GalleryItemHolder holder, int position) {
            holder.title.setText(titles[position]);
            Glide.with(getContext())
                    .fromResource()
                    .load(samples[position])
                    .crossFade()
                    .into(holder.image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGalleryPick(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return samples.length;
        }
    }
}
