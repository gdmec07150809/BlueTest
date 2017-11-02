package com.example.administrator.bluetest;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/9/16.
 */
@SuppressLint("SdCardPath")
public class DeviceSaveActivity extends Activity {
    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private ImageView settings_back,machine_photo_img;
    private TextView location;
    private EditText tv_show_location,machine_name_edt,machine_type_edt,mac_edt,uuid_edt,tv_show_mLocation,tv_show_du;
    String name,id,locationName,locationId,longitude,latitude, blMac,blType,blName,blcode,signAddress;
    private double BatteryT;//电池温度
    private Button photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_machine_activity);
        spinner = (Spinner) findViewById(R.id.spinner);
        settings_back = (ImageView) findViewById(R.id.settings_back);
        location = (TextView) findViewById(R.id.location);
        tv_show_location = (EditText) findViewById(R.id.tv_show_location);
        machine_name_edt = (EditText) findViewById(R.id.machine_name_edt);
        machine_type_edt = (EditText) findViewById(R.id.machine_type_edt);
        mac_edt = (EditText) findViewById(R.id.mac_edt);
        uuid_edt = (EditText) findViewById(R.id.uuid_edt);
        tv_show_mLocation= (EditText) findViewById(R.id.tv_show_mLocation);
        tv_show_du=findViewById(R.id.tv_show_du);
        photo=findViewById(R.id.photo);
        machine_photo_img=findViewById(R.id.machine_photo_img);
        //数据
        data_list = new ArrayList<String>();
        data_list.add("温度");
        data_list.add("湿度");
        data_list.add("流量");
        data_list.add("压力");
        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);

        //下拉列表监听事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        // 注册一个系统 BroadcastReceiver，作为访问电池计量之用这个不能直接在AndroidManifest.xml中注册
                        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                        break;
                    case 1:tv_show_du.setText("  20rh%");break;
                    case 2:tv_show_du.setText("  50M");break;
                    case 3:tv_show_du.setText("  100PA");break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        settings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DeviceSaveActivity.this,DeviceListActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("name", name);
                bundle.putString("id", id);
                bundle.putString("locationId", locationId);
                bundle.putString("locationName", locationName);
                intent.putExtras(bundle);

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        //拍照按钮监听,有点问题，未完全实现
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });


        Bundle bundle = this.getIntent().getExtras();
        //blMac,blType,blName
         longitude = bundle.getString("longitude");
        latitude = bundle.getString("latitude");
        name = bundle.getString("name");
        id = bundle.getString("id");
        blMac = bundle.getString("blMac");
        blType = bundle.getString("blType");
        blName = bundle.getString("blName");
        locationName = bundle.getString("locationName");
        locationId = bundle.getString("locationId");
        blcode = bundle.getString("blCode");
        signAddress = bundle.getString("signAddress");
        System.out.println("设备列表    经度："+longitude+"  "+"纬度："+latitude);
        if(longitude==null||longitude.equals("null")){
            tv_show_location.setText("请更新再获取...");
        }else{
            tv_show_location.setText("经度："+longitude+"\n"+"纬度："+latitude);
        }
        machine_name_edt.setText(blName);
        machine_type_edt.setText(blType);
        mac_edt.setText(blMac);
        uuid_edt.setText(blcode);
        System.out.println(signAddress);
        if(signAddress==null|| signAddress.equals("null")){
            tv_show_mLocation.setText("请更新...");
        }else{
            tv_show_mLocation.setText(signAddress);
        }
       // machine_name_edt,machine_type_edt,mac_edt,uuid_edt
    }


    /* 创建广播接收器 */
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            /*
             * 如果捕捉到的action是ACTION_BATTERY_CHANGED， 就运行onBatteryInfoReceiver()
             */
            if (Intent.ACTION_BATTERY_CHANGED.equals(action))
            {
                BatteryT = intent.getIntExtra("temperature", 0);  //电池温度
                tv_show_du.setText( " "+(BatteryT*0.1) + "℃");
            }
        }
    };

    @SuppressLint("SdCardPath")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.i("TestFile",
                        "SD card is not avaiable/writeable right now.");
                return;
            }
            new DateFormat();
            String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            System.out.println("路径："+name);
            Toast.makeText(this, name, Toast.LENGTH_LONG).show();
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

            FileOutputStream b = null;
            File file = new File("/sdcard/Image/");
            file.mkdirs();// 创建文件夹
            String fileName = "/sdcard/Image/"+name;

            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try
            {
                machine_photo_img.setImageBitmap(bitmap);// 将图片显示在ImageView里
            }catch(Exception e)
            {
                Log.e("error", e.getMessage());
            }

        }
    }
   
}
