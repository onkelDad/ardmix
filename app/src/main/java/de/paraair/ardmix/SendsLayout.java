package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;

import static android.widget.CompoundButton.*;

/**
 * Created by onkel on 04.11.16.
 */

public class SendsLayout extends LinearLayout implements OnClickListener {

    public static final int MSG_WHAT_SEND_CHANGED = 98;
    public static final int MSG_WHAT_SEND_ENABLED = 99;
    public static final int MSG_WHAT_RESET_LAYOUT = 199;
    private Context context;
    private Object[] sargs;

    private TextView sendsDescription;
    private int stripIndex;

    private Handler onChangeHandler;

    public SendsLayout(Context context) {
        super(context);
        this.context = context;
        this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(0x3000FFFF);
        this.setPadding(1, 0, 1, 0);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Message fm = onChangeHandler.obtainMessage(MSG_WHAT_RESET_LAYOUT);
        onChangeHandler.sendMessage(fm);
    }

    public void init(StripLayout strip, Object[] sargs) {
        this.sargs = sargs;
        stripIndex = strip.getId();

        sendsDescription = new TextView(context);
        sendsDescription.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        sendsDescription.setTextSize(18);
        sendsDescription.setPadding(4,4,4,4);
        sendsDescription.setTextColor(Color.WHITE);
        sendsDescription.setTag("pluginTitle");
        sendsDescription.setOnClickListener(this);
        sendsDescription.setText("Sends of " + strip.getTrack().name);
        addView(sendsDescription);

        for (int i = 0; i < sargs.length; i += 5) {
            LinearLayout sLayout = new LinearLayout(context);
            sLayout.setOrientation(HORIZONTAL);
            sLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            CheckBox sendName = new CheckBox(context);
            sendName.setLayoutParams(new LayoutParams(160, 32));
            sendName.setText((String) sargs[i+1]);
            sendName.setChecked((int)sargs[i+4] > 0);
            sendName.setTextColor(Color.WHITE);
            sendName.setId((int)sargs[i+2]);
            sendName.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Message fm = onChangeHandler.obtainMessage(MSG_WHAT_SEND_ENABLED, stripIndex, buttonView.getId(), isChecked ? 1 : 0);
                    onChangeHandler.sendMessage(fm);
                }
            });
            sLayout.addView(sendName);

            FaderView parameterValue = new FaderView(context);
            parameterValue.setLayoutParams(new LayoutParams(240, 32));
            parameterValue.setMax(1000);
            parameterValue.setOrientation(FaderView.Orientation.HORIZONTAL);
            parameterValue.setId((int)sargs[i+2]);
            parameterValue.setProgress((int)((float)sargs[i + 3] * 1000));
            parameterValue.setOnChangeHandler(mHandler);
            sLayout.addView(parameterValue);

            addView(sLayout);
        }

        Button btnClose = new Button(context);
        btnClose.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 26));
        btnClose.setPadding(1, 0, 1, 0);
        btnClose.setText("Close");
        btnClose.setOnClickListener(this);
        addView(btnClose);

    }

    private Handler mHandler = new Handler() {

        /* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    break;
                case 20:
                    int pi = msg.arg1;
                    Message fm = onChangeHandler.obtainMessage(MSG_WHAT_SEND_CHANGED, stripIndex, pi, msg.arg2);
                    onChangeHandler.sendMessage(fm);
                    break;
                case 30:
                    break;
            }
        }
    };

    public void setOnChangeHandler(Handler onChangeHandler) {
        this.onChangeHandler = onChangeHandler;
    }
}
