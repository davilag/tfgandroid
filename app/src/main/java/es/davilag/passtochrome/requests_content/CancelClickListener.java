package es.davilag.passtochrome.requests_content;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import es.davilag.passtochrome.CancelService;
import es.davilag.passtochrome.Globals;

/**
 * Created by davilag on 6/11/14.
 */
public class CancelClickListener implements View.OnClickListener {

    private Context context;
    private String reqId;
    public CancelClickListener(Context activity, String reqId){
        this.context = activity;
        this.reqId = reqId;
    }
    @Override
    public void onClick(View v) {
        Intent i = new Intent(context, CancelService.class);
        i.putExtra(Globals.INTENT_REQ_ID,reqId);
        context.startService(i);
    }
}
