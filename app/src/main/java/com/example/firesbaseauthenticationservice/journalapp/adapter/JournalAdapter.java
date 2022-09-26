package com.example.firesbaseauthenticationservice.journalapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firesbaseauthenticationservice.journalapp.R;
import com.example.firesbaseauthenticationservice.journalapp.model.Journal;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private Context context;
    private final ArrayList<Journal> journalList;

    //Constructor
    public JournalAdapter(Context context, ArrayList<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }



    //Creating JournalViewHolder class
    public class JournalViewHolder extends RecyclerView.ViewHolder{

        public TextView title, thoughts, dateAdded, name, date;
        public ImageView image;
        public ImageView shareBTN;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.journal_title_list);
            thoughts = itemView.findViewById(R.id.journal_thoughts_list);
            dateAdded = itemView.findViewById(R.id.journal_timestamp_list);
            name = itemView.findViewById(R.id.journal_row_username);
            image = itemView.findViewById(R.id.journal_image_list);
            shareBTN = itemView.findViewById(R.id.journal_share_button);
            date = itemView.findViewById(R.id.journal_date_list);

        }
    }

    //Overridden methods
    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.journal_row, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {

        Journal journal = journalList.get(position);

        holder.title.setText(journal.getTitle());
        holder.thoughts.setText(journal.getThoughts());
        holder.name.setText(journal.getUserName());
        holder.date.setText(journal.getTimeAdded().toDate().toString());
        String imageUrl;
        imageUrl = journal.getImageUrl();

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(
                journal.getTimeAdded()
                        .getSeconds()*1000);
        holder.dateAdded.setText(timeAgo);

        //Glide to set the image
        Glide.with(context)
                .load(imageUrl)
                .into(holder.image);

        holder.shareBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Journal tempJournal = journalList.get(holder.getAdapterPosition());
                BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.image.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                Uri uri = getImageToShare(context, bitmap);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, tempJournal.getTitle());
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(Intent.createChooser(intent, "choose one"));
            }
        });

    }

    //This method will convert
    private Uri getImageToShare(Context inContext, Bitmap bitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public int getItemCount() {
        return journalList != null ? journalList.size() : 0;
    }
}
