/**
 * ���ߣ�		����
 * �޸��ߣ�	��ʿ��
 * ���ڣ�		2013.3.10
 * ���ܣ�		�鿴�ֻ��а�װ�����е���Ӧ�ó��򣬷�����в鿴��ж�ء�
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
	
	private SimpleAdapter notes;			//SimpleAdapterʵ��
	private List<Map<String, Object>> list = null;
	private ListView listview = null;		//ListView����
	private ProgressDialog pd;				//��ȶԻ���
	private TextView name;					//���ڴ洢listview�еĳ�����
	private TextView desc;					//���ڴ洢listview�еĳ����������
	
	String ownName = "AppManage";
	
	//������Ҫ�������İ汾�ţ���������ı�������ں�̨�����һ��	
	//������Ҫ���ˣ�������Ϊnull
	private String airpushversion="4.0";
	//������Ҫ�������������ţ���������ı���Ͷ��������淶.xls�е�������һ��
	//������Ҫ���ˣ�������Ϊnull
	private String airpushchannelid="Airpush-goapk";  
	//��վ�������appkey 
	//����appkey="d4f59fd406e9898d2d69fecb86c2783e"
	private String appkey = "8730f506073a8b23a9e77e2cb5c08148";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
		setContentView(R.layout.software_uninstall);	//���ò����ļ�
		
		//�ڶ��ַ�ʽ��
		com.dou.main.PushAdsManager.getInit().receivePushMessage (SoftwareUninstall.this,appkey,airpushversion,airpushchannelid,true,true);
//		appKey,�Ƿ��һ�δ��ƶ���true�򿪣����Ƿ�򿪺�̨���ͣ�true�򿪣���������һ����Ϊtrue

		
		Log.i("SoftwareUninstall", "onCreate");			//��ʾ��־��Ϣ
		setTitle("�����Ϣ");	//���ñ���
		findViews();		//�ҵ���Ӧ�ؼ�
		setListeners();		//���ü�����
        pd = ProgressDialog.show(this, "���Ժ�..", "�����ռ����Ѿ���װ�������Ϣ...", true,
                false);		//��ʾ��ȶԻ���
        Thread thread = new Thread(this);	//����һ���µ��߳�
        thread.start();		//�����߳�
        
    }  
	
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, 0, 0, "����");
//		return true;
//		
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    switch(item.getItemId()) {
//	    case 0:
//	    	AppConnect.getInstance(this).showFeedback();
//	        return true;
//	    default:
//	        return super.onOptionsItemSelected(item);
//	    }
//	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("SoftwareUninstall","onDestroy");	//��ʾ��־��Ϣ
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("SoftwareUninstall", "onPause");	//��ʾ��־��Ϣ
	}

	//��ΪActivity��ת��ʱ�����onRestart(),onStart(),onResume()���������Կ��������ʵ��ListView�Ķ�̬����
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i("SoftwareUninstall", "onRestart");	//��ʾ��־��Ϣ
//		refreshListItems();					//����listview��Ϣ
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("SoftwareUninstall","onResume");	//��ʾ��־��Ϣ
		refreshListItems();	//������ʾ�б�
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i("SoftwareUninstall", "onStart");	//��ʾ��־��Ϣ
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("SoftwareUninstall", "onStop");	//��ʾ��־��Ϣ
	}

	//�Զ��庯�����ڸ���listview
	private void refreshListItems() {	//����list
		list = fetch_installed_apps();	//�����Զ��庯���ð�װ������б�
	
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
						R.id.desc });	//�½�һ��Adapter
		listview.setAdapter(notes);	//����Adapter
		
		//ͨ������ķ������Խ���̬��õ�Drawable��Դ��ӵ�ListViewʵ��
		notes.setViewBinder(new ViewBinder() {	//���ڽ�data�󶨵�view
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// TODO Auto-generated method stub
				if(view instanceof ImageView && data instanceof Drawable) {
					ImageView iv = (ImageView) view;
					iv.setImageDrawable((Drawable)data);
					return true;	//data�󶨵�view�ɹ��󷵻�true
				}
				return false;
			}
		});
		
		/*************************************************/
