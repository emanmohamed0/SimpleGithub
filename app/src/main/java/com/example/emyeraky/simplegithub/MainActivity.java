package com.example.emyeraky.simplegithub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Data[] dataArray;
    SwipeRefreshLayout refreshLayout;
    static Context context;
    DBContoller dbContoller;
    int pageroll = 1;
    private RecyclerView rv;
    RVAdapter rvAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        rv = (RecyclerView) findViewById(R.id.lvGit);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        refreshLayout.setColorSchemeResources(R.color.green, R.color.red, R.color.orange);
        context = getBaseContext();
        dbContoller = new DBContoller(context);
        /////////////////////////////////////

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        pageroll = pageroll + 1;
                        getData();

                    }
                }, 3000);
            }
        });


        getData();

        rv.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {
//                pageroll = pageroll + 1;
//                getData();
            }

            @Override
            public void onLoadMore() {
                pageroll = pageroll + 1;
                if (isNetworkConnected()) {
                    FetchGithub fetchGithub = new FetchGithub();
                    fetchGithub.execute("page=" + pageroll + "&", "per_page=30");
                } else {
                    selectDataOffline();
                }
            }
        });


    }

    public void getData() {
        if (isNetworkConnected()) {
            FetchGithub fetchGithub = new FetchGithub();
            fetchGithub.execute("page=" + pageroll + "&", "per_page=10");
        } else {
            selectDataOffline();
        }
    }

    public void selectDataOffline() {
        try {
            Cursor cursor = dbContoller.get_dataselect();

            if (cursor.moveToFirst()) {

                dataArray = new Data[20];

                for (int i = 0; i < 20; i++) {

                    dataArray[i] = new Data();
                    dataArray[i].setRepoName(cursor.getString(0));
                    dataArray[i].setUser_Name(cursor.getString(1));
                    dataArray[i].setDescription(cursor.getString(2));
                    dataArray[i].setHtml_url(cursor.getString(3));
                    dataArray[i].setFork((cursor.getInt(4))>0);
                    cursor.moveToNext();
                }
                rvAdapter = new RVAdapter(dataArray);
                rv.setAdapter(rvAdapter);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        getData();
        super.onStart();
    }

    // use to tell connection network
    static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //get data from Json
    private Data[] getGitDataFromJson(String gitJsonStr) throws JSONException {
        final String name = "name";
        final String description = "description";
        final String fork = "fork";
        final String repoName = "full_name";
        final String html_url = "html_url";

        JSONArray jsonArray = new JSONArray(gitJsonStr);
        dataArray = new Data[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject gitload = jsonArray.getJSONObject(i);

            dataArray[i] = new Data();
            dataArray[i].setUser_Name(gitload.getString(name));
            dataArray[i].setDescription(gitload.getString(description));
            dataArray[i].setFork(gitload.getBoolean(fork));
            dataArray[i].setRepoName(gitload.getString(repoName));
            dataArray[i].setHtml_url(gitload.getString(html_url));

        }

        return dataArray;
    }


    public class FetchGithub extends AsyncTask<String, ProgressDialog, Data[]> {
        private String LOG_TAG = FetchGithub.class.getSimpleName();

        @Override
        protected Data[] doInBackground(String... params) {

            if (params.length == 0) {

                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String gitJsonStr = null;

            try {
                final String FORECAST_BASE_URL =
                        "https://api.github.com/users/square/repos?client_id=aa4921b3cd23014c38b9&client_secret=d608d9e258688491a1e5f9da6b1ec063f86a3098&" + params[0] + params[1];

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                gitJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getGitDataFromJson(gitJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Data[] datas) {
            int id;
            dataArray = new Data[datas.length];
            if (datas != null) {
                for (int i = 0; i < datas.length; i++) {
                    dataArray[i] = new Data();
                    dataArray[i].setUser_Name(datas[i].getUser_Name());
                    dataArray[i].setRepoName(datas[i].getRepoName());
                    dataArray[i].setDescription(datas[i].getDescription());
                    dataArray[i].setFork(datas[i].getFork());
                    dataArray[i].setHtml_url(datas[i].getHtml_url());

                    id = dbContoller.insert_db(datas[i].getRepoName(), datas[i].getUser_Name(), datas[i].getDescription(), datas[i].getHtml_url(),datas[i].getFork());
                    if (id > 0) {

                    } else
                        Toast.makeText(MainActivity.this, "Not insert", Toast.LENGTH_SHORT).show();

                }
                rvAdapter = new RVAdapter(dataArray);
                rv.setAdapter(rvAdapter);
            }


        }

    }


}
