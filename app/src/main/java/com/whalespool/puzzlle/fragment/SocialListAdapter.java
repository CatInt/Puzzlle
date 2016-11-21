package com.whalespool.puzzlle.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.whalespool.puzzlle.R;
import com.whalespool.puzzlle.module.Player;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialListAdapter extends RecyclerView.Adapter<SocialListAdapter.ViewHolder> implements View.OnClickListener {
    ArrayList<Player> mPlayers;
    Context mContext;

    SocialListAdapter(Context context){
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Player player = mPlayers.get(position);
        holder.name.setText(player.name);
        holder.state.setText(player.isOnline? "online" : "offline");
        holder.state.setTextColor(mContext.getResources().getColor(
                player.isOnline? R.color.colorStateOnline : R.color.colorStateOffline));
        holder.invite.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(
                player.isOnline? R.color.colorIconOnline : R.color.colorIconOffline
        )));
        holder.invite.setTag(position);
        holder.invite.setOnClickListener(this);
        Glide.with(mContext)
                .fromResource()
                .load(player.avatarRes)
                .crossFade()
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return mPlayers == null ? 0 : mPlayers.size();
    }

    public void setPlayerList(ArrayList<Player> playerList){
        mPlayers = playerList;
    }

    public void addItemToPlayerList(Player player){
        mPlayers.add(player);
    }

    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        final Player player = mPlayers.get(pos);
        if (player.isOnline){
            SelectPuzzlesDialogFragment.Show(((FragmentActivity) mContext).getSupportFragmentManager(), player);
        }else {
            Snackbar.make(v, "该玩家不在线", Snackbar.LENGTH_SHORT).show();
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView state;
        CircleImageView avatar;
        ImageView invite;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            state = (TextView) itemView.findViewById(R.id.state);
            avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
            invite = (ImageView) itemView.findViewById(R.id.invite);

        }
    }
}
