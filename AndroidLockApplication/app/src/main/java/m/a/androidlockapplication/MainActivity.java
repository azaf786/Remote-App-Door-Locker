package m.a.androidlockapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    androidData resultObject; //response from the server is stored in this object
    //initialises the data object
    androidData androidInfo = new androidData("unknown", "unknown", "unknown", 0);
    Gson gson = new Gson();
    String pinNumber;
    EditText pinNumberView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pinNumberView = findViewById(R.id.pinNumber);

    }


    public void senddatatoserver(View view) {
        System.out.println("Login Button Pressed. Running senddatatoserver");
        pinNumber = pinNumberView.getText().toString();
        new SendJsonDataToServer().execute();
    }


    private String GetHttpRequest() throws IOException {
        URL url = new URL("http://10.0.2.2:8080/MobileAppAssignmentServer/androidValidation?pinNumber=" + pinNumber);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("HTTP request, response code from the server: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK)// This if statement checks for the 200 response code before executing
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String response = convertStreamToString(br);
            System.out.println("This is the response from the server" + response);
            try
            {
                resultObject = gson.fromJson(response, androidData.class);
                System.out.println("Result object recieved from the server: " + resultObject + "\n");
                try //setting the initialised android object to the values received from the server when the correct pin is entered
                {
                    androidInfo.setAndroid_id(resultObject.getAndroid_id());
                    androidInfo.setRoom_name(resultObject.getRoom_name());
                    androidInfo.setTag_id(resultObject.getTag_id());
                    androidInfo.setUnique_key(resultObject.getUnique_key());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                br.close();
                return response;

            } catch (JsonSyntaxException | IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String convertStreamToString(BufferedReader in) {
        java.util.Scanner js = new java.util.Scanner(in).useDelimiter("\\A");
        return js.hasNext() ? js.next() : "";
    }


    void startActivity()
    {
        Intent intent = new Intent(this, tagDetails.class);
        intent.putExtra("TagObject", resultObject);
        System.out.println("this is a result object: " + resultObject);
        startActivity(intent);
    }

    private class SendJsonDataToServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try
            {
                String response = GetHttpRequest();
                return response;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response)
        {
            super.onPostExecute(response);
            //if the response is not null start the tagDetails activity
            if(!response.equals(""))
            {
                System.out.println("Response received on Post Execute: " + response);
                startActivity();
                Toast.makeText(MainActivity.this, "Access granted.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(MainActivity.this, "No users associated with the pin "+pinNumber + ". Please enter a valid key.", Toast.LENGTH_LONG).show();
            }
        }

    }
}

