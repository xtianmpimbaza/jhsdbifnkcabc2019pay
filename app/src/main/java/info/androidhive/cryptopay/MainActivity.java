package info.androidhive.cryptopay;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crowdfire.cfalertdialog.CFAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.androidhive.cryptopay.Globals.CONFIG;
import info.androidhive.cryptopay.database.DatabaseHelper;
import info.androidhive.cryptopay.database.model.Note;
import info.androidhive.cryptopay.utils.MyDividerItemDecoration;
import info.androidhive.cryptopay.utils.RecyclerTouchListener;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ProgressDialog progressDialog;
    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    //    private TextView noNotesView;
    SharedPreferences.Editor editor;

    private DatabaseHelper db;
    @BindView(R.id.empty_notes_view)
    TextView noNotesView;
    @BindView(R.id.user_balance)
    TextView user_balance;
//    Handler mHandler = new Handler();

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionsDialog();
            }
        });

        ButterKnife.bind(this);
        user_balance.setText("58700 BNU");
//        RecyclerView recyclerView = findViewById(R.id.recycler_view);

//        db = new DatabaseHelper(this);
//        notesList.clear();
//
//        mAdapter = new NotesAdapter(this, notesList);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
//        recyclerView.setAdapter(mAdapter);

//        this.getBalance();
//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
//                recyclerView, new RecyclerTouchListener.ClickListener() {
//
//            @Override
//            public void onClick(View view, final int position) {
//                showDetailsDialog(position);
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//            }
//        }));
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Connecting....");

//        new LongOperation().execute("");
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

    private void showActionsDialog() {
//        Log.e("Scanned",address);
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Select")
                .setMessage("Deposit or withdraw")
                .addButton("Deposit", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent deposit = new Intent(MainActivity.this, DepositActivity.class);
                        deposit.putExtra("to_activity", "DepositActivity");
                        startActivity(deposit);
                        dialog.dismiss();
                    }
                })
                .addButton("Withdraw/Spend", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent withdraw = new Intent(MainActivity.this, ScanActivity.class);
                        withdraw.putExtra("to_activity", "WithdrawActivity");
                        startActivity(withdraw);
                        dialog.dismiss();
                    }
                });
        builder.show();
    }


    private void showDetailsDialog(final int position) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Details")
                .setMessage("Time: " + notesList.get(position).getTimestamp() + "\nAmount: " + notesList.get(position).getNote() + "\n Fee: " + notesList.get(position).getFee() + "\n Id: " + notesList.get(position).getTxid())
                .addButton("Explorer", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse("http://192.168.1.117:8081/BnuExp/transactions.php?txid=" + notesList.get(position).getTxid()));
                        startActivity(viewIntent);
                        dialog.dismiss();
                    }
                })
                .addButton("Close", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }


//---------------------------------------------------------

    private class LongOperation extends AsyncTask<String, Void, List<Note>> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected List<Note> doInBackground(String... params) {
            getTransactions();
            return db.getAllNotes();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(List<Note> result) {
            notesList.addAll(result);
            mAdapter.notifyDataSetChanged();
            if (db.getNotesCount() > 0) {
                noNotesView.setVisibility(View.GONE);
            } else {
                noNotesView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            notesList.clear();
            noNotesView.setVisibility(View.GONE);
            db = new DatabaseHelper(getBaseContext());
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void getTransactions() throws NullPointerException {
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(this));
//        progressDialog.show();
        final JSONObject params = new JSONObject();

        try {
            params.put("method", "getTxes");
            params.put("address", getWallet("address"));
            params.put("number", 10);
        } catch (JSONException e) {
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, CONFIG.PRE_BNU + getPeer() + CONFIG.POST_BNU, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String stat = response.toString();
                        JSONObject jsnobject = null;
                        try {
                            jsnobject = new JSONObject(stat);
                            JSONArray jsonArray = jsnobject.getJSONArray("response");
                            db.insertTxs(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        onNetworkBnu();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
//                onNetworkFailed();
            }
        });
        queue.add(req);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void getBalance() {
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(this));
//        progressDialog.show();
        final JSONObject paramvalues = new JSONObject();

        try {
            paramvalues.put("method", "getMobileBalance");
            paramvalues.put("address", getWallet("address"));
        } catch (JSONException e) {
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, CONFIG.PRE_BNU + getPeer() + CONFIG.POST_BNU, paramvalues,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        progressDialog.dismiss();
//                        Log.e("response_balance", response.toString());
                        try {
                            double available = Double.parseDouble(response.getJSONObject("response").getString("available"));
                            double pending = Double.parseDouble(response.getJSONObject("response").getString("pending"));
                            double bal = available + pending;
//                            String current_bal = user_balance.getText().toString();
                            SharedPreferences wallpref = getSharedPreferences("MY_BALANCE", MODE_PRIVATE);
                            String current_bal = wallpref.getString("balance", null);
//                            Log.e("phone_bal",""+current_bal);
//                            Log.e("updated_bal", ""+bal);

                            if (bal > Double.parseDouble(current_bal)) {
                                user_balance.setText(String.format("%,.0f", bal) + " BNU");
//                                ring();
                            }
                            onNetworkSuccessful(response.getJSONObject("response").getString("available"));
                            editor = getSharedPreferences("MY_BALANCE", MODE_PRIVATE).edit();
                            editor.putString("balance", Double.toString(bal));
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getLocalBalance();
            }
        });
        queue.add(req);
    }

    @SuppressLint("DefaultLocale")
    void onNetworkSuccessful(final String balance) {
        user_balance.setText(String.format("%,.2f", Double.parseDouble(balance)) + " BNU");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    void getLocalBalance() throws NullPointerException {
        SharedPreferences wallpref = Objects.requireNonNull(getSharedPreferences("MY_BALANCE", MODE_PRIVATE));
        String balance = wallpref.getString("balance", null);
        assert balance != null;
        user_balance.setText(String.format("%,.0f", Double.parseDouble(balance)) + " BNU");
    }

    String getWallet(String key) {
        SharedPreferences wallpref = getSharedPreferences("MY_KEYS", MODE_PRIVATE);
        String prefbalance = wallpref.getString(key, null);
        return prefbalance;
    }

//    void ring() {
//        MediaPlayer ring = MediaPlayer.create(this, R.raw.balance);
//        ring.start();
//    }

    String getPeer() {
        SharedPreferences peerpref = getSharedPreferences("MY_PEERS", MODE_PRIVATE);
        String cur_peer = peerpref.getString("current_peer", null);
        return cur_peer;
    }

}
