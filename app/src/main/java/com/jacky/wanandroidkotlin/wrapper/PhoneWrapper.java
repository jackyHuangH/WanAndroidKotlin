package com.jacky.wanandroidkotlin.wrapper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * @author:Hzj
 * @date :2022/9/13
 * desc  ：测试 监听来电和短信
 * record：
 */
public class PhoneWrapper {

    /**
     * 获取来电号码对应的联系人名称
     * @param context        上下文
     * @param phoneNumber   电话号码
     * @return    联系人名称（如果来电号码没有对应联系人名称，则直接返回电话号码）
     */
    public static String getIncomingCallName(Context context, String phoneNumber){
        //格式化电话号码
        if(phoneNumber == null){
            return null;
        }
        phoneNumber = phoneNumber.trim().replace(" ","").replace("-","");

        //是否查询到
        boolean isSuccess = false;
        String phoneName = null;

        //联系人的Uri，也就是content://com.android.contacts/contacts
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //指定获取_id和display_name两列数据，display_name即为姓名
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        //根据Uri查询相应的ContentProvider，cursor为获取到的数据集
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    Long id = cursor.getLong(0);
                    //获取姓名
                    String name = cursor.getString(1);
                    //指定获取NUMBER这一列数据
                    String[] phoneProjection = new String[]{
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    };

                    //根据联系人的ID获取此人的电话号码
                    Cursor phonesCursor = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            phoneProjection,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                            null,
                            null);

                    //因为每个联系人可能有多个电话号码，所以需要遍历
                    if (phonesCursor != null && phonesCursor.moveToFirst()) {
                        do {
                            String num = phonesCursor.getString(0);
                            num = num.trim().replace(" ","").replace("-","");
                            if(num.equals(phoneNumber)){
                                //查询到号码
                                isSuccess = true;
                                phoneName = name;
                                break;  //退出
                            }
                        } while (phonesCursor.moveToNext());
                    }

                    //已经找到，退出大循环
                    if(isSuccess){
                        break;
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        //如果来电号码没有对应联系人名称，则直接返回电话号码
        if(isSuccess) {
            return phoneName;
        }else{
            return phoneNumber;
        }
    }

}
