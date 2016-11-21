package com.whalespool.puzzlle.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.whalespool.puzzlle.R;
import com.whalespool.puzzlle.module.Player;
import com.whalespool.puzzlle.utils.GlobalUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SocialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SocialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFragment extends Fragment implements View.OnClickListener {

    public final static String SOCIAL_FRAGMENT = "social";

    public static final int[] avatarRes = new int[]{
            R.drawable.avatar1, R.drawable.avatar4,
            R.drawable.avatar3, R.drawable.avatar2, R.drawable.avatar5
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static String[] PLAYER_NAMES = new String[]{"Jerry", "Tom", "Amy", "Bob", "Lily"};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SocialListAdapter mAdapter;
    private static final int PLAYER_COUNT = 5;

    public SocialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialFragment newInstance(String param1, String param2) {
        SocialFragment fragment = new SocialFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);
        RecyclerView list = (RecyclerView) view.findViewById(R.id.content_list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        view.findViewById(R.id.fab).setOnClickListener(this);
        mAdapter = new SocialListAdapter(getActivity());
        requestForData();
        list.setAdapter(mAdapter);
        Log.d("XXX", "createView");
        return view;
    }

    private void requestForData() {
        ArrayList<Player> arrayList = new ArrayList<>();
        int[] random = GlobalUtils.getRandomList(PLAYER_NAMES.length);
        for (int i = 0; i < PLAYER_NAMES.length; i++){
            Player player = new Player();
            player.name = PLAYER_NAMES[random[i]];
            player.isOnline = (new Random().nextFloat())*2 > 1;
            player.avatarRes = avatarRes[random[i]];
            arrayList.add(player);
        }
        mAdapter.setPlayerList(arrayList);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View v) {
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setView(R.layout.dialog_add_player)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) ((Dialog)dialog).findViewById(R.id.edit_name);
                        String name = editText.getText().toString();
                        Player player = new Player();
                        player.name = name;
                        player.avatarRes = avatarRes[new Random().nextInt(5)];
                        player.isOnline = (new Random().nextFloat())*2 > 1;
                        mAdapter.addItemToPlayerList(player);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .create();
        dialog.show();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
