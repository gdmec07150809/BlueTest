/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.administrator.bluetest;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.System.in;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public  class DeviceScanActivity extends Activity implements OnClickListener  {
   // private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

	public static final int SHOW_RESPONSE = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;//10秒扫描完毕
    
    private DeviceListAdapter mDevListAdapter;
	//public   String[]  blMacs=null;
	private  String userId=null;
	private  String postName=null;
	private TextView userName;
	ToggleButton tb_on_off;
	TextView btn_searchDev;
	Button btn_aboutUs;
	ListView lv_bleList;
	String locationId;
	List<String> list=new ArrayList<String>();
	List<String> blMacsList=new ArrayList<String>();
	Timer timer;
	public String longitude;
	public String latitude;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		handlerTimer.sendEmptyMessageDelayed(0, 1000);//启动handler，进入蓝牙列表界面1秒后执行

		userName=findViewById(R.id.userName);

		//getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

		//接收登录传过来的数据
		Bundle bundle = this.getIntent().getExtras();
				String name = bundle.getString("name");
				String id = bundle.getString("id");
				String locationName = bundle.getString("locationName");
				locationId = bundle.getString("locationId");
				longitude = bundle.getString("longitude");
				latitude = bundle.getString("latitude");

				//userName.setText("欢迎 "+locationName+" "+name);
				userId=id;
				postName=name;
				//sendRequestWithHttpClient(locationId);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //判断蓝牙是否启动,关闭则启动
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }


        lv_bleList = (ListView) findViewById(R.id.lv_bleList);
        
//		//tb_on_off = (ToggleButton) findViewById(R.id.tb_on_off);
//		btn_searchDev = (TextView) findViewById(R.id.btn_searchDev);
//		btn_aboutUs = (Button) findViewById(R.id.btn_aboutUs);
//		
//		btn_aboutUs.setText("");
//		btn_aboutUs.setOnClickListener(this);
//		btn_searchDev.setOnClickListener(this);
		
		mDevListAdapter = new DeviceListAdapter();
		lv_bleList.setAdapter(mDevListAdapter);

		/*
		// ��������Ƿ�������toggleButton״̬
		if (mBluetoothAdapter.isEnabled()) {
			tb_on_off.setChecked(true);
		} else {
			tb_on_off.setChecked(false);
		}*/
		//暂时不做响应
//		lv_bleList.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				if (mDevListAdapter.getCount() > 0) {
//					/*
//					BluetoothDevice device = mDevListAdapter.getItem(position);
//					Intent intent = new Intent(DeviceScanActivity.this,
//							DeviceControlActivity.class);
//					Bundle bundle = new Bundle();
//					bundle.putString("BLEDevName", device.getName());
//					bundle.putString("BLEDevAddress", device.getAddress());
//					intent.putExtras(bundle);
//					DeviceScanActivity.this.startActivity(intent);
//					*/
//
//
//					 BluetoothDevice device1 = mDevListAdapter.getItem(position);
//				        if (device1 == null) return;
//				        Intent intent1 = new Intent(DeviceScanActivity.this,
//								DeviceControlActivity.class);;
//				        intent1.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device1.getName());
//				        intent1.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device1.getAddress());
//				        if (mScanning) {
//				            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//				            mScanning = false;
//				        }
//				        startActivity(intent1);
//				}
//			}
//		});
    }
    //启动定时器
	private Handler handlerTimer = new Handler(){
		@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
		public void handleMessage(android.os.Message msg) {
				System.out.println("调用定时器");
				Toast.makeText(DeviceScanActivity.this,"开始发送请求",Toast.LENGTH_SHORT).show();

				sendRequestWithHttpClient(locationId);//开始发送请求

				scanLeDevice(true);//开始配对

				handlerTimer.sendEmptyMessageDelayed(0,1000*60*15);//15分钟后再次执行

		}
	};

