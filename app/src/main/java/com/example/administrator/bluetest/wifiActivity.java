package com.example.administrator.bluetest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class wifiActivity extends Activity implements LocationListener {

    private List<ScanResult> scanResults=null;
    private List<ScanResult> scanResultsNew=null;
    private ListView listView;
    private WifiAdapter wifiAdapter;
    private Button five_s_btn, one_min_btn, exit_btn;
    private ImageView come;
    private int index = 0;
    String locationId;
    public String longitude;
    public String latitude;
    private  String userId=null;
    private  String postName=null;
    private ImageView setting_back;
    public static List<String> blMacsList=new ArrayList<String>();
    static List<String> BbsidList=new ArrayList<>();
    public static final int SHOW_RESPONSE = 0;
    String name,id,locationName;

    private LocationManager locationManager;
    private String provider;
    private Location location;
    JSONObject object;
    JSONArray jsonArray = null;
    long endTime = 0;
    long startTime=0;
    private Toast toast;
    //权限检测类
    private PermissionHelper mPermissionHelper;

    public static final int ACCESS_FINE_LOCATION_CODE = 1;//SDcard权限
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    public static final int ACCESS_COARSE_LOCATION_CODE = 2;//SDcard权限
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public static final int ACCESS_WIFI_STATE_CODE = 3;//SDcard权限
    public static final String ACCESS_WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;
    private WifiManager wifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_list);
        Bundle bundle = this.getIntent().getExtras();
         name = bundle.getString("name");
        id = bundle.getString("id");
        locationName = bundle.getString("locationName");
        locationId = bundle.getString("locationId");
        longitude = bundle.getString("longitude");
        latitude = bundle.getString("latitude");

        //userName.setText("欢迎 "+locationName+" "+name);
        userId=id;
        postName=name;

        //判断WIFI是否打开,未打开则强制跳到wifi设置界面
        WifiManager wManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wManager.isWifiEnabled()
                && wManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
            Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            startActivityForResult(intent, 0);
        }

        mPermissionHelper = new PermissionHelper(this);

        one_handler.sendEmptyMessage(1);

        setting_back=findViewById(R.id.settings_back);
        //添加返回按钮的监听
        setting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(wifiActivity.this,DeviceListActivity.class);
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
//                bundle.putString("longitude", longitude);
//                bundle.putString("latitude", latitude);
                //将name，id，locationId，locationName的值传到下个Activity
                bundle.putString("name", name);
                bundle.putString("id", id);
                bundle.putString("locationId", locationId);
                bundle.putString("locationName", locationName);

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtras(bundle);
                //toast.cancel();
                ToastUtil.cancel();
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());//按返回按钮时,结束本Activity的运行



            }
        });

        //wifi管理器
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        listView = (ListView) findViewById(R.id.listView);
        scanResults = new ArrayList<>();//WIFI列表
        scanResultsNew= new ArrayList<>();
        for (int i=0;i<scanResults.size();i++){
            System.out.println("wifi设备："+scanResults);
            if(!scanResultsNew.contains(scanResults.get(i))){
                scanResultsNew.add(scanResults.get(i));
            }
        }
//        HashSet h = new HashSet(scanResults);
//        scanResults.clear();
//        scanResults.addAll(h);

        wifiAdapter = new WifiAdapter(wifiActivity.this, scanResultsNew);
        listView.setAdapter(wifiAdapter);





        //five_s_btn = (Button) findViewById(R.id.five_s_btn);
       // one_min_btn = (Button) findViewById(R.id.one_min_btn);
       // exit_btn = (Button) findViewById(R.id.exit_btn);
       // come= (ImageView) findViewById(R.id.come);

        //five_s_btn.setBackgroundResource(R.drawable.red_click_button);
        //one_min_btn.setBackgroundResource(R.drawable.button_click);
       // five_s_btn.setClickable(true);
       // one_min_btn.setClickable(false);

//        come.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(MainActivity.this,BigActivity.class);
//                startActivity(intent);
//            }
//        });
//        five_s_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                index = 0;
//                Toast.makeText(wifiActivity.this, "Refresh 5 seconds", Toast.LENGTH_SHORT).show();
//                five_s_btn.setBackgroundResource(R.drawable.red_click_button);
//                one_min_btn.setBackgroundResource(R.drawable.button_click);
//                five_s_btn.setClickable(false);
//                one_min_btn.setClickable(true);
//            }
//        });
//
//        one_min_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                index = 1;
//                Toast.makeText(wifiActivity.this, "Refresh 1 Minute", Toast.LENGTH_SHORT).show();
//                one_min_btn.setBackgroundResource(R.drawable.red_click_button);
//                five_s_btn.setBackgroundResource(R.drawable.button_click);
//                five_s_btn.setClickable(true);
//                one_min_btn.setClickable(false);
//            }
//        });

