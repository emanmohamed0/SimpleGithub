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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

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
    AdapterGit adapterGit;
    ListView lvGit;
    Data[] dataArray;
    SwipeRefreshLayout refreshLayout;
    CardView cardview;
    Data objectData;
    static Context context;
    DBContoller dbContoller;
    int pageroll = 0;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objectData = new Data();
        cardview = (CardView) findViewById(R.id.cardview);
        lvGit = (ListView) findViewById(R.id.lvGit);
        lvGit.setLongClickable(true);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        refreshLayout.setColorSchemeResources(R.color.green, R.color.red, R.color.orange);
        context = getBaseContext();
        dbContoller = new DBContoller(context);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        if (isNetworkConnected()) {
                            pageroll = pageroll + 1;
                            FetchGithub fetchGithub = new FetchGithub();
                            fetchGithub.execute("page=" + pageroll + "&", "per_page=10");
                        } else {
                            selectDataOffline();
                        }
                    }
                }, 3000);
            }
        });


        lvGit.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {
                Dialog(position);
                return true;
            }
        });

        if (isNetworkConnected()) {
        } else {
            selectDataOffline();
        }
        lvGit.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = lvGit.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        if (isNetworkConnected()) {
                            pageroll = pageroll + 1;
                            FetchGithub fetchGithub = new FetchGithub();
                            fetchGithub.execute("page=" + pageroll + "&", "per_page=10");

                        } else {
                            selectDataOffline();
                        }

                    }
                }
            }


        });
    }

    public void selectDataOffline() {
        try {
            Cursor cursor = dbContoller.get_dataselect();

            if (cursor.moveToFirst()) {

                dataArray = new Data[cursor.getCount()];

                for (int i = 0; i < cursor.getCount(); i++) {

                    dataArray[i] = new Data();
                    dataArray[i].setUser_Name(cursor.getString(1));
                    dataArray[i].setRepoName(cursor.getString(0));
                    dataArray[i].setDescription(cursor.getString(2));
                    dataArray[i].setHtml_url(cursor.getString(3));
                    cursor.moveToNext();
                }
                adapterGit = new AdapterGit(getBaseContext(), dataArray);
                lvGit.setAdapter(adapterGit);
            }
        } catch (Exception e) {

        }
    }

    // use to tell connection network
    static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Dialog show
    public void Dialog(final int position) {
        Log.v("long clicked", "position: " + position);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("If go to Repository");
        alertDialog.setMessage(dataArray[position].getHtml_url());
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = dataArray[position].getHtml_url();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

                        dialog.dismiss();
                    }
                });

        alertDialog.show();
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
            dataArray = new Data[datas.length];
            if (datas != null) {
                for (int i = 0; i < datas.length; i++) {
                    dataArray[i] = new Data();
                    dataArray[i].setUser_Name(datas[i].getUser_Name());
                    dataArray[i].setRepoName(datas[i].getRepoName());
                    dataArray[i].setDescription(datas[i].getDescription());
                    dataArray[i].setFork(datas[i].getFork());
                    dataArray[i].setHtml_url(datas[i].getHtml_url());

                    dbContoller.insert_db(datas[i].getRepoName(), datas[i].getUser_Name(), datas[i].getDescription(), datas[i].getHtml_url());

                }
                adapterGit = new AdapterGit(getBaseContext(), dataArray);
                lvGit.setAdapter(adapterGit);

            }


        }

    }


}
