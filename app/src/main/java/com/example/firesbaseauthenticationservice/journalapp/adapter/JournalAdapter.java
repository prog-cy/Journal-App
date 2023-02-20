package com.example.firesbaseauthenticationservice.journalapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firesbaseauthenticationservice.journalapp.JournalListActivity;
import com.example.firesbaseauthenticationservice.journalapp.MainActivity;
import com.example.firesbaseauthenticationservice.journalapp.R;
import com.example.firesbaseauthenticationservice.journalapp.model.Journal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private Context context;
    private final ArrayList<Journal> journalList;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = firestore.collection("Journal");

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

        //deleting data when item is pressed long
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = holder.getAdapterPosition();
                Toast.makeText(context, "Deleted "+pos, Toast.LENGTH_SHORT).show();
                Journal journal1 = journalList.get(pos);
                String imageUrl = journal1.getImageUrl();
                collectionReference
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                for(QueryDocumentSnapshot snapshot : task.getResult()){
                                    String str = snapshot.getId();
                                    DocumentReference documentReference = collectionReference.document(str);
                                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            String imgURL = documentSnapshot.getString("imageUrl");
                                            if(imageUrl.equals(imgURL)){
                                                documentReference.delete();
                                                Intent intent = new Intent(context, JournalListActivity.class);
                                                context.startActivity(intent);
                                            }

                                        }
                                    });
                                }

                            }
                        });
                return true;
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
