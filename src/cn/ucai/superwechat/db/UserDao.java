package cn.ucai.superwechat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.bean.User;

/**
 * Created by sks on 2016/5/19.
 */
public class UserDao extends SQLiteOpenHelper {
    public static final String Id = "_id";
    public static final String TABLE_NAME = "user";

    public UserDao(Context context) {
        super(context,"user.db",null,1);
    }

    public UserDao(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);



    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists" + I.User.TABLE_NAME + "(" +
                I.User.USER_ID + "integer primary key autoincrement," +
                I.User.TABLE_NAME + "varchar unique not null" +
                I.User.NICK + "varchar," +
                I.User.PASSWORD + "varchar," +
                I.User.UN_READ_MSG_COUNT + "int default(0)" +
                ");";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(I.User.USER_ID, user.getMUserId());
        values.put(I.User.USER_NAME, user.getMUserName());
        values.put(I.User.NICK, user.getMUserNick());
        values.put(I.User.PASSWORD, user.getMUserPassword());
        values.put(I.User.UN_READ_MSG_COUNT, user.getMUserUnreadMsgCount());
        SQLiteDatabase db = getWritableDatabase();
        long insert = db.insert(I.User.TABLE_NAME, null, values);
        return insert>0;

    }

    public boolean updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(I.User.USER_ID, user.getMUserId());
        values.put(I.User.NICK, user.getMUserNick());
        values.put(I.User.PASSWORD, user.getMUserPassword());
        values.put(I.User.UN_READ_MSG_COUNT, user.getMUserUnreadMsgCount());
        SQLiteDatabase db = getWritableDatabase();
        long insert = db.update(I.User.TABLE_NAME, values, "where" + I.User.USER_NAME + "=?", new String[]{user.getMUserName()});
        return insert>0;
    }

    public User finddUserByUserName(String userName) {

        SQLiteDatabase db = getReadableDatabase();
        String sql= "select * from "+ TABLE_NAME +"where"+I.User.USER_NAME+"=?";
        Cursor c = db.rawQuery(sql, new String[]{userName});
        if (c.moveToNext()) {
            int uid = c.getInt(c.getColumnIndex(I.User.USER_ID));
            String nick = c.getString(c.getColumnIndex(I.User.NICK));
            String password = c.getString(c.getColumnIndex(I.User.PASSWORD));
            int unReaderMsgCount = c.getInt(c.getColumnIndex(I.User.UN_READ_MSG_COUNT));
            return new User(uid,userName,password,nick,unReaderMsgCount);

        }
        c.close();
        return null;
    }
}
