package com.example.administrator.bluetest;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.administrator.bluetest.R.drawable.godess;

/**
 * Created by Administrator on 2017/9/22.
 */

public class DeviceListActivity extends Activity {
    private ImageView ble;
    String name, id, locationName, locationId,blMac,blType,blName;
    public String longitude;
    public String latitude;
    private TextView userName;
    public static final int SHOW_RESPONSE = 0;
    private ListView wifi_listView;
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private static Toast t1 = null;

    JSONObject object = null;
    String response=null;
    JSONArray jsonArray = null;
    Object str[]=null;
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
        setContentView(R.layout.device_list);
        //list1 = findViewById(R.id.list1);
        ble = findViewById(R.id.ble);
        userName = findViewById(R.id.userName);


//        list1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DeviceListActivity.this, DeviceSaveActivity.class);
//                Bundle bundle = new Bundle();
//                //传递name参数为tinyphp
//                bundle.putString("longitude", longitude);
//                bundle.putString("latitude", latitude);
//                bundle.putString("name", name);
//                bundle.putString("id", id);
//                bundle.putString("locationId", locationId);
//                bundle.putString("locationName", locationName);
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });
        Bundle bundle = this.getIntent().getExtras();
        name = bundle.getString("name");
        id = bundle.getString("id");
        locationName = bundle.getString("locationName");
        locationId = bundle.getString("locationId");
        userName.setText("  " + locationName + " \n" + name);
//        if(longitude==null){
//            Toast.makeText(this, "正在获取位置,请打开GPS...", Toast.LENGTH_SHORT).show();
//        }


        wifi_listView=findViewById(R.id.wifi_listView);
        //sendRequestWithHttpClient(locationId);
        //System.out.println(jsonArray);
        handlerTimer.sendEmptyMessageDelayed(0, 1000);//启动handler，进入设备列表界面1秒后执行
        //添加list列表项的监听
        wifi_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(DeviceListActivity.this,DeviceSaveActivity.class);
                //将name,id,locationId,locationName等的值传给下一个Activity
                Bundle bundle=new Bundle();
                bundle.putString("name", name);
                bundle.putString("id", id);
                bundle.putString("locationId", locationId);
                bundle.putString("locationName", locationName);

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//销毁中间的Activity

//                System.out.println("序号： "+i);
//                System.out.println("每一项： "+ str[i]);

                String ss= str[i]+"";

                try {
                    JSONObject obj=new JSONObject(ss);
                    bundle.putString("blMac", obj.getString("blMac"));
                    bundle.putString("blType", obj.getString("blType"));
                    bundle.putString("blName", obj.getString("blName"));
                    bundle.putString("blCode", obj.getString("blCode"));
                    bundle.putString("latitude", obj.getString("lastLatitude"));
                    bundle.putString("longitude", obj.getString("lastLongitude"));
                    bundle.putString("signAddress", obj.getString("signAddress"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent.putExtras(bundle);
                startActivity(intent);//跳转
            }
        });


//
//        //获取定位管理器
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        //设置定位信息
//        //坐标位置改变，回调此监听方法
//        LocationListener listener = new LocationListener() {
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                // TODO Auto-generated method stub
//
//            }
//
//            //位置改变的时候调用，这个方法用于返回一些位置信息
//            @Override
//            public void onLocationChanged(Location location) {
//                //获取位置变化结果
//                float accuracy = location.getAccuracy();//精确度，以密为单位
//                double altitude = location.getAltitude();//获取海拔高度
//                longitude = String.valueOf(location.getLongitude());//经度
//                latitude = String.valueOf(location.getLatitude());//纬度
//                float speed = location.getSpeed();//速度
//
//
//                //显示位置信息
//                //tv_show_location.append("accuracy:" + accuracy + "\n");
//                //tv_show_location.append("altitude:" + altitude + "\n");
//                //tv_show_location.append("longitude:" + longitude + "\n");
//                //tv_show_location.append("latitude:" + latitude + "\n");
//                // tv_show_location.append("speed:" + speed + "\n");
//
//                System.out.println("经度：" + longitude + " " + "纬度：" + latitude);
//
//                Toast.makeText(DeviceListActivity.this, "位置已获取到！！！", Toast.LENGTH_SHORT).show();
//            }
//        };
//
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates("gps", 1000, 10, listener);//Register for location updates


