package com.pip.talk.pip.pc.piptalk.search;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pip.talk.pip.pc.piptalk.R;
import com.pip.talk.pip.pc.piptalk.User_Model.CircleTransform;
import com.pip.talk.pip.pc.piptalk.User_Model.UserModel;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import java.util.ArrayList;

public class SuggestCursorAdapter extends android.support.v4.widget.CursorAdapter {
    private ArrayList<UserModel> items = new ArrayList<>();
    String username ="";
    String URL="http://"+ Util.WAMP_SERVER_DOMAIN+"/piptalk/images/";

    public SuggestCursorAdapter(Context context, Cursor cursor, ArrayList<UserModel> items) {
        super(context, cursor, false);
        this.items = items;
    }

    public void changeData(ArrayList<UserModel> array){

        items = array;
        notifyDataSetChanged();
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }

    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        Log.e("TT","cursor position >> "+cursor.getPosition());


        username = items.get(cursor.getPosition()).getUser_name().toString();
        if(username.length()>17){
            holder.textView.setText(username.substring(0,16)+"...");
        }else {
            holder.textView.setText(username);
        }


            Typeface ralewayName = Typeface.createFromAsset(mContext.getAssets(),"Raleway-Bold.ttf");
            holder.textView.setTypeface(ralewayName);

        if (items.get(cursor.getPosition()).getImage().equals("1")){
            Glide.with(mContext)
                    .load(URL +String.valueOf(items.get(cursor.getPosition()).getId()+".png")) // add your image url
                    .transform(new CircleTransform(mContext)) // applying the image transformer
                    .into(holder.imageView);
        }else{
            Glide.with(mContext)
                    .load(R.drawable.unknown_male) // add your image url
                    .transform(new CircleTransform(mContext)) // applying the image transformer
                    .into(holder.imageView);
        }
           /* Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unknown_male);
            Bitmap resized = Bitmap.createScaledBitmap(bm,(int)(bm.getWidth()*0.1), (int)(bm.getHeight()*0.1), true);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(mContext.getResources(),resized);
            drawable.setCircular(true);
            holder.imageView.setImageDrawable(drawable);*/





    }

    public class ViewHolder {
        public TextView textView;
        public ImageView imageView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View v = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.search_result_item, parent, false);
        holder.textView = (TextView) v.findViewById(R.id.foundContactName);
        holder.imageView = (ImageView)v.findViewById(R.id.foundContactImage);

        v.setTag(holder);
        return v;

    }


}
