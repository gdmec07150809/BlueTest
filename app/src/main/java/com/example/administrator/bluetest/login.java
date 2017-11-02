package com.example.administrator.bluetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/9/19.
 */

public class login extends Activity{
    private Button login;
    private EditText password,user;
    public static final int SHOW_RESPONSE = 0;
    private TextView list;
    SharedPreferences sp=null;//定义储存源，备用
    CheckBox check;
    String name1,pass1;

    private long clickTime=0;

    //重写onKeyDown方法,实现双击退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再次点击退出",  Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            Log.e(TAG, "exit application");
            this.finish();
            System.exit(0);
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login=findViewById(R.id.login);
        password=findViewById(R.id.pass);
        user=findViewById(R.id.name);
        check = (CheckBox) findViewById(R.id.check);

        sp = this.getSharedPreferences("userdata", Context.MODE_WORLD_READABLE);//将数据储存在userdata.xml中
        //判断是否记住密码
        if(sp.getBoolean("IsCheck",true)){
            //设置默认是记录密码状态
            check.setChecked(true);
            user.setText(sp.getString("user", ""));
            password.setText(sp.getString("password", ""));
        }

        //list=findViewById(R.id.list);
        //登录按钮事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否为空
                if(password.getText().toString().equals("")|| user.getText().toString().equals("")){
                    new AlertDialog.Builder(login.this).setTitle("登录提示").setMessage("用户名或密码不能为空！！！").setPositiveButton("确定",null).show();
                }else{
//                 Intent  intent=new Intent(login.this,DeviceScanActivity.class);
//                   startActivity(intent);

                    name1=user.getText().toString();
                    pass1=password.getText().toString();
                    sendRequestWithHttpClient(name1,pass1);
                }

            }
        });
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (check.isChecked()) {
            System.out.println("记住密码已选中");
            sp.edit().putBoolean("IsCheck", true).commit();
        }else {
            System.out.println("记住密码没有选中");
            sp.edit().putBoolean("IsCheck", false).commit();
        }
    }
    //20170821登录验证类，获取手机号和密码
    //方法：发送网络请求，获取百度首页的数据。在里面开启线程
    private void sendRequestWithHttpClient(final String name1, final String pass1) {

              new Thread(new Runnable() {

                   @Override
          public void run() {
                        //用HttpClient发送请求，分为五步
                            //第一步：创建HttpClient对象
                          HttpClient httpCient = new DefaultHttpClient();
                         //第二步：创建代表请求的对象,参数是访问的服务器地址blMac=12:34:56:78:9A:BC&userId=1001&userName=%E5%BC%A0%E4%B8%89
                       // HttpGet httpGet = new HttpGet("http://www.ding-new.com/wifiBlutooth/blutoothLogin.do?code="+name1+"&password="+pass1);
                       HttpGet httpGet = new HttpGet("http://www.java-go.cn/wifiBlutooth/blutoothLogin.do?code="+name1+"&password="+pass1);

                       //HttpGet httpGet = new HttpGet("http://192.168.1.102:8080/EasyCCC/wifiBlutooth/blutoothLogin.do?code="+name1+"&password="+pass1);




                          try {
                                   //第三步：执行请求，获取服务器发还的相应对象
                                     HttpResponse httpResponse = httpCient.execute(httpGet);
                                      //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                                   if (httpResponse.getStatusLine().getStatusCode() == 200) {
                                        //第五步：从相应对象当中取出数据，放到entity当中
                                              HttpEntity entity = httpResponse.getEntity();
                                             String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串
                                            if(response!=null){
                                                //在子线程中将Message对象发出去
                                                Message message = new Message();
                                                message.what = SHOW_RESPONSE;
                                                message.obj = response.toString();
                                                handler.sendMessage(message);
                                            }
                                          }else{
                                       Toast.makeText(login.this,"访问失败!!!请检查服务器...",Toast.LENGTH_LONG).show();
                                   }

                                   } catch (Exception e) {
                                      // TODO Auto-generated catch block
                                    System.out.println("访问失败！！！");
                                    e.printStackTrace();
                                 }

                           }
        }).start();//这个start()方法不要忘记了

           }
    private Handler handler = new Handler() {

             @Override
        public void handleMessage(Message msg) {
                     super.handleMessage(msg);
                    switch (msg.what) {
                          case SHOW_RESPONSE:
                                  String response = (String) msg.obj;
                              if(response.indexOf("success")==-1){
                                  new AlertDialog.Builder(login.this).setTitle("登录提示").setMessage("用户名或密码错误！！！").setPositiveButton("确定",null).show();
                              }else{
                                  try {
                                      JSONObject object=new JSONObject(response);
                                      String locationId =object.getString("locationId");
                                      String name =object.getString("name");
                                      String id =object.getString("id");
                                      String locationName =object.getString("locationName");
//                                      String blName =object.getString("blName");
//                                      String blName =object.getString("blName");
                                     System.out.println(locationId+":"+name+":"+id);
                                      if(check.isChecked())
                                      {
                                          //记住用户名、密码、
                                          SharedPreferences.Editor editor = sp.edit();
                                          editor.putString("user", name1);
                                          editor.putString("password",pass1);
                                          editor.commit();
                                      }else{

                                          //不记住密码,则给输入框赋值为空
                                          SharedPreferences.Editor editor = sp.edit();
                                          editor.putString("user", "");
                                          editor.putString("password","");
                                          check.setChecked(false);
                                          editor.commit();
                                      }
                                      // Intent  intent=new Intent(login.this,DeviceScanActivity.class);
                                      Intent  intent=new Intent(login.this,DeviceListActivity.class);
                                      //用Bundle携带数据
                                      Bundle bundle=new Bundle();
                                      //传递name参数为tinyphp
                                      bundle.putString("name", name);
                                      bundle.putString("id", id);
                                      bundle.putString("locationId", locationId);
                                      bundle.putString("locationName", locationName);
                                      intent.putExtras(bundle);
                                      //跳转
                                      startActivity(intent);
                                      finish();
                                  } catch (JSONException e) {
                                      e.printStackTrace();
                                  }
                                  //list.setText(response);

                              }
                               break;

                         default:
                                break;
                          }
                  }

            };
}

