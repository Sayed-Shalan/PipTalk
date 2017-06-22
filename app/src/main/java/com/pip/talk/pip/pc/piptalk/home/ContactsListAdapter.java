package com.pip.talk.pip.pc.piptalk.home;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.User_Model.CircleTransform;
import com.pip.talk.pip.pc.piptalk.chat.ChatMessage;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import java.util.ArrayList;


public class ContactsListAdapter extends ArrayAdapter<ChatMessage> {

    ArrayList<ChatMessage> items=new ArrayList<>();
    // ArrayList<String> lastMessgae = new ArrayList<>();
  //  private boolean newMessage= false;
   // private int position_ofNmsg;
    private LayoutInflater inflater;
    private Context mContext;
    String URL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/images/";

    public ContactsListAdapter(Context context, int source, ArrayList<ChatMessage> list) {
        super(context,source,list);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        items=list;
    }

    void sendMessageToAdapter(ArrayList<ChatMessage> list) {
        Log.e("II","inside adapter, list size is : "+list.size());
        items = list;
        //  lastMessgae.clear(); // we should change this in order to get the last message from online database too
        notifyDataSetChanged();
    }

//    void addMessageToAdapter(ChatMessage item) {
//        items.add(item) ;
//        //  lastMessgae.clear(); // we should change this in order to get the last message from online database too
//        notifyDataSetChanged();
//    }

    void notifyData(int pos) {
////        newMessage = true;
////        position_ofNmsg= pos;
//        Log.e("CC","inside notify data , newMessage is : ( "+newMessage + " )  and position is :" +position_ofNmsg);
        notifyDataSetChanged();
    }
//    void sendLastMessageAndContactsToAdapter(ArrayList<ChatMessage> list, ArrayList<String> lastMsg) {
//        items = list;
//     //   lastMessgae =lastMsg;
//        notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            holder = new ViewHolder();
            row = inflater.inflate(R.layout.contact_list_item, parent, false);
            holder.imageView = (ImageView)row.findViewById(R.id.contactProfileImage);
            holder.name = (TextView) row.findViewById(R.id.contactName);
            holder.lastMessage = (TextView) row.findViewById(R.id.lastMessage);
          //  holder.newMessagesNum = (TextView) row.findViewById(R.id.new_msg);
            holder.messageTime = (TextView) row.findViewById(R.id.lastMessageTime);

            row.setTag(holder);
        } else {
            holder = (ViewHolder)row.getTag();
        }


        Typeface ralewayName = Typeface.createFromAsset(mContext.getAssets(),"Raleway-Bold.ttf");
        Typeface ralewayMessage = Typeface.createFromAsset(mContext.getAssets(),"Raleway-Regular.ttf");

        holder.name.setTypeface(ralewayName);
        holder.lastMessage.setTypeface(ralewayMessage);
        holder.messageTime.setTypeface(ralewayMessage);
      //  holder.newMessagesNum.setTypeface(ralewayMessage);

        ChatMessage model=items.get(position);

        if(model.isFromOffline()){ /// there should be cashing
            Glide.with(mContext)
                    .load(R.drawable.unknown_male) // add your image url
                    .transform(new CircleTransform(mContext)) // applying the image transformer
                    .into(holder.imageView);
        }else{
            if(model.getContactpp().equals("0")) {
                Glide.with(mContext)
                        .load(R.drawable.unknown_male) // add your image url
                        .transform(new CircleTransform(mContext)) // applying the image transformer
                        .into(holder.imageView);
            }else {
                if (model.isMine()) {
                    Glide.with(mContext)
                            .load(URL +String.valueOf(model.getReceiver_id())+".png") // add your image url
                            .transform(new CircleTransform(mContext)) // applying the image transformer
                            .into(holder.imageView);
                }else{
                    Glide.with(mContext)
                            .load(URL +String.valueOf(model.getSender_id())+".png") // add your image url
                            .transform(new CircleTransform(mContext)) // applying the image transformer
                            .into(holder.imageView);
                }
            }
        }


        holder.lastMessage.setText(model.getmMessageBody());
        holder.messageTime.setText(model.getTime());

//        Log.e("CC","inside getView , newMessage is : ( "+newMessage + " ) and position is :" +position_ofNmsg+
//        "and current position is "+position);

//        if(newMessage && position_ofNmsg == position){
//            holder.newMessagesNum.setVisibility(View.VISIBLE);
//            holder.lastMessage.setTypeface(ralewayMessage,Typeface.BOLD);
//            holder.lastMessage.setTypeface(ralewayMessage,Typeface.BOLD);
//            holder.newMessagesNum.setText("1");
//            if(holder.newMessagesNum.isShown()){
//                Log.e("CC","inside if, note shown!");
//            }else{
//                Log.e("CC","inside if, note is NOT shown!");
//            }
//            newMessage=false;
//        }else {
//            Log.e("CC","inside else ");
//        }


//        if (model.isMine()){
        holder.name.setText(model.getUserContact());
//        }else{
//            holder.name.setText(model.getmSenderUserName());
//        }

        Log.e("II","Inside adapter, message : "+model.getmMessageBody());
        Log.e("II","Inside adapter, sender name : "+ model.getmSenderUserName());
        Log.e("II","Inside adapter, receiver name : "+model.getmReceiverUserName());




        return row;
    }
    static class ViewHolder {
        public ImageView imageView;
        public TextView name;
        public TextView lastMessage;
        public TextView messageTime;
        //public TextView newMessagesNum;

    }









}
