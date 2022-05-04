package com.obana.shellexcutor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button execute, clear;
    EditText inputCommand;
    TextView outputResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        execute = findViewById(R.id.execute_button);
        clear = findViewById(R.id.clear_button);

        inputCommand = findViewById(R.id.input_text);
        outputResult = findViewById(R.id.output_text);
        outputResult.setMovementMethod(new ScrollingMovementMethod());

        execute.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {

					// Executes the command.

					Process process = Runtime.getRuntime().exec(inputCommand.getText().toString());

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

					outputResult.append("$ " + inputCommand.getText().toString() + "\n" +
                        "> " + output.toString() + "\n");

				} catch (IOException e) {

					throw new RuntimeException(e);

				} catch (InterruptedException e) {

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
    }

}
