package rapid.responce.RUCU;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> items;
    private List<Item> filteredItems;

    public ItemAdapter(List<Item> items) {
        this.items = items;
        this.filteredItems = new ArrayList<>(items);
    }

    public void setItems(List<Item> items) {
        this.items = items;
        this.filteredItems = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        filteredItems.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTextView.setText(filteredItems.get(position).getName());
        holder.nameDescription.setText(filteredItems.get(position).getGarageDescription());

    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView nameDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            nameDescription=itemView.findViewById(R.id.nameTextView2);

        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Item> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(items);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Item item : items) {
                        if (item.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems.clear();
                filteredItems.addAll((List<Item>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
