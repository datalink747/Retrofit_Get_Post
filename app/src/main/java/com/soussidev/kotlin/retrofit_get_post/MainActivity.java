package com.soussidev.kotlin.retrofit_get_post;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.soussidev.kotlin.retrofit_get_post.MethodeGet.Constants;
import com.soussidev.kotlin.retrofit_get_post.MethodeGet.RequestInterface;
import com.soussidev.kotlin.retrofit_get_post.MethodeGet.ServerRequest;
import com.soussidev.kotlin.retrofit_get_post.MethodeGet.ServerResponse;
import com.soussidev.kotlin.retrofit_get_post.model.User;
import retrofit2.Call;
import retrofit2.Callback;


public class MainActivity extends AppCompatActivity {
    private TextInputLayout inputName,inputPrenom,inputCin;
    private AppCompatEditText editName,editPrenom,editCin;
    private AppCompatButton btnSend;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        initView();

        // go to List Users
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent getUser =new Intent(MainActivity.this,ListActivity.class);
                startActivity(getUser);


                Snackbar.make(view, "List of Users", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * @author Soussi
     *
     * Func initView()
     */
    private void initView()
    {
        inputName =(TextInputLayout)findViewById(R.id.input_nom);
        inputPrenom =(TextInputLayout)findViewById(R.id.input_prenom);
        inputCin =(TextInputLayout)findViewById(R.id.input_cin);

        editName=(AppCompatEditText)findViewById(R.id.edit_nom);
        editPrenom=(AppCompatEditText)findViewById(R.id.edit_prenom);
        editCin=(AppCompatEditText)findViewById(R.id.edit_cin);

        btnSend=(AppCompatButton)findViewById(R.id.btnSend);

        // btn add Data to database
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=editName.getText().toString().trim();
                String prenom=editPrenom.getText().toString().trim();
                String cin=editCin.getText().toString().trim();

                //check EditText is not empty
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(prenom) && !TextUtils.isEmpty(cin)) {

                    adduser(name,prenom,cin);
                }else
                {
                    Snackbar.make(view, "SVP! Remplir tout les champs ", Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }


    /**
     * @author Soussi
     *
     * Func addUser()
     *
     * @param cin
     * @param name
     * @param prenom
     */

    private void  adduser(String name,String prenom,String cin)
    {
        int cin_=Integer.valueOf(cin);
        //call service
        RequestInterface requestInterface = Constants.getClient().create(RequestInterface.class);

        User user =new User();
        user.setNomUser(name);
        user.setPrenomUser(prenom);
        user.setCinUser(cin_);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.ADD_USER_OPERATION);
        request.setUser(user);

        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();

                      Toast.makeText(MainActivity.this,resp.getMessage(),Toast.LENGTH_SHORT).show();

                 editName.setText("");
                 editPrenom.setText("");
                 editCin.setText("");

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {


                Log.d(Constants.TAG,"failed");

                    Toast.makeText(MainActivity.this,t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