//        exit_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent home = new Intent(Intent.ACTION_MAIN);
//                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                home.addCategory(Intent.CATEGORY_HOME);
//                startActivity(home);
//            }
//        });

        //位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // String provider = LocationManager.GPS_PROVIDER;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setBearingAccuracy(criteria.ACCURACY_HIGH);
        criteria.setHorizontalAccuracy(criteria.ACCURACY_HIGH);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);
        locationManager.requestLocationUpdates(provider, 2000, 0, this);


        handlerTimer.sendEmptyMessageDelayed(0, 1000);//启动handler，进入蓝牙列表界面1秒后执行
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        android.os.Process.killProcess(android.os.Process.myPid());//点击手机返回键时,结束本Activity的运行
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ToastUtil.cancel();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.cancel();

    }
    //获取位置信息
    private void updateWithNewLocation(Location location) {
        String latLongString;

        if (location != null) {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
//            float spe = location.getSpeed();// 速度
//            float acc = location.getAccuracy();// 精度
//            double alt = location.getAltitude();// 海拔
//            float bea = location.getBearing();// 轴承
//            long tim = location.getTime();// 返回UTC时间1970年1月1毫秒
//            latLongString = "纬度:" + lat + "\n经度:" + lng + "\n精度：" + acc
//                    + "\n速度：" + spe + "\n海拔：" + alt + "\n轴承：" + bea + "\n时间："
//                    + sdf.format(tim);
        } else {
            latLongString = "无法获取位置信息";
        }
        System.out.println("经度：" +longitude +"\n"+"纬度："+latitude);
//        System.out.println(longitude.split(".",6).toString());
//        if(longitude.split(".",6).length<7){
//            longitude=longitude+"000";
//            latitude=latitude+"000";
//        }

    }
    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        updateWithNewLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        updateWithNewLocation(null);
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    Runnable r = new Runnable() {
        @Override
        public void run() {

            wifiAdapter = new WifiAdapter(wifiActivity.this, scanResults);
            Log.e("@@@", "refresh");
            listView.setAdapter(wifiAdapter);
        }
    };

    boolean pression = false;

    private Handler handlerTimer = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void handleMessage(android.os.Message msg) {
            System.out.println("调用定时器");
            //toast1=Toast.makeText(wifiActivity.this,"开始发送请求",Toast.LENGTH_SHORT);
            //toast1.show();
            if(scanResults.size()>0){
                System.out.println("开始发送请求");
                ToastUtil.showToast(wifiActivity.this,"开始发送请求",Toast.LENGTH_SHORT);
//                toast=Toast.makeText(wifiActivity.this,"开始发送请求",Toast.LENGTH_SHORT);
//                toast.setText("开始发送请求");
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
            }

            System.out.println("wifi列表Id: "+locationId);
            sendRequestWithHttpClient(locationId);//开始发送请求

                //System.out.println("   if   "+blMacsList.get(0));



            handlerTimer.sendEmptyMessageDelayed(0,1000*60*15);//15分钟后再次执行

        }
    };


    //发送请求获取blMAC
    private void sendRequestWithHttpClient(final String locationId) {
        //toast1=Toast.makeText(wifiActivity.this,"正在获取要配对的blMAC,请稍等...",Toast.LENGTH_SHORT);
        //toast1.show();
        if(scanResults.size()>0){
            ToastUtil.showToast(wifiActivity.this,"正在获取要配对的blMAC,请稍等...",Toast.LENGTH_SHORT);
//            toast.setText("正在获取要配对的blMAC,请稍等...");
//            toast.setDuration(Toast.LENGTH_SHORT);
//            toast.show();
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                HttpClient httpCient = new DefaultHttpClient();
                //第二步：创建代表请求的对象,参数是访问的服务器地址blMac=12:34:56:78:9A:BC&userId=1001&userName=%E5%BC%A0%E4%B8%89
               // HttpGet httpGet = new HttpGet("http://www.ding-new.com/wifiBlutooth/blutoothList.do?locationId="+locationId);
                //HttpGet httpGet = new HttpGet("http://192.168.1.102:8080/EasyCCC/wifiBlutooth/blutoothList.do?locationId="+locationId);
               HttpGet httpGet = new HttpGet("http://www.java-go.cn/wifiBlutooth/blutoothList.do?locationId="+locationId);

                try {
                    //第三步：执行请求，获取服务器发还的相应对象
                    HttpResponse httpResponse = httpCient.execute(httpGet);
                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第五步：从相应对象当中取出数据，放到entity当中
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串

                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = response.toString();
                        System.out.println("请求！！！");
                        handler.sendMessage(message);
                    }else{
                        Toast.makeText(wifiActivity.this,"请求不成功",Toast.LENGTH_LONG);
                        //toast.show();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();//这个start()方法不要忘记了

    }
    //获取需要配对的blMac
    private Handler handler = new Handler() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_RESPONSE:

                    String response = (String) msg.obj;
                    System.out.println(response);


                    try {
                        jsonArray = new JSONArray(response);
                        System.out.println("jsonArray:"+jsonArray);
                        for(int i=0;i<jsonArray.length();i++){
                             object=new JSONObject(jsonArray.get(i)+"");
                            System.out.println("_________________________");
                            System.out.println("object:"+object.getString("blMac"));
                            blMacsList.add(object.getString("blMac"));
                            System.out.println("请求回来的MAC:  "+blMacsList.get(i));
                            //Toast.makeText(wifiActivity.this,"正在同步...",Toast.LENGTH_SHORT).show();
                        }
                        if(blMacsList.size()<1){
                            Toast.makeText(wifiActivity.this,"没有设备需要配对,可以休息了...",Toast.LENGTH_SHORT).show();
                        }
                        //System.out.println(object.getString("blMac"));
                        matching(blMacsList,BbsidList,jsonArray);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
        }

    };

    private void matching(List<String> blMacList, List<String> BbsidList,JSONArray jsonArray){
        System.out.println("进来");

        int length=blMacList.size();
        if(latitude!=null){
            if(scanResults.size()>0){
            ToastUtil.showToast(wifiActivity.this,"定位经纬度："+longitude+" : "+latitude,Toast.LENGTH_SHORT);
            }
            //toast=Toast.makeText(this,"定位经纬度："+longitude+" : "+latitude,Toast.LENGTH_LONG);

            startTime=System.currentTimeMillis();

            for(int i=0;i<length;i++){
                if(BbsidList.contains(blMacList.get(i))){
                    System.out.println("找到设备："+blMacList.get(i));
                    //配对成功,给后台发送更新信息
                    sendRequestWithHttpClient1(blMacList.get(i),postName,userId);

                }else{
                    System.out.println("没有找到设备："+blMacList.get(i));
                    JSONObject object1= null;
                    try {
                        object1 = new JSONObject(jsonArray.get(i)+"");
                       String blId= object1.getString("blId");
                        System.out.println("blId:"+blId);

                        // toast=  Toast.makeText(wifiActivity.this,"正在初始化未搜索到的设备",Toast.LENGTH_LONG);
                        if(scanResults.size()>0){
                            ToastUtil.showToast(wifiActivity.this,"正在初始化未搜索到的设备",Toast.LENGTH_SHORT);
                        }


                        //给后台发送初始化指令
                        sendRequestWithHttpClient2(blId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //sendRequestWithHttpClient2(blMacsList.get(i),postName,userId);
                    //Toast.makeText(this,"没有找到设备："+blMacsList.get(i),Toast.LENGTH_LONG).show();
                }
            }
            //toast.show();

            endTime=System.currentTimeMillis();
            // myLocationText.setText("经度：" +longitude +"\n"+"纬度："+latitude);
            System.out.println("时间："+(endTime-startTime));
        }else{

            //Toast.makeText(this,"未定位到经纬度,请移动到空旷的地方,再进来!!!",Toast.LENGTH_SHORT).show();
            ToastUtil.showToast(wifiActivity.this,"未定位到经纬度,请移动到空旷的地方,再进来!!!",Toast.LENGTH_LONG);
        }
        BbsidList.clear();
        blMacList.clear();
    }
//更新联网操作
    private void sendRequestWithHttpClient1(final String blMac,final String postName,final String userId) {
        if(scanResults.size()>0){
            ToastUtil.showToast(wifiActivity.this,"请求设备"+blMac+",是否需要更新",Toast.LENGTH_SHORT);
        }
        //toast1=Toast.makeText(wifiActivity.this,"请求设备"+blMac+",是否需要更新",Toast.LENGTH_SHORT);

        //toast1.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                HttpClient httpCient = new DefaultHttpClient();
                //第二步：创建代表请求的对象,参数是访问的服务器地址blMac=12:34:56:78:9A:BC&userId=1001&userName=%E5%BC%A0%E4%B8%89
                //HttpGet httpGet = new HttpGet("http://www.ding-new.com/wifiBlutooth/updateBlutooth.do?blMac="+blMac+"&userId="+userId+"&userName="+postName+"&longitude="+longitude+"&latitude="+latitude);
                //HttpGet httpGet = new HttpGet("http://192.168.1.102:8080/EasyCCC/wifiBlutooth/updateBlutooth.do?blMac="+blMac+"&userId="+userId+"&userName="+postName+"&longitude="+longitude+"&latitude="+latitude);
                HttpGet httpGet = new HttpGet("http://www.java-go.cn/wifiBlutooth/updateBlutooth.do?blMac="+blMac+"&userId="+userId+"&userName="+postName+"&longitude="+longitude+"&latitude="+latitude);

                try {
                    //第三步：执行请求，获取服务器发还的相应对象
                    HttpResponse httpResponse = httpCient.execute(httpGet);
                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第五步：从相应对象当中取出数据，放到entity当中
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串
                        System.out.println("正在请求，设备:"+blMac);
                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = response.toString();
                        handler1.sendMessage(message);
                    }else{
                        Toast.makeText(wifiActivity.this,"请求不成功",Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();//这个start()方法不要忘记了

    }
//处理返回的数据
    private Handler handler1 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    System.out.println(response);

                    try {
                        JSONObject object=new JSONObject(response);
                        object.getString("message");
                        //System.out.println(object.getString("message"));
                        if(object.getString("message").equals("fail")){
                           // System.out.println("已确认");
                            //toast2=Toast.makeText(wifiActivity.this,"已确认",Toast.LENGTH_LONG);
                            if(scanResults.size()>0){
                                ToastUtil.showToast(wifiActivity.this,"已确认",Toast.LENGTH_SHORT);
                            }

                        }else if(object.getString("message").equals("success")){
                            //toast2=Toast.makeText(wifiActivity.this,"未确认",Toast.LENGTH_LONG);
                            if(scanResults.size()>0){
                                ToastUtil.showToast(wifiActivity.this,"未确认",Toast.LENGTH_SHORT);
                            }

                            //System.out.println("未确认");
                        }
                        //toast2.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//						try {
//							JSONArray object=new JSONArray(response);
//							String blMac =object.getString("blMac");
//							System.out.println(blMac);
//
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}


            }


        }

    };
//    private Handler star_handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (!pression){
//                starWifi();
//            }
//            star_handler.sendEmptyMessageDelayed(1, 1000);
//        }
//    };

//每隔1000*60*60时间刷新一次wifi列表
    private Handler one_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

                scanResults = new ArrayList<>();
            scanResultsNew= new ArrayList<>();
            for (int i=0;i<scanResults.size();i++){
                System.out.println("wifi设备："+scanResults);
                if(!scanResultsNew.contains(scanResults.get(i))){
                    scanResultsNew.add(scanResults.get(i));
                }
            }
                wifiAdapter = new WifiAdapter(wifiActivity.this, scanResultsNew);
                listView.setAdapter(wifiAdapter);
                scanResults = getAllNetWorkList(wifiActivity.this);
                one_handler.postDelayed(r, 100);
            one_handler.sendEmptyMessageDelayed(1, 1000*60*60);
        }
    };

