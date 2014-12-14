package es.davilag.passtochrome.container_content;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import es.davilag.passtochrome.R;
import es.davilag.passtochrome.database.FilaContenedor;

/**
 * Created by davilag on 5/12/14.
 */
public class RecyclerContainerAdapter extends RecyclerView.Adapter<RecyclerContainerAdapter.ViewHolderContainer> implements View.OnClickListener {

    private Context c;
    private ArrayList<FilaContenedor> dataset;

    public RecyclerContainerAdapter (Context c, ArrayList<FilaContenedor> dataset){
        this.c = c;
        this.dataset = dataset;
    }

    @Override
    public RecyclerContainerAdapter.ViewHolderContainer onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_container_row,parent,false);
        ViewHolderContainer holder = new ViewHolderContainer(v);
        holder.row.setOnClickListener(RecyclerContainerAdapter.this);
        holder.row.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderContainer viewHolderContainer, int i) {
        viewHolderContainer.dom.setText(dataset.get(i).getDom());
        viewHolderContainer.user.setText(dataset.get(i).getUsuario());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public void onClick(View v) {

    }

    public static class ViewHolderContainer extends RecyclerView.ViewHolder{
        public CardView row;
        public TextView dom;
        public TextView user;
        public ViewHolderContainer(View itemView) {
            super(itemView);
            row = (CardView) itemView.findViewById(R.id.card_view);
            dom = (TextView) itemView.findViewById(R.id.list_container_dominio);
            user = (TextView) itemView.findViewById(R.id.list_container_user);
            Typeface tf = Typeface.createFromAsset(row.getContext().getAssets(), "Roboto-Bold.ttf");
            dom.setTypeface(tf);
            tf = Typeface.createFromAsset(row.getContext().getAssets(), "Roboto-ThinItalic.ttf");
            user.setTypeface(tf);

        }
    }
}
