/**
 * @Author 		Shiyao Qi
 * @Date 		2013.3.10
 * @Function 	Display all the Apps installed on your device, one click to uninstall,
 * 				convenient for managing Apps on your device.
 */

package com.twlkyao.appmanage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class SoftwareUninstall extends Activity implements  Runnable {
	
	private SimpleAdapter notes;
	private List<Map<String, Object>> list = null;
	private ListView listview = null;
	private ProgressDialog pd;
	private TextView name;
	private TextView desc;
	
	String ownName = "AppManage";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
		setContentView(R.layout.software_uninstall);
		
		Log.i("SoftwareUninstall", "onCreate");
		
		setTitle(R.string.title);
		
		findViews();
		setListeners();
        pd = ProgressDialog.show(this, getString(R.string.pd_title),
        		getString(R.string.pd_message), true,
                false);
        Thread thread = new Thread(this);
        thread.start();
    }
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("SoftwareUninstall","onDestroy");
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("SoftwareUninstall", "onPause");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i("SoftwareUninstall", "onRestart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("SoftwareUninstall","onResume");
		refreshListItems(); // Refresh the ListView.
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i("SoftwareUninstall", "onStart");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("SoftwareUninstall", "onStop");
	}

	// Refresh the ListView.
	private void refreshListItems() {
		list = fetch_installed_apps();
	
		// Sort the list of application in alphabet order.
		Collections.sort(list,new Comparator<Map<String, Object>>() {  

			public int compare(Map<String, Object> map1,Map<String, Object> map2) {  

				// map1 and map2 are elements in list, return the result according to the comparison.
				String string1 = (String) map1.get("name");
				String string2 = (String) map2.get("name");
				
				// 0 if the strings are equal,
				// a negative integer if this string is before the specified string,
				// or a positive integer if this string is after the specified string.
				return string1.compareTo(string2);
			}
		});
		
		notes = new SimpleAdapter(this, list, R.layout.info_row,
				new String[] { "icon", "name", "desc" }, new int[] { R.id.icon, R.id.name,
						R.id.desc }); // Instance a new SimpleAdapter.
		listview.setAdapter(notes); // Set adapter.
		
		notes.setViewBinder(new ViewBinder() { // To get the App icon dynamically.
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// TODO Auto-generated method stub
				if(view instanceof ImageView && data instanceof Drawable) {
					ImageView iv = (ImageView) view;
					iv.setImageDrawable((Drawable)data);
					return true;
				}
				return false;
			}
		});
		setTitle(getString(R.string.total_installed) + list.size()); // Set title.
	}
	
	public List<Map<String, Object>> fetch_installed_apps() {
        List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(0);
		list = new ArrayList<Map<String, Object>>(
				packages.size());
		Iterator<ApplicationInfo> appInfo = packages.iterator();
		
		while (appInfo.hasNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			ApplicationInfo app = (ApplicationInfo) appInfo.next();	
			String packageName = app.packageName;
            String label = "";
            Drawable icon = null;
            try {
                label = getPackageManager().getApplicationLabel(app).toString();
                icon = getPackageManager().getApplicationIcon(app);
            } catch (Exception e) {  
            	Log.i("Exception",e.toString());
            }
            
            if(0 == (app.flags  & ApplicationInfo.FLAG_SYSTEM) && !label.equals(ownName)) { // Exclude the app itself.
            	
            	map.put("icon", icon); // Put icon key-vale.
           		map.put("name", label); // Put name key-value.
          		map.put("desc", packageName); // Put desc key-value.
          		list.add(map);
            }
		}
		return list;
    }
	
	public void run() {
		fetch_installed_apps();
		handler.sendEmptyMessage(0);
	}
	
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            refreshListItems();
            pd.dismiss();
        }
    };
    
	public void findViews() {
    	listview = (ListView) findViewById(R.id.softwareUninstall);	
    }
    
    public void setListeners() {
    	listview.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			name = (TextView) view.findViewById(R.id.name);
    			desc = (TextView) view.findViewById(R.id.desc);
        		Log.i("itemName", name.getText().toString());
        		Log.i("itemDesc", desc.getText().toString());
	
        		new AlertDialog.Builder(SoftwareUninstall.this)
        		.setTitle(R.string.alertdialog_title)
        		.setMessage(R.string.alertdialog_message)
        		.setPositiveButton(R.string.positive, new OnClickListener() {
    				public void onClick(DialogInterface arg0, int arg1) {
    					Intent uninstall = new Intent(Intent.ACTION_DELETE);
    					uninstall.setAction("android.intent.action.DELETE");
    					uninstall.addCategory("android.intent.category.DEFAULT");
    					Uri packageName = Uri.parse("package:" + desc.getText().toString());
    					uninstall.setData(packageName);
    					startActivity(uninstall);
    				}
    			})
    			.setNegativeButton(R.string.negative, new OnClickListener() {
    				public void onClick(DialogInterface dialog, int which) {
    					return;
    				}
    			}).show();
        	}
    	});
    }
}
