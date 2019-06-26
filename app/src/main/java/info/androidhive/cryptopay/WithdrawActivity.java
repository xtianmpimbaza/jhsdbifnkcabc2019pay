package info.androidhive.cryptopay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crowdfire.cfalertdialog.CFAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.androidhive.cryptopay.Globals.CONFIG;
import info.androidhive.cryptopay.Globals.PrefManager;

public class WithdrawActivity extends AppCompatActivity {
    String balance;
    String token;
    ProgressDialog progressDialog;
    String payid = "";
    PrefManager prefManager;
    @BindView(R.id.input_address)
    EditText inputAddress;
    @BindView(R.id.amount)
    EditText inputAmount;

    @OnClick(R.id.btn_send)
    public void submit() {
        String addr = inputAddress.getText().toString();
        String short_add = addr;
        if (addr.length() > 11) {
            short_add = addr.substring(0, 11) + "..." + addr.substring(addr.length() - 11);
        }
        if (isValid()) {
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
            builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
            builder.setTitle("Send BNU");
            builder.setMessage("Send " + inputAmount.getText().toString() + " to " + short_add);
            builder.addButton("Continue", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    isOwner();
                }
            });
            builder.addButton("Cancel", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        SharedPreferences prefs = getSharedPreferences("MY_BALANCE", MODE_PRIVATE);
        String prefbalance = prefs.getString("balance", null);
        if (prefbalance != null) {
            balance = prefs.getString("balance", "0"); //"0" is the default value.
        }
        prefManager = new PrefManager(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending .....");

        String args = getIntent().getStringExtra("code");
        if (!args.isEmpty()) {
            this.fillData(args);
        }

    }

    private boolean isValid() {
        return true;
    }

    void fillData(String strEditText) {
        Log.e("code", strEditText);
        String jsonString = strEditText.replace("?", ":").replace("=", ":");
        String[] arraySplit = jsonString.split(":");

        if (arraySplit.length < 5 || arraySplit.length > 6) {
            final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                    .setTitle("Failed")
                    .setIcon(getResources().getDrawable(R.drawable.ic_failed))
                    .setMessage("Wrong data found");
            builder.show();
        } else {
            inputAddress.setText(arraySplit[1]);
            inputAmount.setText(arraySplit[5]);
            payid = arraySplit[3];

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void send(final String tkn) throws NoSuchAlgorithmException {
        RequestQueue queue = Volley.newRequestQueue(this);
        progressDialog.show();
        final JSONObject params = new JSONObject();

        String address = inputAddress.getText().toString();
        String amount = inputAmount.getText().toString();

        try {
            params.put("oauth", cryptoHash(tkn));
            params.put("method", "createMobileTransaction");
            params.put("from", this.getWallet("address"));
            params.put("amount", amount);
            params.put("to", address);
            if (!payid.equalsIgnoreCase("")) {
                params.put("payid", this.payid);
            }
        } catch (JSONException e) {
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, CONFIG.PRE_BNU + getPeer() + CONFIG.POST_BNU, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("fail")) {
                                String reason = response.getJSONObject("response").getString("reason");
                                traansactionFailed(reason);
                            } else {
                                onNetworkSuccessful(params);
                            }
                            Log.e("response_address", response.getString("response"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                onNetworkFailed(tkn);
            }
        });
        queue.add(req);
    }

    void isOwner() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String pn = userInput.getText().toString();
                                Log.e("conf_strl", pn);
                                Log.e("conf_prefmanager", prefManager.getPin());
                                if (pn.equalsIgnoreCase(prefManager.getPin())) {
                                    //send();
                                    getAuth();
                                } else {
                                    onAuthFailed();

                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    void onAuthFailed() {
        final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setTitle("Failed")
                .setIcon(getResources().getDrawable(R.drawable.ic_failed))
                .setMessage("Wrong PIN");
        builder.show();
    }

    void onNetworkSuccessful(final JSONObject params) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setTitle("Sent")
                .setIcon(getResources().getDrawable(R.drawable.ic_complete))
                .setMessage("Sent Successfully");

        builder.onDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
        builder.show();
    }

    void onNetworkFailed(final String tokn) {
        final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setTitle("Error")
                .setIcon(getResources().getDrawable(R.drawable.ic_failed))
                .setMessage("Failed, check your internet connection and try again")
                .addButton("Retry", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            send(tokn);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addButton("Cancel", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    void traansactionFailed(String reason) {
        final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setTitle("Failed")
                .setIcon(getResources().getDrawable(R.drawable.ic_failed))
                .setMessage("Error: " + reason);
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String cryptoHash(String tok) throws NoSuchAlgorithmException {
        MessageDigest md = null;
        md = MessageDigest.getInstance("SHA-256");
        String text = getWallet("spendSecretKey") + tok;
        // Change this to UTF-16 if needed
        md.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        String hex = String.format("%064x", new BigInteger(1, digest));
        System.out.println(hex);
        return hex;
    }

    String getWallet(String key) {
        SharedPreferences wallpref = getSharedPreferences("MY_KEYS", MODE_PRIVATE);
        String prefbalance = wallpref.getString(key, null);
        return prefbalance;
    }

    void getAuth() {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        final JSONObject parameters = new JSONObject();
        try {
            parameters.put("method", "generateToken");
            parameters.put("address", getWallet("address"));
        } catch (JSONException e) {
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, CONFIG.PRE_BNU + getPeer() + CONFIG.POST_BNU, parameters,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            Log.e("response_apitkn", response.toString());
                            token = response.getJSONObject("response").getString("token");
                            send(token);
                        } catch (JSONException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                onTokenFailed();
            }
        });
        queue.add(req);
    }

    void onTokenFailed() throws IllegalStateException {
        final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setTitle("Error")
                .setIcon(getResources().getDrawable(R.drawable.ic_failed))
                .setMessage("Failed, check your internet connection and try again")
                .addButton("Retry", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getAuth();
                    }
                })
                .addButton("Cancel", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    String getPeer() {
        SharedPreferences peerpref = getSharedPreferences("MY_PEERS", MODE_PRIVATE);
        return peerpref.getString("current_peer", null);
    }

}
