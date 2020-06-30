package ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javafirstfirebase.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Journal;

public class JournalRecylerAdapter extends RecyclerView.Adapter<JournalRecylerAdapter.ViewHolder> {

    private Context context;
    private List<Journal>journalList;

    public JournalRecylerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecylerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.journal_row,parent,false);

        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecylerAdapter.ViewHolder holder, int position) {

        Journal journal=journalList.get(position);
        String  imageurl;

        holder.title.setText(journal.getTitle());
        holder.thoughts.setText(journal.getThought());
        holder.name.setText(journal.getUserName());

        //set image by picasoo library

        imageurl=journal.getImageUrl();

        String timeAgo= (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds()*1000);

        holder.dateAdded.setText(timeAgo);
        Picasso.get().load(imageurl).placeholder(R.drawable.image_one).fit().into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title,thoughts,dateAdded,name;
        public ImageView imageView;
        String userId;
        String username;
        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context=ctx;
            title=itemView.findViewById(R.id.journal_title_list);
            thoughts=itemView.findViewById(R.id.journal_thought_list);
            dateAdded=itemView.findViewById(R.id.journal_timestamp);
            imageView=itemView.findViewById(R.id.journal_image_list);
            name=itemView.findViewById(R.id.journal_row_username);
        }
    }
}