//    private Handler five_handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (index == 0) {
//                scanResults = new ArrayList<>();
//                wifiAdapter = new WifiAdapter(wifiActivity.this, scanResults);
//                listView.setAdapter(wifiAdapter);
//                scanResults = getAllNetWorkList(wifiActivity.this);
//                five_handler.postDelayed(r, 100);
//            }
//            five_handler.sendEmptyMessageDelayed(1, 5000);
//        }
//    };


    //扫描wifi
    public static List<ScanResult> getAllNetWorkList(Context context) {
        WifiAdmin mWifiAdmin = new WifiAdmin(context);
        // 开始扫描网络
        mWifiAdmin.startScan();

        for (int i=0;i<mWifiAdmin.getWifiList().size();i++){
            BbsidList.add(mWifiAdmin.getWifiList().get(i).BSSID);
            //System.out.println("20秒获取： "+mWifiAdmin.getWifiList().get(i).BSSID);
        }
        return mWifiAdmin.getWifiList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作
                    //判断权限授权状态
                } else {
                    //如果请求失败
                    Toast.makeText(getApplicationContext(),"权限缺失，程序可能不能正常运行",Toast.LENGTH_SHORT).show();
                }
                break;
            case ACCESS_COARSE_LOCATION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作

                } else {
                    //如果请求失败
                    Toast.makeText(getApplicationContext(),"权限缺失，程序可能不能正常运行",Toast.LENGTH_SHORT).show();
                    mPermissionHelper.startAppSettings();
                }
                break;
            case ACCESS_WIFI_STATE_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作
                    //five_handler.sendEmptyMessage(1);
                    one_handler.sendEmptyMessage(1);
                    pression = true;
                } else {
                    //如果请求失败
                    Toast.makeText(getApplicationContext(),"权限缺失，程序可能不能正常运行",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void starWifi(){
        //判断权限授权状态
        boolean b = mPermissionHelper.checkPermission(ACCESS_FINE_LOCATION);
        //如果没有获取到权限,则尝试获取权限
        if (!b) {
            mPermissionHelper.permissionsCheck(ACCESS_FINE_LOCATION,ACCESS_FINE_LOCATION_CODE);
        } else {
            //如果请求成功，则进行相应的操作
            b = mPermissionHelper.checkPermission(ACCESS_COARSE_LOCATION);
            //如果没有获取到权限,则尝试获取权限
            if (!b) {
                mPermissionHelper.permissionsCheck(ACCESS_COARSE_LOCATION,ACCESS_COARSE_LOCATION_CODE);
            } else {
                //如果请求成功，则进行相应的操作
                b = mPermissionHelper.checkPermission(ACCESS_WIFI_STATE);
                //如果没有获取到权限,则尝试获取权限
                if (!b) {
                    mPermissionHelper.permissionsCheck(ACCESS_WIFI_STATE,ACCESS_WIFI_STATE_CODE);
                } else {
                    //如果请求成功，则进行相应的操作
                    //five_handler.sendEmptyMessage(1);
                    one_handler.sendEmptyMessage(1);
                    pression = true;
                }
            }
        }
    }
    //进行初始化联网处理
    private void sendRequestWithHttpClient2(final String blId) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                HttpClient httpCient = new DefaultHttpClient();
                //第二步：创建代表请求的对象,参数是访问的服务器地址blMac=12:34:56:78:9A:BC&userId=1001&userName=%E5%BC%A0%E4%B8%89
                //HttpGet httpGet = new HttpGet("http://www.ding-new.com/wifiBlutooth/updateBlutooth.do?blMac="+blMac+"&userId="+userId+"&userName="+postName+"&longitude="+longitude+"&latitude="+latitude);
                //HttpGet httpGet = new HttpGet("http://192.168.1.102:8080/EasyCCC/wifiBlutooth/updateBlutooth.do?blMac="+blMac+"&userId="+userId+"&userName="+postName+"&longitude="+longitude+"&latitude="+latitude);
                HttpGet httpGet = new HttpGet("http://www.java-go.cn/wifiBlutooth/initBluetooth.do?blId="+blId);

                try {
                    //第三步：执行请求，获取服务器发还的相应对象
                    HttpResponse httpResponse = httpCient.execute(httpGet);
                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第五步：从相应对象当中取出数据，放到entity当中
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串
                       // System.out.println("正在请求，设备:"+blMac);
                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = response.toString();
                        handler2.sendMessage(message);
                    }else{
                       // Toast.makeText(wifiActivity.this,"请求不成功",Toast.LENGTH_LONG).show();
                        ToastUtil.showToast(wifiActivity.this,"请求不成功",Toast.LENGTH_LONG);

                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();//这个start()方法不要忘记了

    }
//对初始化联网返回数据进行处理
    private Handler handler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    System.out.println(response);

                    try {
                        JSONObject object=new JSONObject(response);
                        object.getString("message");
                        System.out.println(object.getString("message"));
                        if(object.getString("message").equals("fail")){
                            //System.out.println("已确认");
                            //Toast.makeText(wifiActivity.this,"初始化失败",Toast.LENGTH_LONG).show();
                            if(scanResults.size()>0){
                                ToastUtil.showToast(wifiActivity.this,"初始化失败",Toast.LENGTH_SHORT);
                            }
                        }else{
                            if(scanResults.size()>0){
                                ToastUtil.showToast(wifiActivity.this,"成功初始化",Toast.LENGTH_SHORT);
                            }

                           //toast1= Toast.makeText(wifiActivity.this,"成功初始化",Toast.LENGTH_LONG);
                            //System.out.println("未确认");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//						try {
//							JSONArray object=new JSONArray(response);
//							String blMac =object.getString("blMac");
//							System.out.println(blMac);
//
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}


            }

            //toast1.show();

        }

    };

    boolean indexPression = false;

    @Override
    protected void onPause() {
        ToastUtil.cancel();
        super.onPause();
        indexPression = pression;
        pression = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        pression = indexPression;
    }
}
