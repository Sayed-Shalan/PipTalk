package com.pip.talk.pip.pc.piptalk.chat;

import android.util.Log;

import java.util.Random;



public class ChatMessage {

    public String mMessageBody,
            mSenderUserName,
            mSenderUserToken, mReceiverUserName,mReceiverUserToken;
    public String Date, Time;
    public String msgId;
    public String msg_lang_code;
    public String userContact;
    public String userContactNum;
    public boolean fromOffline;


    public void setmSenderUserToken(String mSenderUserToken) {
        this.mSenderUserToken = mSenderUserToken;
    }

    public void setmReceiverUserToken(String mReceiverUserToken) {
        this.mReceiverUserToken = mReceiverUserToken;
    }

    public void setFromOffline(boolean fromOffline) {
        this.fromOffline = fromOffline;
    }

    public void setMsg_lang_code(String msg_lang_code) {
        this.msg_lang_code = msg_lang_code;
    }

    public String getMsg_lang_code() {
        return msg_lang_code;
    }

    public boolean isFromOffline() {
        return fromOffline;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public void setUserContactNum(String userContactNum) {
        this.userContactNum = userContactNum;
    }

    public String getUserContact() {
        return userContact;
    }

    public String getUserContactNum() {
        return userContactNum;
    }




    String contactpp, sender_has_image, receriver_has_image;
    int sender_id,
            receiver_id;

    public void setSender_has_image(String sender_has_image) {
        this.sender_has_image = sender_has_image;
    }

    public void setReceriver_has_image(String receriver_has_image) {
        this.receriver_has_image = receriver_has_image;
    }

    public String getSender_has_image() {
        return sender_has_image;
    }

    public String getReceriver_has_image() {
        return receriver_has_image;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getContactpp() {
        return contactpp;
    }

    public void setContactpp(String contactpp) {
        this.contactpp = contactpp;
    }

    public boolean isMine;// Did I send the message.

    public String getmMessageBody() {
        return mMessageBody;
    }

    public boolean isMine() {
        return isMine;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public String getDate() {
        return Date;
    }

    public String getmReceiverUserToken() {
        return mReceiverUserToken;
    }

    public void setmReceiverUserName(String receiverUserName){
        mReceiverUserName= receiverUserName;
    }

    public String getmReceiverUserName() {
        return mReceiverUserName;
    }

    public String getmSenderUserToken() {
        return mSenderUserToken;
    }

    public void setmSenderUserName(String userName){
        mSenderUserName= userName;
    }

    public void setMessageBody(String msg){
        mMessageBody= msg;
    }
    public void setMsgId(String msgID){
        msgId= msgID;
    }

    public String getmSenderUserName() {
        return mSenderUserName;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }



    public ChatMessage(String SenderUsername,
                       String senderToken,
                       String receiverUsername,
                       String receiverToken,
                       String messageString,
                       String MessageId,
                       boolean isMINE) {
        mSenderUserName = SenderUsername;
        mSenderUserToken = senderToken;
        mReceiverUserName = receiverUsername;
        mReceiverUserToken = receiverToken;
        mMessageBody = messageString;
        msgId = MessageId;
        Log.e("CHAT"," coming IS MINE ????  : "+isMINE);
        this.isMine = isMINE;
    }

    public ChatMessage(String SenderUsername,
                       String receiverUsername,
                       String messageString,
                       String MessageId,
                       boolean isMINE) {
        mSenderUserName = SenderUsername;
        mReceiverUserName = receiverUsername;
        mMessageBody = messageString;
        msgId = MessageId;
        this.isMine = isMINE;
    }


    public ChatMessage(String mMessageBody,String msgId,String time,String date, int sender_id,int receiver_id) {
        this.mMessageBody=mMessageBody;
        this.msgId=msgId;
        this.Time=time;
        this.Date=date;
        this.sender_id=sender_id;
        this.receiver_id=receiver_id;
    }

    public ChatMessage() {
    }

    public void setMsgID() {
        msgId += "-" + String.format("%02d", new Random().nextInt(100));
    }
}