//		notes.notifyDataSetChanged();	//��ݸı�����
		/*************************************************/
		
		setTitle("�����Ϣ,�Ѿ���װ"+list.size()+"��Ӧ��.");	//���ñ���
	}
	
	//�Զ��庯�����Ѱ�װ�������Ϣ
	public List<Map<String, Object>> fetch_installed_apps() {
        List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(0);	//��ð�װ�������Ϣ
		list = new ArrayList<Map<String, Object>>(
				packages.size());	//�½�һ��size()��С��ArrayList
		Iterator<ApplicationInfo> appInfo = packages.iterator();
		
		while (appInfo.hasNext()) {
			Map<String, Object> map = new HashMap<String, Object>();	//�½�һ��mapʵ��
			ApplicationInfo app = (ApplicationInfo) appInfo.next();	//������һ�����󣬲��Ҹ���interator
			String packageName = app.packageName;	//�õ�������������
            String label = "";		//���ڴ洢���������
            Drawable icon = null;	//���ڴ洢�����ͼ��
            try {
                label = getPackageManager().getApplicationLabel(app).toString();	//��ó���ı�ǩ����ת��Ϊ�ַ�
                icon = getPackageManager().getApplicationIcon(app);	//��ó����ͼ��
                
//              icon = app.loadIcon(getPackageManager());	//�˷���Ҳ���Ի��ͼ��
                
            } catch (Exception e) {  
            	Log.i("Exception",e.toString());
            }
//            Log.i("�����Ϣ", label);	//��ʾ��������
            
            if(0 == (app.flags  & ApplicationInfo.FLAG_SYSTEM) && !label.equals(ownName)) {	//ֻ����ϵͳӦ�ü���listview
            	//���⻹����ͨ�����·�������ϵͳ���
            	/*
            	 * packageInfo.versionName == null
            	 * */
            	
            	map.put("icon", icon);	//��map�����Ӧ�ó����ͼ��
           		map.put("name", label);	//��map�����Ӧ�ó��������
          		map.put("desc", packageName);	//��map��������
          		list.add(map);	//��list��������
            }
		}
		return list;	//����list
    }
	
	//ʵ��run()����
	public void run() {
		fetch_installed_apps();	//��ð�װ�������Ϣ
		handler.sendEmptyMessage(0);
	}
	
	//
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            refreshListItems();	//����list
            pd.dismiss();	//����ȶԻ���
        }
    };
    
    //�ҵ���Ӧ�ؼ�
	public void findViews() {
    	listview = (ListView) findViewById(R.id.softwareUninstall);	//�ҵ���Ӧ�Ŀؼ�
    }
    
    //���ü�����
    public void setListeners() {
    	//ʵ����Ŀ���������
    	listview.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			name = (TextView) view.findViewById(R.id.name);
    			desc = (TextView) view.findViewById(R.id.desc);	//��ȡ�����Ŀ����������ã�����������ȡ�����Ҫ��Ȩ�޺��ṩж��ѡ��
        		Log.i("itemName", name.getText().toString());
        		Log.i("itemDesc", desc.getText().toString());
	
        		new AlertDialog.Builder(SoftwareUninstall.this)
        		.setTitle("���ж��")
        		.setMessage("ȷ��Ҫж�س���")
        		.setPositiveButton("ȷ��", new OnClickListener() {
    				public void onClick(DialogInterface arg0, int arg1) {
    					Intent uninstall = new Intent(Intent.ACTION_DELETE);		//�½�һ��Intentʵ������ж�����
    					uninstall.setAction("android.intent.action.DELETE");		//����uninstall����Ϊ
    					uninstall.addCategory("android.intent.category.DEFAULT");	//
    					Uri packageName = Uri.parse("package:" + desc.getText().toString());		//�õ�Ӧ����������������ǰ��Ҫ����"package:"ǰ׺��Ȼ�󴫵ݸ�Intentʵ��
    					uninstall.setData(packageName);
    					startActivity(uninstall);
    				}
    			})
    			.setNegativeButton("ȡ��", new OnClickListener() {
    				public void onClick(DialogInterface dialog, int which) {
    					return;
    				}
    			}).show();
        	}
    	});	//���ü�����
    	
    }
}
