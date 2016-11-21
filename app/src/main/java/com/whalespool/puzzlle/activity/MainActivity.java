package com.whalespool.puzzlle.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.whalespool.puzzlle.PuzzlleApplication;
import com.whalespool.puzzlle.R;
import com.whalespool.puzzlle.fragment.GameFragment;
import com.whalespool.puzzlle.fragment.HomeFragment;
import com.whalespool.puzzlle.fragment.SocialFragment;
import com.whalespool.puzzlle.fragment.SuccessDialogFragment;
import com.whalespool.puzzlle.module.Record;
import com.whalespool.puzzlle.module.User;
import com.whalespool.puzzlle.utils.SharedPreferencesUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.whalespool.puzzlle.fragment.HomeFragment.HOME_FRAGMENT;
import static com.whalespool.puzzlle.fragment.SocialFragment.SOCIAL_FRAGMENT;

public class MainActivity extends AppCompatActivity implements GameFragment.OnFragmentInteractionListener,SuccessDialogFragment.OnFragmentInteractionListener{

    private final static String TAG = "MainActivity";

    @BindView(R.id.container)
    FrameLayout container;

    //Custom action bar
    TextView mTimerView;
    TextView mTitleView;
    ImageView mSwitch;

    RecyclerView mRecordsRecycler;
    RecordAdapter mRecordAdapter;

    boolean mIsGameMode = true;
    String mAppName;
    User mUser;
    FindListener<Record> mFindListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mUser = PuzzlleApplication.PreferencesUtil.getCurrentUser();
        if (mUser == null) {
            onLogout(null);
        }
        mAppName = getResources().getString(R.string.app_name);

        initToolbar();
        initFragment();

        mFindListener = new FindListener<Record>() {
            @Override
            public void done(List<Record> list, BmobException e) {
                if (e == null) {
                    mRecordAdapter.setList(list);
                } else {
                    e.printStackTrace();
                }
            }
        };
        updateRecordListAll();
    }

    private void initToolbar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            View custom = getLayoutInflater().inflate(R.layout.action_bar_custom, null, false);
            mTitleView = (TextView) custom.findViewById(R.id.title);
            mTimerView = (TextView) custom.findViewById(R.id.time_stamp);
            mSwitch = (ImageView) custom.findViewById(R.id.btn_switch);
            mSwitch.setImageLevel(2);
            mSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwitch.setImageLevel(mIsGameMode ? 1 : 2);
                    Fragment fragment;
                    if (!mIsGameMode){
                        fragment = getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT);
                        if(fragment == null) fragment = new HomeFragment();
                    }else {
                        fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_FRAGMENT);
                        if (fragment == null) fragment = SocialFragment.newInstance(null,null);
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment).commit();
                    mIsGameMode = ! mIsGameMode;
                }
            });
            actionBar.setCustomView(custom, new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            initDrawer();
        }
    }

    private void initDrawer() {
        final Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withDrawerWidthDp(320)
                .withCustomView(getCustomView())
                .withSliderBackgroundColorRes(R.color.accent)
                .withCloseOnClick(true)
                .build();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer.getDrawerLayout(), null,
                R.string.material_drawer_open, R.string.material_drawer_close);

        drawer.setActionBarDrawerToggle(toggle);
        drawer.getActionBarDrawerToggle().syncState();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment(), HOME_FRAGMENT).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.container, GameFragment.newInstance("", "")).addToBackStack("home").commit();
    }

    private View getCustomView() {
        View view = getLayoutInflater().inflate(R.layout.drawer_view, null);
        CircleImageView avatarView = (CircleImageView) view.findViewById(R.id.avatar);
        TextView nameView = (TextView) view.findViewById(R.id.username);
        nameView.setText(mUser.getUsername());
        mRecordsRecycler = (RecyclerView) view.findViewById(R.id.record_recycler);
        mRecordAdapter = new RecordAdapter(null, getResources());
        mRecordsRecycler.setAdapter(mRecordAdapter);
        mRecordsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //init recordList head line text color
        int color = getResources().getColor(R.color.primary_dark);
        ((TextView)view.findViewById(R.id.record_username)).setTextColor(color);
        ((TextView)view.findViewById(R.id.record_pic_id)).setTextColor(color);
        ((TextView)view.findViewById(R.id.record_time)).setTextColor(color);
        return view;
    }


    public void onLogout(View v) {
        PuzzlleApplication.PreferencesUtil.setObject(SharedPreferencesUtil.SP_KEY_USER, null);
        Intent intent = new Intent(getApplicationContext(), StartupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onTimeStampUpdate(String time) {
        if (time == null) {
            mSwitch.setVisibility(View.VISIBLE);
            mTimerView.setVisibility(View.INVISIBLE);
            return;
        }
        mSwitch.setVisibility(View.GONE);
        mTimerView.setVisibility(View.VISIBLE);
        mTimerView.setText(time);
    }

    @Override
    public void onTitleUpdate(String title) {
        if (title != null && !title.equals(mAppName)) {
            updateRecordListSpecify(title);
            mTitleView.setText(title);
        } else {
            mTitleView.setText(mAppName);
            updateRecordListAll();
        }

    }

    @Override
    public void onSuccessInteract(String id) {
        updateRecordListSpecify(id);
    }

    private void updateRecordListSpecify(String picId) {
        BmobQuery<Record> query = new BmobQuery<>();
        query.addWhereEqualTo("picId", picId);
        query.order("-level,finishTime");
        query.setLimit(20);
        query.findObjects(mFindListener);
    }

    private void updateRecordListAll() {
        BmobQuery<Record> query = new BmobQuery<>();
        query.order("-level,finishTime");
        query.setLimit(20);
        query.findObjects(mFindListener);
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.record_username)
        TextView userView;
        @BindView(R.id.record_pic_id)
        TextView picIdView;
        @BindView(R.id.record_time)
        TextView timeView;

        public RecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class RecordAdapter extends RecyclerView.Adapter<RecordViewHolder> {

        List<Record> mRecordList;
        int colorOne;
        int colorTwo;

        public RecordAdapter(@Nullable List<Record> records, Resources rs) {
            mRecordList = records;
            colorOne = rs.getColor(R.color.colorRecordDivider);
            colorTwo = rs.getColor(R.color.colorPrimaryLight);
        }

        @Override
        public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecordViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_record, parent, false));
        }

        @Override
        public void onBindViewHolder(RecordViewHolder holder, int position) {
            Record record = mRecordList.get(position);
            holder.userView.setText(record.getUsername());
            holder.picIdView.setText(record.getPicIdInFormat());
            holder.timeView.setText(record.getFinishTimeInFormat());
            if ( position%2 == 0){
                holder.itemView.setBackgroundColor(colorOne);
            }else {
                holder.itemView.setBackgroundColor(colorTwo);
            }

        }

        @Override
        public int getItemCount() {
            return mRecordList == null ? 0 : mRecordList.size();
        }

        public void setList(List<Record> list) {
            if (mRecordList != list) {
                mRecordList = list;
            }
            notifyDataSetChanged();
        }
    }
}
