package com.example.nfccontactadder;

import java.nio.charset.Charset;



import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Contacts.People;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView text;
	public static  StringBuilder stringBuilder = null;
	public static  StringBuilder sb = null;
private NfcAdapter nfcAdapter;
private PendingIntent mPendingIntent;
private IntentFilter[] mFilters;
private String[][] mTechLists;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text = (TextView) findViewById(R.id.text);
		text.setText("Adding to Contacts");
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0,
		        new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName(), NfcA.class.getName() } };
		if(nfcAdapter==null)
		{
		Toast.makeText(getApplicationContext(), "NFC not enabled", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "NFC enabled", Toast.LENGTH_SHORT).show();
			
			}
		handleIntent(getIntent());
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		super.onResume();
		nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
	}
@Override
	protected void onPause() {
		// TODO Auto-generated method stub
//    stopForegroundDispatch(this, nfcAdapter);
		super.onPause();
		nfcAdapter.disableForegroundDispatch(this);
	}
@Override
	protected void onNewIntent(Intent intent) {
//		 TODO Auto-generated method stub
	handleIntent(intent);		
//	super.onNewIntent(intent);
//	nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
//	String action = intent.getAction();
//	if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
//	{
//		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
////		handleIntent(intent,detectedTag);
//	}

//
}
	private void handleIntent(Intent intent) {
		// TODO Auto-generated method stub
		byte[] payload = null;
		  byte[] id = null;
		  byte statusByte=0;
		  String action = intent.getAction();
		  System.out.println(action);
	        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
	                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
	                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
	            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	            NdefMessage[] msgs;
	            if (rawMsgs != null) {
	            	String payload1;
	            	
	            	System.out.println("in if");
	                msgs = new NdefMessage[rawMsgs.length];
	                for (int i = 0; i < rawMsgs.length; i++) {
	                    msgs[i] = (NdefMessage) rawMsgs[i];
	   	                }
	                for(int i = 0; i<msgs.length; i++)
	                {
	                for(int j=0;j<msgs[0].getRecords().length;j++)
	                {
	                	NdefRecord record = msgs[i].getRecords()[j];
	                	statusByte = record.getPayload()[0];
	                	int languageCodeLength = statusByte & 0x3F;
	                	String languageCode = new String( record.getPayload(), 1,
	                			languageCodeLength, Charset.forName("UTF-8"));
	                	int isUTF8 = statusByte-languageCodeLength;
	                	if(isUTF8 == 0x00){
	                		
	                		payload1 = new String( record.getPayload(), 1+languageCodeLength,
	                		record.getPayload().length-1-languageCodeLength,
	                		Charset.forName("UTF-8"));
	                		System.out.println("payload1"+payload1);
	                		}
	                	else if (isUTF8==-0x80){
	                	
	                		payload1 = new String( record.getPayload(), 1+languageCodeLength,
	                		record.getPayload().length-1-languageCodeLength,
	                		Charset.forName("UTF-16"));
	                		String[] a = payload1.split(",");
	                		System.out.println("payload"+a[0]+" "+a[1]);
//	                		makecall(payload1);
	                		 addContact(a[0],a[1]);
	                		}
	                	
	                }
	                }
	                
	}
	            else {
	                // Unknown tag type
	            	 System.out.println("in else");
	                byte[] empty = new byte[0];
	                id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
	                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	                 payload = dumpTagData(tag).getBytes();
	                 System.out.println(payload);
	                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
	                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
	                msgs = new NdefMessage[] { msg };
	                 stringBuilder = new StringBuilder();
	                char[] buffer = new char[2];
	                for (int i = 0; i < id.length; i++) {
	                    buffer[0] = Character.forDigit((id[i] >>> 4) & 0x0F, 16);  
	                    buffer[1] = Character.forDigit(id[i] & 0x0F, 16);  
	                    System.out.println(buffer);
	                    stringBuilder.append(buffer);
	                
	                }
	                char[] buffer1 = new char[2];
	                sb = new StringBuilder();
	                for (int i = 0; i < payload.length; i++) {
	                    buffer1[0] = Character.forDigit((payload[i] >>> 4) & 0x0F, 16);  
	                    buffer1[1] = Character.forDigit(payload[i] & 0x0F, 16);  
	                    System.out.println(buffer1);
	                    sb.append(buffer1);
	                	Toast.makeText(getApplicationContext(), sb.toString().trim(), Toast.LENGTH_SHORT).show();
	                	
	                }
	            
	            
	            }
	            }
	}
	        private void addContact(String name,String phone) {
		// TODO Auto-generated method stub
	        	text.setText("Saved Successfully");
	        	ContentValues values = new ContentValues();
	        	values.put(People.NUMBER, phone);
	        	values.put(People.TYPE, Phone.TYPE_CUSTOM);
	        	values.put(People.LABEL, name);
	        	values.put(People.NAME, name);
	        	Uri dataUri = getContentResolver().insert(People.CONTENT_URI, values);
	        	Uri updateUri = Uri.withAppendedPath(dataUri, People.Phones.CONTENT_DIRECTORY);
	        	values.clear();
	        	values.put(People.Phones.TYPE, People.TYPE_MOBILE);
	        	values.put(People.NUMBER, phone);
	        	updateUri = getContentResolver().insert(updateUri, values);
	        	
		
	}
			private String dumpTagData(Parcelable p) {
	            StringBuilder sb = new StringBuilder();
	            Tag tag = (Tag) p;
	            byte[] id = tag.getId();
	            sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
	            sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
	            sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

	            String prefix = "android.nfc.tech.";
	            sb.append("Technologies: ");
	            for (String tech : tag.getTechList()) {
	                sb.append(tech.substring(prefix.length()));
	                sb.append(", ");
	            }
	            sb.delete(sb.length() - 2, sb.length());
	            for (String tech : tag.getTechList()) {
	                if (tech.equals(MifareClassic.class.getName())) {
	                    sb.append('\n');
	                    MifareClassic mifareTag = MifareClassic.get(tag);
	                    String type = "Unknown";
	                    switch (mifareTag.getType()) {
	                    case MifareClassic.TYPE_CLASSIC:
	                        type = "Classic";
	                        break;
	                    case MifareClassic.TYPE_PLUS:
	                        type = "Plus";
	                        break;
	                    case MifareClassic.TYPE_PRO:
	                        type = "Pro";
	                        break;
	                    }
	                    sb.append("Mifare Classic type: ");
	                    sb.append(type);
	                    sb.append('\n');

	                    sb.append("Mifare size: ");
	                    sb.append(mifareTag.getSize() + " bytes");
	                    sb.append('\n');

	                    sb.append("Mifare sectors: ");
	                    sb.append(mifareTag.getSectorCount());
	                    sb.append('\n');

	                    sb.append("Mifare blocks: ");
	                    sb.append(mifareTag.getBlockCount());
	                }

	                if (tech.equals(MifareUltralight.class.getName())) {
	                    sb.append('\n');
	                    MifareUltralight mifareUlTag = MifareUltralight.get(tag);
	                    String type = "Unknown";
	                    switch (mifareUlTag.getType()) {
	                    case MifareUltralight.TYPE_ULTRALIGHT:
	                        type = "Ultralight";
	                        break;
	                    case MifareUltralight.TYPE_ULTRALIGHT_C:
	                        type = "Ultralight C";
	                        break;
	                    }
	                    sb.append("Mifare Ultralight type: ");
	                    sb.append(type);
	                }
	            }

	            return sb.toString();
	        }
	        
	        private String getHex(byte[] bytes) {
	            StringBuilder sb = new StringBuilder();
	            for (int i = bytes.length - 1; i >= 0; --i) {
	                int b = bytes[i] & 0xff;
	                if (b < 0x10)
	                    sb.append('0');
	                sb.append(Integer.toHexString(b));
	                if (i > 0) {
	                    sb.append(" ");
	                }
	            }
	            return sb.toString();
	        }

	        private long getDec(byte[] bytes) {
	            long result = 0;
	            long factor = 1;
	            for (int i = 0; i < bytes.length; ++i) {
	                long value = bytes[i] & 0xffl;
	                result += value * factor;
	                factor *= 256l;
	            }
	            return result;
	        }

	        private long getReversed(byte[] bytes) {
	            long result = 0;
	            long factor = 1;
	            for (int i = bytes.length - 1; i >= 0; --i) {
	                long value = bytes[i] & 0xffl;
	                result += value * factor;
	                factor *= 256l;
	            }
	            return result;
	        }

  
	        
	        
	        
	        
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
		case R.id.action_settings:
		Intent i= new Intent(MainActivity.this,Add2Tag.class);
		startActivity(i);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}
	
}
