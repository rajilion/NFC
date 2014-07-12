package com.example.nfccontactadder;

import java.nio.charset.Charset;
import java.util.Locale;



import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Add2Tag extends Activity{


//	final Context context = this;

EditText phone,name;
TextView text;
Button enter;
String phone_number=null,contact_name=null,contact_detail=null;
private NfcAdapter nfcAdapter;
private PendingIntent mPendingIntent;
private IntentFilter[] mFilters;
private String[][] mTechLists;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
setContentView(R.layout.writer);
name = (EditText) findViewById(R.id.name);
phone = (EditText) findViewById(R.id.phone);
text = (TextView) findViewById(R.id.text);
text.setText("Enter Name and Contact Number");
enter = (Button) findViewById(R.id.submit);
enter.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		contact_name = name.getText().toString();
		phone_number = phone.getText().toString();
		contact_detail = contact_name.trim()+","+phone_number.trim();
	text.setText("Hit the NFC tag");
	}
});



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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
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
//	handleIntent(intent);		
//	super.onNewIntent(intent);
//	nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
	String action = intent.getAction();
	if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
	{
		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		handleIntent(intent,detectedTag);
	}

//
}
//	for new intent
private void handleIntent(Intent intent,Tag detectedTag) {
	// TODO Auto-generated method stub
//	String phone_number = text.getText().toString();
	Toast.makeText(getApplicationContext(), contact_detail, Toast.LENGTH_SHORT).show();
	String action = intent.getAction();
	System.out.println("tag: "+action);
	if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
	{
//		Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
		System.out.println("detected tag"+detectedTag);
		Locale locale= new Locale("en","US");
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
		boolean encodeInUtf8=false;
		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") :
		Charset.forName("UTF-16");
		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);
//		String RTD_TEXT = "9962047313";
		byte[] textBytes=null;
		try{
		textBytes = contact_detail.getBytes(utfEncoding);
		}catch(NullPointerException e)
		{
			Toast.makeText(getApplicationContext(), "Enter phone number and Hit the tag", Toast.LENGTH_SHORT).show();
		return;
		}
//		try{
//		 textBytes = phone_number.getBytes(utfEncoding);
//		 System.out.println("text"+textBytes);
//		}catch(Exception e)
//		{
//			Toast.makeText(getApplicationContext(), "Enter the phone numeber and tap the tag", Toast.LENGTH_LONG).show();
//			
//		}
		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
		NdefRecord.RTD_TEXT, new byte[0], data);
		NdefMessage newMessage= new NdefMessage(new NdefRecord[] { textRecord });
				
		System.out.println("result "+newMessage.toString());
		boolean result = writeNdefMessageToTag(newMessage, detectedTag);
		System.out.println("result "+result);
		if(result==true)
		{
		Toast.makeText(getApplicationContext(), "Data Successfully", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(getApplicationContext(), "Data writing failed ", Toast.LENGTH_SHORT).show();
		}
	}
}


//for normal on create
	private void handleIntent(Intent intent) {
		// TODO Auto-generated method stub
//		String phone_number = text.getText().toString();
		Toast.makeText(getApplicationContext(), contact_detail, Toast.LENGTH_SHORT).show();
		String action = intent.getAction();
		System.out.println("tag: "+action);
		if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
		{
			Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
			System.out.println("detected tag"+detectedTag);
			Locale locale= new Locale("en","US");
			byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
			boolean encodeInUtf8=false;
			Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") :
			Charset.forName("UTF-16");
			int utfBit = encodeInUtf8 ? 0 : (1 << 7);
			char status = (char) (utfBit + langBytes.length);
//			String RTD_TEXT = "9962047313";
			byte[] textBytes = contact_detail.getBytes(utfEncoding);
//			try{
//			 textBytes = phone_number.getBytes(utfEncoding);
//			 System.out.println("text"+textBytes);
//			}catch(Exception e)
//			{
//				Toast.makeText(getApplicationContext(), "Enter the phone numeber and tap the tag", Toast.LENGTH_LONG).show();
//				
//			}
			byte[] data = new byte[1 + langBytes.length + textBytes.length];
			data[0] = (byte) status;
			System.arraycopy(langBytes, 0, data, 1, langBytes.length);
			System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
			NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
			NdefRecord.RTD_TEXT, new byte[0], data);
			NdefMessage newMessage= new NdefMessage(new NdefRecord[] { textRecord });
					
			System.out.println("result "+newMessage.toString());
			boolean result = writeNdefMessageToTag(newMessage, detectedTag);
			System.out.println("result "+result);
			if(result==true)
			{
			Toast.makeText(getApplicationContext(), "Data Successfully", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(getApplicationContext(), "Data writing failed ", Toast.LENGTH_SHORT).show();
			}
		}
	}
	private boolean writeNdefMessageToTag(NdefMessage newMessage, Tag detectedTag) {
		int size = newMessage.toByteArray().length;
		System.out.println(size);
		try{
			Ndef ndef = Ndef.get(detectedTag);
			System.out.println("ndef"+ndef);
			if(ndef!=null)
			{System.out.println("ndef1 "+ndef);
				ndef.connect();
				if(!ndef.isWritable())
				{System.out.println("ndef2 "+ndef);
					Toast.makeText(getApplicationContext(), "Tag is readonly", Toast.LENGTH_SHORT).show();
					return false;
				}
				if(ndef.getMaxSize()<size)
				{System.out.println("ndef3 "+ndef);
					Toast.makeText(getApplicationContext(), "data cannot be written to tag since msg size is greater than tag capacity", Toast.LENGTH_LONG).show();
				return false;
				}
				ndef.writeNdefMessage(newMessage);
				System.out.println("write");
			ndef.close();
			Toast.makeText(getApplicationContext(), "Phone number written to Tag successfully", Toast.LENGTH_SHORT).show();
			return true;
			}
			else{
				System.out.println("ndef4 "+ndef);
				NdefFormatable ndefFormat  = NdefFormatable.get(detectedTag);
						System.out.println("ndefformat"+ndef);
				if(ndefFormat!=null)
				{
					try{
						ndefFormat.connect();
						ndefFormat.format(newMessage);
						ndefFormat.close();
						Toast.makeText(getApplicationContext(), "Phone number written to Tag successfully", Toast.LENGTH_SHORT).show();
						return true;
					}catch(Exception e)
					{
						Toast.makeText(getApplicationContext(), "Tag cannot be formatted", Toast.LENGTH_SHORT).show();
						return false;
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Tag is not supported", Toast.LENGTH_LONG).show();
					return false;
				}
			}
		}catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), "Write operation is failed", Toast.LENGTH_LONG).show();
		}
		return false;
		// TODO Auto-generated method stub
		
	}
	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
 
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{new String[] { MifareClassic.class.getName(), NfcA.class.getName() }};
 
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        
    
         
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
        
      

    }
	  public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
          adapter.disableForegroundDispatch(activity);
      }
	
	
}
