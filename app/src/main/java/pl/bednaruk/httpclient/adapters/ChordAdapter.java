package pl.bednaruk.httpclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import pl.bednaruk.httpclient.R;
import pl.bednaruk.httpclient.models.ChordApp;

public class ChordAdapter extends RecyclerView.Adapter<ChordAdapter.ViewHolder>  {
    private final List<ChordApp> chordList;
    Context context;

    public ChordAdapter(Context context, List<ChordApp> chordList) {
        this.chordList = chordList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivChord;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChord = itemView.findViewById(R.id.ivChord);
        }
    }

    @NonNull
    @Override
    public ChordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chord, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ChordAdapter.ViewHolder viewHolder, int position) {
        viewHolder.itemView.setTag(chordList.get(position));
       Picasso.get().load(chordList.get(position).getImageResources()).into(viewHolder.ivChord);
        //viewHolder.ivChord.setImageResource(R.drawable.ic_launcher_background);
    }
    @Override
    public int getItemCount() {
        return chordList.size();
    }
}
