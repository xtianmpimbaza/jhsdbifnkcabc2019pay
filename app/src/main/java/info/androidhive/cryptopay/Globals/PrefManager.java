package info.androidhive.cryptopay.Globals;


import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "binusu-welcome";

    private static final String IS_NOT_LOGGED_IN = "IsNotLoggedIn";

    private static final String USER_ID = "userid";

    private static final String CONTACT = "contact";

    private static final String PIN = "pin";

    private static final String IS_SYNCED = "IsSynced";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsSynced(boolean isSynced) {
        editor.putBoolean(IS_SYNCED, isSynced);
        editor.commit();
    }

    public boolean isSynced() {
        return pref.getBoolean(IS_SYNCED, false);
    }

    public void setIsNotLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(IS_NOT_LOGGED_IN, isLoggedIn);
        editor.commit();
    }

    public boolean isNotLoggedIn() {
        return pref.getBoolean(IS_NOT_LOGGED_IN, true);
    }


    public String getContact() {
        return pref.getString(CONTACT, "");
    }

    public void setPin(String pin) {
        editor.putString(PIN, pin);
        editor.commit();
    }

    public String getPin() {
        return pref.getString(PIN, null);
    }

    public void setUserId(String userId) {
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(USER_ID, "");
    }

}
