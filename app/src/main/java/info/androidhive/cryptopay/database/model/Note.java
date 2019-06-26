package info.androidhive.cryptopay.database.model;

public class Note {
    public static final String TABLE_NAME = "notes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TXID = "txid";
    public static final String COLUMN_FEE = "fee";
    public static final String COLUMN_BLOBKINDEX = "blockindex";
    public static final String COLUMN_CONFIRMATIONS = "confirmations";
    public static final String COLUMN_SENDER = "sender";
    public static final String COLUMN_RECEIPIENT = "receipient";
    public static final String COLUMN_PAYMENTID = "payid";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TIMESTAMP = "time";

    private int id;
    private String note;
    private String timestamp;
    private String fee;
    private String confirmations;
    private String txid;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_TXID + " TEXT,"
                    + COLUMN_FEE + " TEXT,"
                    + COLUMN_BLOBKINDEX + " TEXT,"
                    + COLUMN_CONFIRMATIONS + " TEXT,"
                    + COLUMN_SENDER + " TEXT,"
                    + COLUMN_RECEIPIENT + " TEXT,"
                    + COLUMN_PAYMENTID + " VARCHAR(100) PRIMARY KEY,"
                    + COLUMN_AMOUNT + " TEXT,"
                    + COLUMN_TIMESTAMP + " TEXT"
                    + ")";

    public Note() {
    }

    public Note(int id, String note, String timestamp, String fee, String confirmations, String txid) {
        this.id = id;
        this.note = note;
        this.timestamp = timestamp;
        this.fee = fee;
        this.confirmations = confirmations;
        this.txid = txid;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public String getTxid() {
        return txid;
    }

    public String getFee() {
        return fee;
    }

    public String getConfirmations() {
        return confirmations;
    }

    public void setNote(String note) {
        this.note = note;
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

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }

}