//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        // String provider = LocationManager.GPS_PROVIDER;
//
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(true);
//        criteria.setBearingRequired(true);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//
//        provider = locationManager.getBestProvider(criteria, true);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        location = locationManager.getLastKnownLocation(provider);
//        updateWithNewLocation(location);
//        locationManager.requestLocationUpdates(provider, 2000, 10, this);
        //添加进入搜索列表的按钮监听
        ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent  intent=new Intent(DeviceListActivity.this,DeviceScanActivity.class);
                Intent intent = new Intent(DeviceListActivity.this, wifiActivity.class);
                //用Bundle携带数据
                Bundle bundle = new Bundle();
                //传递name参数为tinyphp
                //blMac,blType,blName
                //将name，id，locationName，locationId，latitude，longitude的值传给下一个Activity
                bundle.putString("name", name);
                bundle.putString("id", id);
                bundle.putString("locationName", locationName);
                bundle.putString("locationId", locationId);
                bundle.putString("latitude", latitude);
                bundle.putString("longitude", longitude);


                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //t1.cancel();
                startActivity(intent);
               
            }
        });

    }


//    private void updateWithNewLocation(Location location) {
//        String latLongString;
//
//        if (location != null) {
//            latitude = String.valueOf(location.getLatitude());
//            longitude = String.valueOf(location.getLongitude());
////            float spe = location.getSpeed();// 速度
////            float acc = location.getAccuracy();// 精度
////            double alt = location.getAltitude();// 海拔
////            float bea = location.getBearing();// 轴承
////            long tim = location.getTime();// 返回UTC时间1970年1月1毫秒
////            latLongString = "纬度:" + lat + "\n经度:" + lng + "\n精度：" + acc
////                    + "\n速度：" + spe + "\n海拔：" + alt + "\n轴承：" + bea + "\n时间："
////                    + sdf.format(tim);
//        } else {
//            latLongString = "无法获取位置信息";
//        }
//        System.out.println("经度：" +longitude +"\n"+"纬度："+latitude);
//       // myLocationText.setText("经度：" +longitude +"\n"+"纬度："+latitude);
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        // TODO Auto-generated method stub
//        updateWithNewLocation(location);
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        // TODO Auto-generated method stub
//        updateWithNewLocation(null);
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//        // TODO Auto-generated method stub
//
//    }

        //发送请求获取blMAC

    private void sendRequestWithHttpClient(final String locationId) {
        // Toast.makeText(wifiActivity.this,"正在获取要配对的blMAC,请稍等...",Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                HttpClient httpCient = new DefaultHttpClient();
                //第二步：创建代表请求的对象,参数是访问的服务器地址blMac=12:34:56:78:9A:BC&userId=1001&userName=%E5%BC%A0%E4%B8%89
                //HttpGet httpGet = new HttpGet("http://www.ding-new.com/wifiBlutooth/blutoothList.do?locationId="+locationId);
                  HttpGet httpGet = new HttpGet("http://www.java-go.cn/wifiBlutooth/blutoothList.do?locationId=" + locationId);
               //HttpGet httpGet = new HttpGet("http://192.168.1.102:8080/EasyCCC/wifiBlutooth/blutoothList.do?locationId="+locationId);


                try {
                    //第三步：执行请求，获取服务器发还的相应对象
                    HttpResponse httpResponse = httpCient.execute(httpGet);
                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第五步：从相应对象当中取出数据，放到entity当中
                        HttpEntity entity = httpResponse.getEntity();

                        String response = EntityUtils.toString(entity, "utf-8");//将entity当中的数据转换为字符串

                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = response.toString();
                        System.out.println("请求！！！");
                        handler.sendMessage(message);
                    } else {
                        Toast.makeText(DeviceListActivity.this, "请求不成功", Toast.LENGTH_LONG).show();
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
                    //object1= (JSONObject) msg.obj;
                     response = (String) msg.obj;
                    System.out.println(response);


                    try {

                        jsonArray = new JSONArray(response);
                        str=new Object[jsonArray.length()];
                        System.out.println("jsonArray:" + jsonArray);
                        //将获取回来的数据进行解析
                        for (int i = 0; i < jsonArray.length(); i++) {
                            object = new JSONObject(jsonArray.get(i) + "");
                            System.out.println("_________________________");
                            System.out.println("object:" + object);
                            //blMacsList.add(object.getString("blMac"));
                            str[i]=object;
                            // System.out.println("请求回来的MAC:  "+blMacsList.get(i));
                            //Toast.makeText(DeviceListActivity.this,"正在同步...",Toast.LENGTH_SHORT).show();
                            //ToastUtil.showToast(DeviceListActivity.this,"正在同步...",Toast.LENGTH_LONG);
                        }
//                        if(blMacsList.size()<1){
//                            Toast.makeText(wifiActivity.this,"没有设备需要配对,可以休息了...",Toast.LENGTH_SHORT).show();
//                        }
                        //System.out.println(object.getString("blMac"));
                        for(int i=0;i<str.length-1;i++){
                            System.out.println("新："+str[i]);
                        }
                        if(str!=null){
                            wifi_listView.setAdapter(new MyAdapter(DeviceListActivity.this,str));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
        }

    };

    //启动定时器
    private Handler handlerTimer = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void handleMessage(android.os.Message msg) {
            System.out.println("调用定时器");
            sendRequestWithHttpClient(locationId);//开始发送请求
            //t1=Toast.makeText(DeviceListActivity.this,"正在同步...",Toast.LENGTH_SHORT);
            //t1.show();
            handlerTimer.sendEmptyMessageDelayed(1,1000*30);//60秒后再次执行

        }
    };
    //列表适配器
    public class MyAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater;
        private Context mContext;
        private Object[] str;

        public MyAdapter(Context context, Object[] str)
        {
            mInflater = LayoutInflater.from(context);
            this.mContext = context;
            this.str = str;
        }

        @Override
        public int getCount()
        {
            return str.length;
        }

        @Override
        public Object getItem(int position)
        {
            return str;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder viewHolder = null;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.list_item,null);
                viewHolder = new ViewHolder();
                viewHolder.mImageView = (ImageView) convertView
                        .findViewById(R.id.list1);

                viewHolder.star = (ImageView) convertView
                        .findViewById(R.id.star);
                viewHolder.Mac = (TextView) convertView
                        .findViewById(R.id.Mac);
                viewHolder.SN = (TextView) convertView
                        .findViewById(R.id.SN);
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.name);
                convertView.setTag(viewHolder);
            } else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
                    System.out.println((JSONObject)str[position]);
            String ss= str[position]+"";
            try {
                JSONObject obj=new JSONObject(ss);
               // blMac,blType,blName
                //进行渲染列表
                blMac=obj.getString("blMac");
                blType=obj.getString("blType");
                blName=obj.getString("blName");
                viewHolder.Mac.setText(obj.getString("blMac"));
                viewHolder.SN.setText(obj.getString("blCode"));
                viewHolder.name.setText(obj.getString("blName"));
                System.out.println(obj.getString("blArriveFlag"));
               if( obj.getString("blArriveFlag").equals("Y")){
                   System.out.println("Y 进来");
                   viewHolder.star.setImageDrawable( getResources().getDrawable(R.drawable.select_yes) );
                   //viewHolder.star.setBackgroundResource(R.drawable.select_yes);
               }else{
                   viewHolder.star.setImageDrawable( getResources().getDrawable(R.drawable.delete) );
                   //viewHolder.star.setBackgroundResource(R.drawable.delete);
               }



            } catch (JSONException e) {
                e.printStackTrace();
            }

/*            try {
                ob = new JSONObject((String) str[0]);
                System.out.println(ob.getString("blFlag"));
           } catch (JSONException e) {
                e.printStackTrace();
            }*/






            //viewHolder.Mac.setText(mDatas.get(position));
            return convertView;
        }

        private final class ViewHolder
        {
            TextView Mac,SN,name;
            ImageView mImageView,star;
        }



    }



}

