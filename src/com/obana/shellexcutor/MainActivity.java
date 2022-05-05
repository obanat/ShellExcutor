package com.obana.shellexcutor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity_SExcutor";
	private static final int HISTORY_MIN_LENGTH = 5;
    Button execute, clear;
    EditText inputCommand;
    TextView outputResult;
    private Spinner historySpinner;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        execute = findViewById(R.id.execute_button);
        clear = findViewById(R.id.clear_button);

        inputCommand = findViewById(R.id.input_text);
        outputResult = findViewById(R.id.output_text);
        outputResult.setMovementMethod(new ScrollingMovementMethod());
		
		historySpinner=(Spinner)findViewById(R.id.spinner);
		historySpinnerInit();
		
        execute.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {

					// Executes the command.
					String commandLine = inputCommand.getText().toString();//"/data/local/tmp/frida-server -l 0.0.0.0:6666 &"
					Process process = Runtime.getRuntime().exec(
						new String[]{"/system/bin/sh", "-c", commandLine});

					// Reads stdout.
					// NOTE: You can write to stdin of the command using
					//       process.getOutputStream().
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(process.getInputStream()));
					int read;
					char[] buffer = new char[4096];
					StringBuffer output = new StringBuffer();
					while ((read = reader.read(buffer)) > 0) {
						output.append(buffer, 0, read);
					}
					reader.close();

					// Waits for the command to finish.
					process.waitFor();

					outputResult.append("$ " + commandLine + "\n" +
                        "> " + output.toString() + "\n");
					save2history(commandLine);
				} catch (IOException e) {
					Log.e(TAG, "IOException: " + e);
					throw new RuntimeException(e);
				} catch (InterruptedException e) {
					Log.e(TAG, "InterruptedException: " + e);
					throw new RuntimeException(e);
				}
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outputResult.setText("");
            }
        });
		
		
		historySpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				String text = historySpinner.getSelectedItem().toString();
				inputCommand.setText(text);
			}

			public void onNothingSelected(AdapterView<?> parentView) 
			{
				//Toast.makeText("log", "onNothingSelected", Toast.LENGTH_LONG).show();
			}
		});
    }

	private void historySpinnerInit(){
        
        ArrayList<String> allContents = new ArrayList<String>();
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(this);  
		int size = mSharedPreference1.getInt("list_size", 0);    
  
		for(int i=0; i<size; i++) {  
			allContents.add(mSharedPreference1.getString("contents_" + i, null));    
		}

        ArrayAdapter<String> aspnContents=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,allContents);
        aspnContents.setDropDownViewResource(android.R.layout.simple_spinner_item);
        historySpinner.setAdapter(aspnContents);
    }
	
	private void save2history(String cmd){
		
		if (cmd == null) return;
		if (cmd.length() < HISTORY_MIN_LENGTH) return;
		cmd = cmd.trim(); //remove space
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor mEdit1= sp.edit();
		int size = sp.getInt("list_size", 0);   
		 
		 
 
		int i = 0;
		for(i=0;i<size;i++) {
			String value = sp.getString("contents_" + i, null);
			if (cmd.equals(value)) return; //do nothing    
		}  
		mEdit1.putString("contents_" + i, cmd);
		mEdit1.putInt("list_size",i+1); /**/  
		mEdit1.commit();
		historySpinnerInit();
    }
}
