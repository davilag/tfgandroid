package es.davilag.passtochrome.requests_content;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import es.davilag.passtochrome.Globals;
import es.davilag.passtochrome.ResponseService;

/**
 * Created by davilag on 6/11/14.
 */
public class ResponseClickListener implements View.OnClickListener {

    private Context context;
    private String reqId;
    public ResponseClickListener(Context activity, String reqId){
        this.context = activity;
        this.reqId = reqId;
    }
    @Override
    public void onClick(View v) {
        Intent i = new Intent(context, ResponseService.class);
        i.putExtra(Globals.INTENT_REQ_ID,reqId);
        context.startService(i);
    }
}
