package com.example.administrator.bluetest;

/**
 * Created by Administrator on 2017/9/27.
 */

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static String oldMsg;
    private static Toast mToast,mToastNormal;
    private static long time;

    public static void showToast(Context context, String msg, int duration) {
            if (!msg.equals(oldMsg)) { // 当显示的内容不一样时，即断定为不是同一个Toast
                mToastNormal = Toast.makeText(context.getApplicationContext(), msg, duration);
                time = System.currentTimeMillis();
            } else {
                //ToastUtil.stop();
                // 显示内容一样时，只有间隔时间大于2秒时才显示
                if (System.currentTimeMillis() - time > 1000) {
                    mToastNormal = Toast.makeText(context.getApplicationContext(), msg, duration);
                    time = System.currentTimeMillis();
                }
            }
            if(mToastNormal!=null){
                mToastNormal.show();
            }

        oldMsg = msg;
        //System.out.println("弹出时："+mToastNormal.toString().length());
    }
    public static void cancel(){
       // System.out.println("消失时："+mToastNormal.toString().length());
        if(mToastNormal != null){
            System.out.println("消失没"+mToastNormal.getView());
            mToastNormal.cancel();
           mToastNormal = null;
        }

    }


}
