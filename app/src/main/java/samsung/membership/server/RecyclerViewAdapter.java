package samsung.membership.server;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumin on 2017-08-05.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(dataSet.get(position).text);
        holder.imageView.setImageResource(dataSet.get(position).img);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    private ArrayList<MyData> dataSet;

    // Provide a reference to the views for each data item`
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public TextView textView;

        ViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            textView = (TextView)view.findViewById(R.id.textview);
        }
    }

    public RecyclerViewAdapter(ArrayList<MyData> myDataset) {
        dataSet = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

class MyData{
    public String text;
    public int img;
    public MyData(String text, int img){
        this.text = text;
        this.img = img;
    }
}