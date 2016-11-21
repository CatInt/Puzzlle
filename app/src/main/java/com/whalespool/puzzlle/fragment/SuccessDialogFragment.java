package com.whalespool.puzzlle.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whalespool.puzzlle.R;
import com.whalespool.puzzlle.module.Player;
import com.whalespool.puzzlle.module.Record;
import com.whalespool.puzzlle.utils.DensityUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by yodazone on 2016/10/21.
 */

public class SuccessDialogFragment extends DialogFragment implements View.OnClickListener {


    AlertDialog mDialog;
    Record mRecord;
    Player mCompetitor;
    boolean mIsVictory;

    OnFragmentInteractionListener mListener;

    public static void show(FragmentManager fm, Record user){
        show(fm, user, null, true);
    }

    public static void show(FragmentManager fm, Record user, Player competitor, boolean isVictory) {
        SuccessDialogFragment dialogFragment = new SuccessDialogFragment();
        Bundle argument = new Bundle();
        argument.putParcelable("player", competitor);
        argument.putBoolean("victory", isVictory);
        argument.putParcelable("record", user);
        dialogFragment.setArguments(argument);
        dialogFragment.show(fm, "success");
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args != null) {
            mRecord = args.getParcelable("record");
            mIsVictory = args.getBoolean("victory");
            mCompetitor = args.getParcelable("player");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return mDialog = new AlertDialog.Builder(getActivity(), R.style.NoBackgroundDialog)
                .setView(mCompetitor != null? getCompeteDialogView() : getDialogViewFromRes())
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDialog != null) {
            mDialog.show();
        }
    }

    private void changeDrawerRecordList(OnFragmentInteractionListener context, String id) {
        if (context != null) {
            context.onSuccessInteract(id);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public View getCompeteDialogView(){
        View view = LayoutInflater.from(new ContextThemeWrapper(getContext(), R.style.NoBackgroundDialog)).inflate(R.layout.dialog_succes, null);
        String result;
        Button btn = (Button) view.findViewById(R.id.dialog_button);
        btn.setOnClickListener(this);
        if (mIsVictory){
            result = String.format("你以%s击败了%s", mRecord.getFinishTimeInFormat(),mCompetitor.name);
        }else {
            result = String.format("你被%s(%s)击败了", mCompetitor.name, mCompetitor.bestRecord.getFinishTimeInFormat());
            btn.setText("可恶");
            btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_red_600)));
        }
        ((TextView) view.findViewById(R.id.dialog_time)).setText(result);
        return view;
    }

    public View getDialogViewFromRes() {
        View view = LayoutInflater.from(new ContextThemeWrapper(getContext(), R.style.NoBackgroundDialog)).inflate(R.layout.dialog_succes, null);
        ((TextView) view.findViewById(R.id.dialog_time)).setText(mRecord.getFinishTimeInFormat());
        view.findViewById(R.id.dialog_button).setOnClickListener(this);
        return view;
    }

    public View getDialogView() {
        int widthPx = DensityUtil.dip2px(getContext(), 240);

        LinearLayout view = new LinearLayout(getContext(), null, android.R.style.Theme_Dialog);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                widthPx, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setOrientation(LinearLayout.VERTICAL);
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        view.setBackgroundColor(getResources().getColor(R.color.accent));

        ImageView logo = new ImageView(getContext());
        logo.setLayoutParams(new LinearLayout.LayoutParams(widthPx, widthPx));
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        logo.setImageDrawable(getResources().getDrawable(R.drawable.app_logo_large));

        TextView time = new TextView(getContext());
        time.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        time.setText(mRecord.getFinishTime());

        Button button = new Button(getContext());
        button.setText("POST");
        button.setTextColor(getResources().getColor(R.color.md_white_1000));
        button.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setBackgroundColor(getResources().getColor(R.color.primary));
        button.setOnClickListener(this);

        view.addView(logo);
        view.addView(time);
        view.addView(button);
        return view;
    }


    public Context getContext() {
        return getActivity();
    }

    @Override
    public void onClick(View v) {
        if (mIsVictory){
            mRecord.save(new SaveListener<String>() {
                //In case the fragment already dismiss
                Context context = getActivity();

                @Override
                public void done(String s, BmobException e) {
                    if (e != null) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        changeDrawerRecordList((OnFragmentInteractionListener) context, mRecord.getPicId());
                        Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        dismiss();
    }

    public interface OnFragmentInteractionListener {
        void onSuccessInteract(String id);
    }
}
