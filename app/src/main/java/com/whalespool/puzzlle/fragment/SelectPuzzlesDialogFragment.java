package com.whalespool.puzzlle.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.transition.Slide;

import com.whalespool.puzzlle.PuzzlleApplication;
import com.whalespool.puzzlle.R;
import com.whalespool.puzzlle.module.Player;

import static com.whalespool.puzzlle.fragment.HomeFragment.samples;
import static com.whalespool.puzzlle.fragment.HomeFragment.titles;

/**
 * Created by yodazone on 2016/11/21.
 */

public class SelectPuzzlesDialogFragment extends DialogFragment implements DialogInterface.OnClickListener{
    private static final String TAG = "SelectPuzzlesDialogFragment";
    private static final String ARG_PLAYER = "player";

    Player mPlayer;
    int picId;

    public static void Show(FragmentManager managerFragment, Player player){
        SelectPuzzlesDialogFragment fragment = new SelectPuzzlesDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PLAYER, player);
        fragment.setArguments(args);
        fragment.show(managerFragment, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPlayer = getArguments().getParcelable(ARG_PLAYER);
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setSingleChoiceItems(titles, 0, this)
                .setPositiveButton(String.format("向%s发起挑战", mPlayer.name),this)
                .create();
        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which > -1){
            picId = which;
        }else {
            GameFragment game = GameFragment.newInstance(titles[picId], samples[picId],
                    PuzzlleApplication.PreferencesUtil.getCurrentLevel(), mPlayer);
            if (Build.VERSION.SDK_INT > 21) {
                game.setEnterTransition(new Slide());
                game.setExitTransition(new Slide());
            }

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, game)
                    .addToBackStack("home")
                    .commit();
        }
    }
}
