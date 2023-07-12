package rapid.responce.RUCU;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<Request> requests;
    private OnItemClickListener onItemClickListener;

    public RequestAdapter(List<Request> requests, OnItemClickListener onItemClickListener) {
        this.requests = requests;
        this.onItemClickListener = onItemClickListener;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    public void clear() {
        requests.clear();
        notifyDataSetChanged();
    }

    public Request getItem(int position) {
        return requests.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requests.get(position);
        holder.nameTextView.setText(request.getUsername());
        holder.nameDescription.setText(request.getTimestamp().toString());

        // Set the click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(request);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView nameDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            nameDescription = itemView.findViewById(R.id.nameTextView2);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Request request);
    }
}
