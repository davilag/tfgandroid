package es.davilag.passtochrome.requests_content;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import es.davilag.passtochrome.R;
import es.davilag.passtochrome.database.Request;

/**
 * Created by davilag on 18/08/14.
 */
public class RecyclerRequestAdapter extends RecyclerView.Adapter<RecyclerRequestAdapter.ViewHolderRequests> implements View.OnClickListener{

    private ArrayList<Request> mDataset;
    private static Context sContext;

    // Adapter's Constructor
    public RecyclerRequestAdapter(Context context, ArrayList<Request> myDataset) {
        mDataset = myDataset;
        sContext = context;
    }

    // Create new views. This is invoked by the layout manager.
    @Override
    public ViewHolderRequests onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // Create a new view by inflating the row item xml.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_request_row, parent, false);

        // Set the view to the ViewHolder
        ViewHolderRequests holder = new ViewHolderRequests(v);
        holder.row.setOnClickListener(RecyclerRequestAdapter.this);
        holder.row.setTag(holder);
        return holder;
    }

    // Replace the contents of a view. This is invoked by the layout manager.
    @Override
    public void onBindViewHolder(ViewHolderRequests holder, int position) {

        // Get element from your dataset at this position and set the text for the specified element
        holder.mNameTextView.setText(mDataset.get(position).getDom());
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
        ViewHolderRequests holder = (ViewHolderRequests) view.getTag();
        if (view.getId() == holder.row.getId()) {
            Toast.makeText(sContext, holder.mNameTextView.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    // Create the ViewHolder class to keep references to your views
    public static class ViewHolderRequests extends RecyclerView.ViewHolder {
        public CardView row;
        public TextView mNameTextView;
        public ImageButton responseButton;
        public ImageButton cancelButton;

        /**
         * Constructor
         * @param v The container view which holds the elements from the row item xml
         */
        public ViewHolderRequests(View v) {
            super(v);
            row = (CardView) v.findViewById(R.id.card_view);
            mNameTextView = (TextView) v.findViewById(R.id.list_requests_dominio);
            responseButton = (ImageButton) v.findViewById(R.id.list_request_response);
            cancelButton = (ImageButton) v.findViewById(R.id.list_request_cancel);
        }
    }
    /*
    public void addItem(Request content){
        int position = mDataset.size();
        mDataset.add(position, content);
        notifyItemInserted(position);
    }
    */
}
