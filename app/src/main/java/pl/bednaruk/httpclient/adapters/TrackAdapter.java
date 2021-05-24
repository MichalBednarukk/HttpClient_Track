package pl.bednaruk.httpclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pl.bednaruk.httpclient.R;
import pl.bednaruk.httpclient.models.ChordApp;
import pl.bednaruk.httpclient.models.Track;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    private final List<Track> trackList;
    Context context;

    public interface ItemClicked {
        void OnItemClicked(int id);
    }
    public TrackAdapter(Context context, List<Track> trackList) {
        this.trackList = trackList;
        this.context = context;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvChord, tvAuthor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvChord = itemView.findViewById(R.id.tvChord);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
        }
    }
    @NonNull
    @Override
    public TrackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_track, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TrackAdapter.ViewHolder viewHolder, int position) {
        viewHolder.itemView.setTag(trackList.get(position));
        TrackAdapter.ItemClicked itemClicked = (TrackAdapter.ItemClicked) context;
        StringBuilder chordValue = new StringBuilder();
        viewHolder.tvName.setText(String.valueOf(trackList.get(position).getName()));
        viewHolder.tvAuthor.setText(String.valueOf(trackList.get(position).getAuthor()));

        for (ChordApp chordApp : trackList.get(position).getChordApps()) {
            chordValue.append(chordApp.getChordName()).append(", ");
        }
        viewHolder.tvChord.setText(chordValue);
        viewHolder.itemView.setOnClickListener(v -> itemClicked.OnItemClicked(trackList.get(position).getId()));
    }
    @Override
    public int getItemCount() {
        return trackList.size();
    }
}
