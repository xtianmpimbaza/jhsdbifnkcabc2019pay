package info.androidhive.cryptopay;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DepositActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    @BindView(R.id.etqr)
    EditText etqr;
    @BindView(R.id.display)
    TextView display;
    @BindView(R.id.txt_address)
    TextView txt_address;

    @BindView(R.id.iv)
    ImageView iv;

    @OnClick(R.id.bt_copy)
    void copyAdress() {
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("bux_address", getWallet("address"));
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Address copied", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn)
    void submitButton(View view) {
        progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
//                progressDialog.setIndeterminate(true);
        if (etqr.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Invalid Amount!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            etqr.onEditorAction(EditorInfo.IME_ACTION_DONE);

            if (progressDialog != null && progressDialog.isShowing()) {
                new Handler().postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        QRCodeWriter writer = new QRCodeWriter();
                        try {
                            String payid = cryptoHash();
                            BitMatrix bitMatrix = writer.encode("binusu:" + getWallet("address") + "?payid=" + payid + "?amount=" + etqr.getText().toString(), BarcodeFormat.QR_CODE, 650, 650);
                            int width = bitMatrix.getWidth();
                            int height = bitMatrix.getHeight();
                            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                            for (int x = 0; x < width; x++) {
                                for (int y = 0; y < height; y++) {
                                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                }
                            }
                            iv.setImageBitmap(bmp);
                            display.setText("Transfer " + etqr.getText().toString() + "BNU to " + txt_address.getText() + "\n payid: " + payid);

                        } catch (WriterException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, 500);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        String address_string = this.getWallet("address");
        String adr = address_string.substring(0,11)+"..."+address_string.substring(address_string.length() - 11);
        txt_address.setText(adr);
    }

    String getWallet(String key) {
        SharedPreferences wallpref = getSharedPreferences("MY_KEYS", MODE_PRIVATE);
        String prefbalance = wallpref.getString(key, null);
        return prefbalance;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String cryptoHash() throws NoSuchAlgorithmException {
        MessageDigest md = null;
        md = MessageDigest.getInstance("SHA-256");
        int min = 100000;
        int max = 999999;
        int random = new Random().nextInt((max - min) + 1) + min;
        String text = Integer.toString(random);
        // Change this to UTF-16 if needed
        md.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        String hex = String.format("%064x", new BigInteger(1, digest));
        System.out.println(hex);
        return hex;
    }


}