//发送请求获取blMAC
	private void sendRequestWithHttpClient(final String locationId) {
		Toast.makeText(DeviceScanActivity.this,"正在获取要配对的blMAC,请稍等...",Toast.LENGTH_SHORT).show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				//用HttpClient发送请求，分为五步
				//第一步：创建HttpClient对象
				HttpClient httpCient = new DefaultHttpClient();
				//第二步：创建代表请求的对象,参数是访问的服务器地址blMac=12:34:56:78:9A:BC&userId=1001&userName=%E5%BC%A0%E4%B8%89
				//HttpGet httpGet = new HttpGet("http://www.ding-new.com/wifiBlutooth/blutoothList.do?locationId="+locationId);
				HttpGet httpGet = new HttpGet("http://192.168.1.105:8080/EasyCCC/wifiBlutooth/blutoothList.do?locationId="+locationId);

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
						handler.sendMessage(message);
					}else{
						Toast.makeText(DeviceScanActivity.this,"请求不成功",Toast.LENGTH_LONG).show();
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

					JSONArray jsonArray = null;
					try {
						jsonArray = new JSONArray(response);
						System.out.println("jsonArray:"+jsonArray);
						for(int i=0;i<jsonArray.length();i++){
						JSONObject object=new JSONObject(jsonArray.get(i)+"");
							System.out.println("_________________________");
							System.out.println("object:"+object.getString("blMac"));
							blMacsList.add(object.getString("blMac"));
							Toast.makeText(DeviceScanActivity.this,"正在同步...",Toast.LENGTH_SHORT).show();
						}
						if(blMacsList.size()<1){
							Toast.makeText(DeviceScanActivity.this,"没有设备需要配对,可以休息了...",Toast.LENGTH_SHORT).show();
						}
						//System.out.println(object.getString("blMac"));
					} catch (JSONException e) {
						e.printStackTrace();
				}

					}
		}

	};

	public void onClick(View v) {
		switch (v.getId()) {
		case 0:
			break;
//		case R.id.btn_searchDev:
//			//scanLeDevice(true);
//			break;

//		case R.id.btn_aboutUs:
//			 Intent intent = new Intent();
//		        intent.setAction("android.intent.action.VIEW");
//		        Uri content_url = Uri.parse("https://item.taobao.com/item.htm?spm=a1z10.1-c.w4004-11559702484.2.uKkX9H&id=44163359933");
//		        intent.setData(content_url);
//		        startActivity(intent);
//			break;
		}
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
		System.out.println("进入菜单");
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                //mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                //mDevListAdapter.;
                mDevListAdapter.clear();
                mDevListAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }
/*
    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
        
    }*/
    
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            bk;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) bk;
        final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }
*/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
				@Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                   // invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
			
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

		System.out.println("开始寻找需要配对的设备");
		System.out.println("postName:"+postName);
		System.out.println("userId:"+userId);
		int length=blMacsList.size();
		for(int i=0;i<length;i++){
			if(list.contains(blMacsList.get(i))){
				sendRequestWithHttpClient1(blMacsList.get(i),postName,userId);
			}else{
				System.out.println("没有找到设备："+blMacsList.get(i));
				//Toast.makeText(this,"没有找到设备："+blMacsList.get(i),Toast.LENGTH_LONG).show();
			}
		}
		// invalidateOptionsMenu();

