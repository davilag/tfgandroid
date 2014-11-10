package es.davilag.passtochrome.requests_content;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.davilag.passtochrome.R;

/**
 * Created by davilag on 5/11/14.
 */
public class ListRequestsAdapter extends ArrayAdapter<RequestItem> {

    Context context;
    List<RequestItem> drawerItemList;
    int layoutResID;

    public ListRequestsAdapter(Context context, int layoutResourceID,
                             List<RequestItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ListRequestsHolder listRequestItemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listRequestItemHolder = new ListRequestsHolder();

            view = inflater.inflate(layoutResID, parent, false);
            listRequestItemHolder.ItemName = (TextView) view
                    .findViewById(R.id.list_requests_dominio);
            listRequestItemHolder.buttonResponse = (ImageView) view.findViewById(R.id.list_request_response);
            listRequestItemHolder.buttonCancel = (ImageView) view.findViewById(R.id.list_request_cancel);

            view.setTag(listRequestItemHolder);

        } else {
            listRequestItemHolder = (ListRequestsHolder) view.getTag();

        }

        RequestItem dItem = this.drawerItemList.get(position);

        listRequestItemHolder.ItemName.setText(dItem.getDominio());
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        listRequestItemHolder.ItemName.setTypeface(tf);
        listRequestItemHolder.buttonResponse.setOnClickListener(new ResponseClickListener(context,dItem.getReqId()));
        listRequestItemHolder.buttonCancel.setOnClickListener(new CancelClickListener(context,dItem.getReqId()));

        return view;
    }
    private static class ListRequestsHolder {
        TextView ItemName;
        ImageView buttonResponse;
        ImageView buttonCancel;
    }
}