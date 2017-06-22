package com.pip.talk.pip.pc.piptalk.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pip.talk.pip.pc.piptalk.R;

import java.util.ArrayList;


public class SendChatAdapter extends ArrayAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<ChatMessage> chatMessageList= new ArrayList<>();


    public SendChatAdapter(Context context, int resource,ArrayList<ChatMessage> arrayList) {
        super(context, resource);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        chatMessageList = arrayList;

    }

    public SendChatAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }
    void SendMessageToAdapter(ArrayList<ChatMessage> list) {
        chatMessageList = list;
        notifyDataSetChanged();
    }

    void addMessageToAdapter(ChatMessage list) {
        chatMessageList.add(list) ;
        notifyDataSetChanged();
    }


    @Override
    public int getPosition(Object item) {
        return super.getPosition(item);
    }


    public int getCount() {

        Log.e("POSITION"," inside adapter, size is :"+chatMessageList.size());
        return chatMessageList.size();
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ViewHolder adapterHolder=null;

        boolean mine = chatMessageList.get(position).isMine();

        Log.e("WWW","** is mine : "+position +"  "+chatMessageList.get(position).getmSenderUserName());
        Log.e("WWW","** message : "+position +"  "+chatMessageList.get(position).getmMessageBody());
        Log.e("WWW","** is mine : "+position +"  "+chatMessageList.get(position).isMine());
        Log.e("POSITION"," inside adapter, iteration number :"+position);

        if (row == null) {
            row = inflater.inflate(R.layout.chat_message_item, parent, false);
            adapterHolder= new ViewHolder();
            //adapterHolder.p = (LinearLayout) row.findViewById(R.id.bubble_parent);
            adapterHolder.layoutSend = (LinearLayout) row.findViewById(R.id.linearSend);
            adapterHolder.layoutReceive = (LinearLayout) row.findViewById(R.id.linearReceive);
            adapterHolder.message = (TextView) row.findViewById(R.id.message_text);
            adapterHolder.time = (TextView) row.findViewById(R.id.message_time);
            adapterHolder.messageReveive = (TextView) row.findViewById(R.id.message2_text);
            adapterHolder.timeReceive = (TextView) row.findViewById(R.id.message2_time);
            adapterHolder.alert = (ImageView) row.findViewById(R.id.alert_not_sent);

            row.setTag(adapterHolder);
        } else {
            adapterHolder = (ViewHolder) row.getTag();
        }


        if (mine){
            adapterHolder.layoutSend.setVisibility(View.VISIBLE);
            adapterHolder.message.setVisibility(View.VISIBLE);
            adapterHolder.time.setVisibility(View.VISIBLE);
            Log.e("POSITION"," inside adapter, message in position :"+position+" is : "+chatMessageList.get(position).getmMessageBody());
            adapterHolder.message.setText(chatMessageList.get(position).getmMessageBody());
            adapterHolder.time.setText(chatMessageList.get(position).getTime());
            adapterHolder.messageReveive.setVisibility(View.GONE);
            adapterHolder.timeReceive.setVisibility(View.GONE);
            adapterHolder.layoutReceive.setVisibility(View.GONE);
        }
        else {
            Log.e("WWW"," adapter , message received : "+chatMessageList.get(position).getmMessageBody());
            adapterHolder.layoutReceive.setVisibility(View.VISIBLE);
            adapterHolder.messageReveive.setVisibility(View.VISIBLE);
            adapterHolder.timeReceive.setVisibility(View.VISIBLE);
            Log.e("POSITION"," inside adapter, message in position :"+position+" is : "+chatMessageList.get(position).getmMessageBody());
            adapterHolder.messageReveive.setText(chatMessageList.get(position).getmMessageBody());
            adapterHolder.timeReceive.setText(chatMessageList.get(position).getTime());
            adapterHolder.message.setVisibility(View.GONE);
            adapterHolder.time.setVisibility(View.GONE);
            adapterHolder.layoutSend.setVisibility(View.GONE);

        }
        return row;

    }

    private static class ViewHolder {
        public TextView message;
        public TextView time;
        private TextView messageReveive;
        private TextView timeReceive;
        private LinearLayout layoutReceive;
        private LinearLayout layoutSend;
        private ImageView alert;

    }


}

