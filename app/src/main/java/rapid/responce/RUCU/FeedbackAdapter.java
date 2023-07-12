package rapid.responce.RUCU;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    private List<Approval> requests;
    private OnItemClickListener onItemClickListener;

    public FeedbackAdapter(List<Approval> requests, OnItemClickListener onItemClickListener) {
        this.requests = requests;
        this.onItemClickListener = onItemClickListener;
    }

    public void setRequests(List<Approval> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    public void clear() {
        requests.clear();
        notifyDataSetChanged();
    }

    public Approval getItem(int position) {
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
        Approval approval = requests.get(position);
        String districtRegion = approval.getGarageName() + "\nSTATUS: " + approval.getStatus();
        holder.nameTextView.setText(districtRegion);
        holder.nameDescription.setText(approval.getTimeStamp().toString());

        // Set the click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(approval);
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
        void onItemClick(Approval request);
    }
}
