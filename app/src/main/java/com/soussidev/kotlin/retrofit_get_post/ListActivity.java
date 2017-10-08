package com.soussidev.kotlin.retrofit_get_post;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.soussidev.kotlin.retrofit_get_post.MethodeGet.Constants;
import com.soussidev.kotlin.retrofit_get_post.MethodeGet.RequestInterface;
import com.soussidev.kotlin.retrofit_get_post.MethodeGet.ServerRequest;
import com.soussidev.kotlin.retrofit_get_post.MethodeGet.ServerResponse;
import com.soussidev.kotlin.retrofit_get_post.adapter.UserAdapter;
import com.soussidev.kotlin.retrofit_get_post.model.PrefItem;
import com.soussidev.kotlin.retrofit_get_post.model.User;
import com.soussidev.kotlin.retrofit_get_post.rxSharedPref.RxSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;



public class ListActivity extends AppCompatActivity {

    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_THREE = 3;
    private RecyclerView recyclerView;
    private UserAdapter itemAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<User> itemsuser;
    private SharedPreferences sharedPreferences;
    private RxSharedPreferences rxShared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // toolbar.setTitle("List of Users");
        toolbar.setSubtitle("By Soussidev");

       AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);





        //Call function getData()
        getData();


        //Init RxsharedPref
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        rxShared = RxSharedPreferences.with(sharedPreferences);

        //Default SPAN
        rxShared.getInt("span", 0)
                .subscribe(span_count -> {
                    //Get Span Count
                    Log.d("Get pref", "SPAN: " + span_count);
                    if(span_count.equals(0))
                    {
                        // If Span Count 0 Replace With 1
                        rxShared.putString("app_name","layout_switch_RXShared_Pref")
                                .flatMap(span_c ->rxShared.putInteger("span",SPAN_COUNT_ONE))
                                .flatMap(span_name ->rxShared.putString("span.name","Single"))
                                .flatMap(span_item -> rxShared.getAll())
                                .flatMap(integerMap -> Observable.fromIterable(integerMap.entrySet()))
                                .map(Object::toString)

                                .subscribe(s -> Log.d("TAG 1", s));
                    }
                });


        Observable.just(new PrefItem())
                .flatMap(prefItem -> rxShared.getInt("span", 1), (prefItem, sp) -> {
                    prefItem.setSpan_count(sp);
                    return prefItem;
                })

                .flatMap(prefItem -> rxShared.getString("span.name", ""), (prefItem, sp) -> {
                    prefItem.setSpan_name(sp);
                    return prefItem;
                })

                .subscribe(prefItem -> {
                    Log.d("TAG 3 NUM:", String.valueOf(prefItem.getSpan_count()));
                    Log.d("TAG 3 NAME:", prefItem.getSpan_name());
                    //Get span From RXPref to Layout Manager
                    gridLayoutManager = new GridLayoutManager(this, prefItem.getSpan_count());
                });
//Recyclerview

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(gridLayoutManager);
       // recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        itemAdapter = new UserAdapter(itemsuser, gridLayoutManager,ListActivity.this);
        recyclerView.setAdapter(itemAdapter);



        // btn add user
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add User", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent addUser =new Intent(ListActivity.this,MainActivity.class);
                startActivity(addUser);


            }
        });
    }

    /**
     * @author Soussi
     *
     * Func Call Response ()
     *
     *
     */

    private void getData()

    {
        itemsuser =new ArrayList<>();

        RequestInterface requestInterface = Constants.getClient().create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_USER_OPERATION);

        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();

                //      Toast.makeText(getActivity(),resp.getMessage(),Toast.LENGTH_SHORT).show();


                if(resp.getResult().equals(Constants.SUCCESS)){

                    int responseCode = response.code();


                    for (User cn : resp.getUser()) {

                        Log.i("list user", String.valueOf(cn.getNomUser()));

                        itemsuser.add(cn);
                        itemAdapter.notifyItemRangeChanged(0, itemAdapter.getItemCount());

                    }
                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {


                Log.d(Constants.TAG,"failed");

                //    Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }



    /**
     * @author Soussi
     *
     * Func switchlayout()
     *
     *
     */


    private void switchLayout() {
        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_ONE) {
            gridLayoutManager.setSpanCount(SPAN_COUNT_THREE);

            rxShared.putString("app_name","layout_switch_RXShared_Pref")
                    .flatMap(span_count ->rxShared.putInteger("span",SPAN_COUNT_THREE))
                    .flatMap(span_name ->rxShared.putString("span.name","Multiple"))
                    .flatMap(span_item -> rxShared.getAll())
                    .flatMap(integerMap -> Observable.fromIterable(integerMap.entrySet()))
                    .map(Object::toString)

                    .subscribe(s -> Log.d("Switch_layout 3:", s));


        } else {
            gridLayoutManager.setSpanCount(SPAN_COUNT_ONE);

            rxShared.putString("app_name","layout_switch_RXShared_Pref")
                    .flatMap(span_count ->rxShared.putInteger("span",SPAN_COUNT_ONE))
                    .flatMap(span_name ->rxShared.putString("span.name","Single"))
                    .flatMap(span_item -> rxShared.getAll())
                    .flatMap(integerMap -> Observable.fromIterable(integerMap.entrySet()))
                    .map(Object::toString)

                    .subscribe(s -> Log.d("Switch_layout 1:", s));


        }
        itemAdapter.notifyItemRangeChanged(0, itemAdapter.getItemCount());
    }

    /**
     * @author Soussi
     *
     * Func switchIcon()
     *
     * @param item

     */
    private void switchIcon(MenuItem item) {
        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_THREE) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_span_1));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_span_3));
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_switch_layout) {
            switchLayout();
            switchIcon(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
