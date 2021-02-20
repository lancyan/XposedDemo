package com.wrbug.xposeddemo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class ProviderHelper {

    static Uri uri_test = Uri.parse("content://com.wrbug.xposeddemo.myprovider/test");

    public static int getIndex(Context context) {
        ContentResolver resolver = context.getContentResolver();
        int index = 0;
        Cursor cursor = resolver.query(uri_test, new String[]{"_id", "idx"}, "_id=?", new String[]{"1"}, null);
        if (cursor.moveToNext()) {
            //System.out.println("query test: _id:" + cursor21.getInt(0) +",idx:"+ cursor21.getString(1));
            index = cursor.getInt(1);
        }
        cursor.close();
        return index;
    }

    public static int update(Context context, int val) {
        //Cursor cursor = resolver.query(uri_user, new String[]{"_id","name"}, null, null, null);
        //和上述类似,只是URI需要更改,从而匹配不同的URI CODE,从而找到不同的数据资源
        //Uri uri_test = Uri.parse("content://com.wrbug.xposeddemo.myprovider/test");

        ContentValues values21 = new ContentValues();
        values21.put("idx", val);

        ContentResolver resolver21 = context.getContentResolver();
        return resolver21.update(uri_test, values21, "_id=?", new String[]{"1"});

        //Cursor cursor21 = resolver21.query(uri_test, new String[]{"_id","idx"}, null, null, null);
        //while (cursor21.moveToNext()){
        //    System.out.println("query test: _id:" + cursor21.getInt(0) +",idx:"+ cursor21.getString(1));
        //}
        //cursor21.close();

    }
}
