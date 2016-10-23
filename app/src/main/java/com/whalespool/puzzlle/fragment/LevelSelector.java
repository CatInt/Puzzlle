//package com.whalespool.puzzlle.fragment;
//
//import android.app.Dialog;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v7.app.AlertDialog;
//
//import com.whalespool.puzzlle.R;
//import com.whalespool.puzzlle.module.Record;
//
///**
// * Created by yodazone on 2016/10/22.
// */
//
//public class LevelSelector extends DialogFragment {
//
//    AlertDialog mDialog;
//    Record mRecord;
//
//    public static void show(FragmentManager fm, Record user) {
//        SuccessDialogFragment dialogFragment = new SuccessDialogFragment();
//        Bundle argument = new Bundle();
//        argument.putParcelable("record", user);
//        dialogFragment.setArguments(argument);
//        dialogFragment.show(fm, "success");
//    }
//
//    @Override
//    public void setArguments(Bundle args) {
//        super.setArguments(args);
//        if (args != null) {
//            mRecord = args.getParcelable("record");
//        }
//    }
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        return mDialog = new AlertDialog.Builder(getActivity(), R.style.NoBackgroundDialog)
//                .setView(getDialogViewFromRes())
//                .create();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (mDialog != null) {
//            mDialog.show();
//        }
//    }
//
//}
