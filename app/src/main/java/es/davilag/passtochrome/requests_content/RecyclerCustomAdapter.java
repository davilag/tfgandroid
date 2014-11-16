package es.davilag.passtochrome.requests_content;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import es.davilag.passtochrome.R;

/**
 * Created by davilag on 18/08/14.
 */
public class RecyclerCustomAdapter extends RecyclerView.Adapter<RecyclerCustomAdapter.ViewHolder> implements View.OnClickListener,
        View.OnLongClickListener{

    private ArrayList<RequestItem> mDataset;
    private static Context sContext;

    // Adapter's Constructor
    public RecyclerCustomAdapter(Context context, ArrayList<RequestItem> myDataset) {
        mDataset = myDataset;
        sContext = context;
    }

    // Create new views. This is invoked by the layout manager.
    @Override
    public RecyclerCustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // Create a new view by inflating the row item xml.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_request_row, parent, false);

        // Set the view to the ViewHolder
        ViewHolder holder = new ViewHolder(v);
        holder.row.setOnClickListener(RecyclerCustomAdapter.this);
        holder.row.setOnLongClickListener(RecyclerCustomAdapter.this);

        holder.row.setTag(holder);
        return holder;
    }

    // Replace the contents of a view. This is invoked by the layout manager.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get element from your dataset at this position and set the text for the specified element
        holder.mNameTextView.setText(mDataset.get(position).getDominio());
        holder.responseButton.setOnClickListener(new ResponseClickListener(sContext,mDataset.get(position).getReqId()));
        holder.cancelButton.setOnClickListener(new CancelClickListener(sContext,mDataset.get(position).getReqId()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Implement OnClick listener. The clicked item text is displayed in a Toast message.
    @Override
    public void onClick(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (view.getId() == holder.row.getId()) {
            Toast.makeText(sContext, holder.mNameTextView.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    // Implement OnLongClick listener. Long Clicked items is removed from list.
    @Override
    public boolean onLongClick(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        Log.v("Adapter",holder.row.getId()+"");
        Log.v("Adapter",view.getId()+"");
        if (view.getId() == holder.row.getId()) {
            mDataset.remove(holder.getPosition());

            // Call this method to refresh the list and display the "updated" list
            notifyDataSetChanged();

            Toast.makeText(sContext, "Item " + holder.mNameTextView.getText() + " has been removed from list",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    // Create the ViewHolder class to keep references to your views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView row;
        public TextView mNameTextView;
        public ImageButton responseButton;
        public ImageButton cancelButton;

        /**
         * Constructor
         * @param v The container view which holds the elements from the row item xml
         */
        public ViewHolder(View v) {
            super(v);
            row = (CardView) v.findViewById(R.id.card_view);
            mNameTextView = (TextView) v.findViewById(R.id.list_requests_dominio);
            responseButton = (ImageButton) v.findViewById(R.id.list_request_response);
            cancelButton = (ImageButton) v.findViewById(R.id.list_request_cancel);
        }
    }

    public void addItem( RequestItem content){
        int position = mDataset.size();
        mDataset.add(position, content);
        notifyItemInserted(position);
    }
}
