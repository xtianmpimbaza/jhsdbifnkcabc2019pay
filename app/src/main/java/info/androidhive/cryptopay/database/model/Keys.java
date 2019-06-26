package info.androidhive.cryptopay.database.model;

/**
 * Created by ravi on 20/02/18.
 */

public class Keys {
    public static final String TABLE_NAME = "keyring";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PUBLIC = "publickey";
    public static final String COLUMN_PRIVATE = "privatekey";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String publickey;
    private String privatekey;
    private String timestamp;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PUBLIC + " TEXT,"
                    + COLUMN_PRIVATE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Keys() {
    }

    public Keys(int id, String publickey, String privatekey, String timestamp) {
        this.id = id;
        this.publickey = publickey;
        this.privatekey = privatekey;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getPublickey() {
        return publickey;
    }

    public String getPrivatekey() {
        return privatekey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