//		for(String mac:blMacsList){
//			for(String ls:list){
//				if(mac.equals(ls)){
//					System.out.println("找到设备："+mac);
//					sendRequestWithHttpClient1(mac,postName,userId);
//				}else{
//					System.out.println("没有找到设备："+mac);
//					Toast.makeText(this,"没有找到设备："+mac,Toast.LENGTH_LONG).show();
//				}
//			}
//		}
    }


	private void sendRequestWithHttpClient1(final String blMac,final String postName,final String userId) {
			Toast.makeText(DeviceScanActivity.this,"请求设备"+blMac+",是否需要更新",Toast.LENGTH_SHORT).show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				//用HttpClient发送请求，分为五步
				//第一步：创建HttpClient对象
				HttpClient httpCient = new DefaultHttpClient();
				//第二步：创建代表请求的对象,参数是访问的服务器地址blMac=12:34:56:78:9A:BC&userId=1001&userName=%E5%BC%A0%E4%B8%89
				//HttpGet httpGet = new HttpGet("http://www.ding-new.com/wifiBlutooth/updateBlutooth.do?blMac="+blMac+"&userId="+userId+"&userName="+postName+"&longitude="+longitude+"&latitude="+latitude);

				HttpGet httpGet = new HttpGet("http://192.168.1.105:8080/EasyCCC/wifiBlutooth/updateBlutooth.do?blMac="+blMac+"&userId="+userId+"&userName="+postName+"&longitude="+longitude+"&latitude="+latitude);

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
						Toast.makeText(DeviceScanActivity.this,"请求不成功",Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();//这个start()方法不要忘记了

	}

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
						System.out.println(object.getString("message"));
						if(object.getString("message").equals("fail")){
							System.out.println("已签收");
							Toast.makeText(DeviceScanActivity.this,"已签收",Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(DeviceScanActivity.this,"未签收",Toast.LENGTH_LONG).show();
							System.out.println("未签收");
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


		}

	};
/*
    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
            	//String gg=device.getAddress().toString().trim();
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            bk mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            bk mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            bk mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            bk i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            bk view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	//String gg=device.getAddress().toString().trim();
                	//Log.i("tag", gg);
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
    */
    
    
    
    
    

	private LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					System.out.println(device.getAddress());
					mDevListAdapter.addDevice(device);
					mDevListAdapter.notifyDataSetChanged();
					if(!list.contains(device.getAddress())){
						list.add(device.getAddress());
					}

				}
			});

		}
	};

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	protected void onResume() {//打开APP时扫描设备
		super.onResume();
		scanLeDevice(true);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	protected void onPause() {//停止扫描
		super.onPause();
		scanLeDevice(false);
	}

    
    
//适配器
	class DeviceListAdapter extends BaseAdapter {

		private List<BluetoothDevice> mBleArray;
		private ViewHolder viewHolder;

		public DeviceListAdapter() {
			mBleArray = new ArrayList<BluetoothDevice>();

		}

		public void addDevice(BluetoothDevice device) {
			if (!mBleArray.contains(device)) {
				mBleArray.add(device);
			}

		}
		public void clear(){
			mBleArray.clear();
		}

		@Override
		public int getCount() {
			return mBleArray.size();
		}

		@Override
		public BluetoothDevice getItem(int position) {
			return mBleArray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(DeviceScanActivity.this).inflate(
						R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_devName = (TextView) convertView
						.findViewById(R.id.device_name);
				viewHolder.itemId = (TextView) convertView
						.findViewById(R.id.itemId);
				viewHolder.tv_devAddress = (TextView) convertView
						.findViewById(R.id.device_address);
                viewHolder.device_uuid = (TextView) convertView
                        .findViewById(R.id.device_uuid);
				convertView.setTag(viewHolder);
			} else {
				convertView.getTag();
			}


			// add-Parameters
			BluetoothDevice device = mBleArray.get(position);
			String devName = device.getName();
			if (devName != null && devName.length() > 0) {
				viewHolder.tv_devName.setText("蓝牙名称："+devName);
			} else {
				viewHolder.tv_devName.setText("unknow-device");
			}
			viewHolder.tv_devAddress.setText("blMac： "+device.getAddress());
            viewHolder.device_uuid.setText("Uuid： "+device.getUuids());
			viewHolder.itemId.setText(position+1+"");
			return convertView;
		}

	}

	class ViewHolder {
		TextView tv_devName, tv_devAddress,itemId,device_uuid;
	}


    
    
    
    
    
    
}