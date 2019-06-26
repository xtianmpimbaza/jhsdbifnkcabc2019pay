package info.androidhive.cryptopay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import info.androidhive.cryptopay.database.model.Note;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private Context context;
    private List<Note> notesList;
//    GlobalFuns gfuns;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView note;
        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.note);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = notesList.get(position);
        DecimalFormat formatter = new DecimalFormat("#,###,###.##");
        holder.note.setText(String.format("%,.2f", Double.parseDouble(note.getNote())));
        holder.dot.setText(Html.fromHtml("&#8226;"));
//        Log.e("String_tx",note.getNote());
        Log.e("String_confermations",note.getConfirmations());
        if (Double.parseDouble(note.getConfirmations()) < 1 && Double.parseDouble(note.getNote()) > 0){
            holder.dot.setTextColor(Color.parseColor("#ff6600"));
        }else if(Double.parseDouble(note.getNote()) < 0 ){
            holder.dot.setTextColor(Color.parseColor("#D81B60"));
        }else {
            holder.dot.setTextColor(Color.parseColor("#ff008000"));
        }
        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(note.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Date error", ""+e.getMessage());
        }
        return "";
    }

    private String formatNumber(String numeStr) {
        DecimalFormat formatter = new DecimalFormat("#,###,###.##");
//        DecimalFormat formater = new DecimalFormat("#.00");
        return formatter.format(numeStr);
    }

}
