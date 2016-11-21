package com.whalespool.puzzlle.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Int2;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.whalespool.puzzlle.PuzzlleApplication;
import com.whalespool.puzzlle.R;
import com.whalespool.puzzlle.event.CompetitionResultEvent;
import com.whalespool.puzzlle.event.DishManagerInitFinishEvent;
import com.whalespool.puzzlle.event.GameSuccessEvent;
import com.whalespool.puzzlle.event.PieceMoveSuccessEvent;
import com.whalespool.puzzlle.event.TimeEvent;
import com.whalespool.puzzlle.module.Player;
import com.whalespool.puzzlle.module.Record;
import com.whalespool.puzzlle.module.User;
import com.whalespool.puzzlle.puzzle.DraggableView;
import com.whalespool.puzzlle.puzzle.GameTimer;
import com.whalespool.puzzlle.puzzle.PuzzleManager;
import com.whalespool.puzzlle.puzzle.PuzzlePiece;
import com.whalespool.puzzlle.puzzle.SplitterUtil;
import com.whalespool.puzzlle.utils.BitmapUtil;
import com.whalespool.puzzlle.utils.DensityUtil;
import com.whalespool.puzzlle.utils.GlobalUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobDate;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "param1";
    private static final String ARG_DRAWABLE = "param2";
    private static final String ARG_LEVEL = "param3";
    private static final String ARG_PLAYER = "param4";

    private static final String TAG = "GameFragment";

    private final Int2 PUZZLE_BOARD_SIZE_DP = new Int2(300, 300);

    private String mTitle;
    private int mDrawableRes;
    private int mLevel;
    private Player mCompetitor;

    private int mCompetitorTime = 0;

    private PuzzleManager mPuzzleManager;
    private GameTimer mTimer;
    private List<PuzzlePiece> mPuzzlePieces;

    private OnFragmentInteractionListener mListener;

    public GameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title       Parameter 1.
     * @param drawableRes Parameter 2.
     * @return A new instance of fragment GameFragment.
     */
    public static GameFragment newInstance(String title, int drawableRes, int level) {
        return newInstance(title, drawableRes, level, null);
    }

    public static GameFragment newInstance(String title, int drawableRes, int level, Player mCompetitor) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_DRAWABLE, drawableRes);
        args.putInt(ARG_LEVEL, level);
        args.putParcelable(ARG_PLAYER, mCompetitor);
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mDrawableRes = getArguments().getInt(ARG_DRAWABLE);
            mLevel = getArguments().getInt(ARG_LEVEL);
            mCompetitor = getArguments().getParcelable(ARG_PLAYER);
        }

    }

    @BindView(R.id.puzzle_board)
    ImageView mPuzzleBoard;
    @BindView(R.id.pieces_container)
    RecyclerView mPiecesRecycler;
    @BindView(R.id.competitor)
    CircleImageView mCompetitorAvatar;
    @BindView(R.id.back)
    FloatingActionButton mActionBtn;

    PiecesAdapter mPiecesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_puzzle, container, false);
        ButterKnife.bind(this, root);
        EventBus.getDefault().register(this);
        if (mCompetitor != null){
            Glide.with(getContext())
                    .fromResource()
                    .load(mCompetitor.avatarRes)
                    .into(mCompetitorAvatar);
            mCompetitorAvatar.setVisibility(View.VISIBLE);
            mActionBtn.setVisibility(View.GONE);
            mCompetitorTime = (int) ((mLevel-2)*24  - (new Random()).nextFloat() * (4 + mLevel*2));
        }
        initialization();
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (mTimer != null) {
            mTimer.recycle();
        }
        if (mPuzzleManager != null) {
            mPuzzleManager.recycle();
            mPuzzleManager = null;
        }
    }

    public void onTimeChange(String time) {
        if (mListener != null) {
            mListener.onTimeStampUpdate(time);
        }
    }

    public void onTitleChange(String title) {
        if (mListener != null) {
            mListener.onTitleUpdate(title);
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
        onTimeChange(null);
        onTitleChange(null);
        mListener = null;
    }

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
        void onTimeStampUpdate(String time);

        void onTitleUpdate(String title);
    }

    @OnClick(R.id.back)
    void onclick(View v) {
        if (mTimer != null) {
            mTimer.recycle();
        }
        if (mPuzzleManager != null) {
            mPuzzleManager.recycle();
        }
        onTimeChange("00:00");
        initialization();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PieceMoveSuccessEvent event) {
        int index = event.getIndex();
        PuzzlePiece piece = mPuzzlePieces.get(index);
        piece.recycleBitmap();
        mPiecesAdapter.notifyDataSetChanged();

    }

    private int mTimeInMilli;
    private String mTimeInString;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TimeEvent event) {
        mTimeInMilli++;
        if (mTimeInMilli > mCompetitorTime && mCompetitorTime != 0){
            EventBus.getDefault().post(new CompetitionResultEvent(false));
        }

        int min = mTimeInMilli / 60;
        int sec = mTimeInMilli % 60;

        mTimeInString = String.format("%02d:%02d", min, sec);
        onTimeChange(mTimeInString);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CompetitionResultEvent event){
        mTimer.stopTimer();
        //create record
        Record record = new Record();
        User user = PuzzlleApplication.PreferencesUtil.getCurrentUser();
        record.setLevel(mLevel);
        record.setFinishTime(mTimeInMilli);
        record.setPicId(mTitle);
        record.setCreateTime(new BmobDate(new Date()).toString());

        if (event.isVictory){
            record.setUsername(user.getUsername());
            SuccessDialogFragment.show(getFragmentManager(),record,mCompetitor,true);
        }else {
            record.setUsername(mCompetitor.name);
            mCompetitor.bestRecord = record;
            SuccessDialogFragment.show(getFragmentManager(),record,mCompetitor,false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GameSuccessEvent event) {
        if (mCompetitor != null){
            mCompetitorTime = 0;
            EventBus.getDefault().post(new CompetitionResultEvent(true));
            return;
        } else if (mCompetitorTime > 0) {
            return;
        }
        mTimer.stopTimer();
        //create record
        Record record = new Record();
        User user = PuzzlleApplication.PreferencesUtil.getCurrentUser();
        record.setUsername(user.getUsername());
        record.setLevel(mLevel);
        record.setFinishTime(mTimeInMilli);
        record.setPicId(mTitle);
        record.setCreateTime(new BmobDate(new Date()).toString());
        //show dialog
        SuccessDialogFragment.show(getFragmentManager(), record);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DishManagerInitFinishEvent event) {
        mTimeInMilli = 0;
        mTimer.startTimer();
        onTitleChange(mTitle);
    }

    private void initialization() {
        Log.d(TAG, "init begin");
//        layViewContainer.removeAllViews();
//        pieceList.clear();
        mTimer = new GameTimer(mTimeHandler);

        int boardWidth = DensityUtil.dip2px(getContext(), PUZZLE_BOARD_SIZE_DP.x);
        int boardHeight = DensityUtil.dip2px(getContext(), PUZZLE_BOARD_SIZE_DP.y);

        Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromResources(getResources(),
                mDrawableRes,
                boardWidth, boardHeight);
        mPuzzleManager = PuzzleManager.getInstance();
        mPuzzleManager.updateLevel(mLevel);
        mPuzzleManager.initNewGame(bitmap, mPuzzleBoard);

        Log.d(TAG, "DishManager init finish");

        try {
            // 裁剪算法优化基本完成，尚有几像素的偏差，可能是int到float强制转换的精度损失
            //mPuzzlePieces = ImageSplitter.split(bitmap, PuzzleApplication.getLevel(), DISH_WIDTH, DISH_HEIGHT);

            Bitmap tempBitmap = BitmapUtil.createNoRecycleScaleBitmap(
                    bitmap,
                    boardWidth,
                    boardHeight);
            mPuzzlePieces = SplitterUtil.split(tempBitmap, mLevel,
                    boardWidth,
                    boardHeight);


            tempBitmap.recycle();
            Log.d(TAG, "split finish");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginEnd(20);

            mPiecesAdapter = new PiecesAdapter();
            mPiecesRecycler.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
            mPiecesRecycler.setAdapter(mPiecesAdapter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "init finish");
        EventBus.getDefault().post(new DishManagerInitFinishEvent());
    }

    public static class PiecesHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.piece)
        DraggableView piece;

        public PiecesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class PiecesAdapter extends RecyclerView.Adapter<PiecesHolder> {

        int[] random;

        public PiecesAdapter() {
            random = GlobalUtils.getRandomList(mLevel * mLevel);
        }

        @Override
        public PiecesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PiecesHolder(getLayoutInflater(null).inflate(R.layout.piece_view, parent, false));
        }

        @Override
        public void onBindViewHolder(PiecesHolder holder, int position) {
            int randomPiece = random[position];
            holder.piece.setIndex(mPuzzlePieces.get(randomPiece).index);
            Bitmap b = mPuzzlePieces.get(randomPiece).bitmap;
            if (b != null && !b.isRecycled()) {
                holder.piece.setVisibility(View.VISIBLE);
                holder.piece.setImageBitmap(b);
            } else {
                holder.piece.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return random.length;
        }
    }

    private TimeHandler mTimeHandler = new TimeHandler(this);

    public static class TimeHandler extends Handler {

        private final WeakReference<Fragment> mActivity;

        public TimeHandler(Fragment activity) {
            mActivity = new WeakReference<Fragment>(activity);
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GameTimer.MESSAGE_TIMER:
                    EventBus.getDefault().post(new TimeEvent());
                    //refreshTimeText();
                    break;

            }
        }
    }
}
