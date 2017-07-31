package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by onkel on 21.10.16.
 */

public class PluginLayout extends LinearLayout implements View.OnClickListener  {

    public static final int PLUGIN_PARAMETER_CHANGED = 28;
    public static final int PLUGIN_RESET = 29;
    public static final int PLUGIN_DESCRIPTOR_REQUEST = 22;
    public static final int PLUGIN_NEXT = 31;
    public static final int PLUGIN_PREV = 32;
    public static final int PLUGIN_BYPASS = 35;
    private static final int NAVBUTTON_HEIGHT = 36;

    private static final int PARAMETER_HEIGHT = 36;

    private ToggleTextButton ttbBypass;

    private final Context context;

    Track track;

    private Handler onChangeHandler;
    private TextView pluginDescription;
    private Button resetPlugin;
    private ArdourPlugin currentPlugin;
    private ScrollView scrollView;

    private boolean bInitEnabled = true;

    public PluginLayout(Context context) {
        super(context);
        this.context = context;
    }

    public void initLayout(boolean inlude_request, Track t) {

        if( track != null && track.remoteId == t.remoteId)
            return;
        removeAllViews();
        this.track = t;
        pluginDescription = new TextView(context);
        pluginDescription.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        pluginDescription.setTextSize(18);
        pluginDescription.setPadding(4,4,4,4);
        pluginDescription.setTextColor(Color.WHITE);
        pluginDescription.setTag("pluginTitle");
        if( track.pluginDescriptors.size() > 0 ) {
            if (track.pluginDescriptors.size() > 1)
                pluginDescription.setOnClickListener(this);
        }
        else
            pluginDescription.setText(R.string.lb_Descr_noFX);
        addView(pluginDescription);

        LinearLayout btnLayout = new LinearLayout(context);
        btnLayout.setOrientation(HORIZONTAL);
        btnLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        btnLayout.setPadding(0,0,0,16);

        Button btnClose = new Button(context);
        LayoutParams bclp = new LayoutParams(LayoutParams.WRAP_CONTENT, NAVBUTTON_HEIGHT);
        bclp.setMargins(0,0,0,0);
        btnClose.setLayoutParams(bclp);
        btnClose.setPadding(1, 0, 1, 0);
        btnClose.setTag("close");
        btnClose.setText(R.string.btn_close);
        btnClose.setOnClickListener(this);
        btnLayout.addView(btnClose);

        if( t.pluginDescriptors.size() > 0 ) {
            resetPlugin = new Button(context);
            resetPlugin.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, NAVBUTTON_HEIGHT));
            resetPlugin.setText(R.string.btn_reset);
            resetPlugin.setPadding(1, 0, 1, 0);
            resetPlugin.setTag("resetPlugin");
            resetPlugin.setOnClickListener(this);
            btnLayout.addView(resetPlugin);

            ttbBypass = new ToggleTextButton(context);
            LayoutParams bblp = new LayoutParams(LayoutParams.WRAP_CONTENT, NAVBUTTON_HEIGHT);
            bblp.setMargins(2,0,24,0);
            ttbBypass.setPadding(1,1,1,1);
            ttbBypass.setLayoutParams(bblp);
            ttbBypass.setPadding(1, 0, 1, 0);
            ttbBypass.setTag("bypass");
            ttbBypass.setAllText("BYPASS");
            ttbBypass.setOnClickListener(this);
            ttbBypass.onColor = getResources().getColor(R.color.BUTTON_FX, null);
            btnLayout.addView(ttbBypass);
            ttbBypass.setAutoToggle(true);

        }

        if( track.type != Track.TrackType.MASTER) {
            Button btnPrev = new Button(context);
            btnPrev.setLayoutParams(new LayoutParams(48, NAVBUTTON_HEIGHT));
            btnPrev.setPadding(1, 0, 1, 0);
            btnPrev.setTag("prev");
            btnPrev.setText("<");
            btnPrev.setOnClickListener(this);
            btnLayout.addView(btnPrev);

            Button btnNext = new Button(context);
            btnNext.setLayoutParams(new LayoutParams(48, NAVBUTTON_HEIGHT));
            btnNext.setPadding(1, 0, 1, 0);
            btnNext.setTag("next");
            btnNext.setText(">");
            btnNext.setOnClickListener(this);
            btnLayout.addView(btnNext);
        }
        addView(btnLayout);

        scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        scrollView.setFillViewport(true);
        addView(scrollView);

        if( inlude_request ) {
            Message fm = onChangeHandler.obtainMessage(PLUGIN_DESCRIPTOR_REQUEST, track.remoteId, 1);
            onChangeHandler.sendMessage(fm);
        }

    }

    public void init(int pluginId) {
        if( !bInitEnabled )
            return;
        if( currentPlugin != null && this.currentPlugin.getPluginId()  == pluginId)
            return;
        this.currentPlugin = track.getPluginDescriptor(pluginId);
        setTag(currentPlugin);
        pluginDescription.setText("(" + (currentPlugin.getPluginId() ) + "/" + track.pluginDescriptors.size() + ") - " + currentPlugin.getName() + " - " + track.name);
        resetPlugin.setId(currentPlugin.getPluginId());
        ttbBypass.setToggleState(!currentPlugin.enabled);
//        int pi = 0;

        scrollView.removeAllViews();

        LinearLayout scroller = new LinearLayout(context);
        scroller.setOrientation(VERTICAL);
        scroller.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        scrollView.addView(scroller);


        for(int index: currentPlugin.getParameters().keySet()) {
            ArdourPlugin.InputParameter parameter = currentPlugin.getParameters().get(index);
            if( (parameter.flags & 0x80) == 0x80 && (parameter.flags & 0x100) != 0x100) {

                Log.d("PLUGIN", "parameter " + parameter.name + " flags: " + String.format("%x", parameter.flags) +
                        " u/l " + String.format("%.2f", parameter.min) + "/" + String.format("%.2f", parameter.max));


                LinearLayout pLayout = new LinearLayout(context);
                pLayout.setOrientation(HORIZONTAL);
                pLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, PARAMETER_HEIGHT));

                TextView parameterName = new TextView(context);
                parameterName.setLayoutParams(new LayoutParams(160, PARAMETER_HEIGHT));
                parameterName.setText(parameter.name);
                parameterName.setTextColor(Color.WHITE);

                pLayout.addView(parameterName);

                if( parameter.scaleSize == 0 ) {
                    if( (parameter.flags & 64) == 64 ) {
                        CheckBox parameterValue = new CheckBox(context);
                        parameterValue.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        parameterValue.setChecked(parameter.current != 0);
                        parameterValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                Message msg = mHandler.obtainMessage(40, (int)buttonView.getTag(), 0, (float)(isChecked ? 1 : 0) );
                                mHandler.sendMessage(msg);
                            }
                        });
                        parameterValue.setTag(parameter.parameter_index);
                        pLayout.addView(parameterValue);
                    }
                    else {
                        FaderView parameterValue = new FaderView(context);
                        parameterValue.param = parameter;

                        parameterValue.setLayoutParams(new LayoutParams(240, PARAMETER_HEIGHT));
                        parameterValue.setOrientation(FaderView.Orientation.HORIZONTAL);
                        parameterValue.setId(parameter.parameter_index);
                        if( (parameter.flags & 0x02) == 0x02) {
                            parameterValue.setMax((int)parameter.max);
                            parameterValue.setMin((int)parameter.min);
                            parameterValue.setProgress((int)parameter.current);
                        }
                        else {
                            parameterValue.setMax(1000);
                            parameterValue.setProgress(parameter.getFaderFromCurrent(1000));
                        }
                        parameterValue.SetListener(parameterHandler);
                        pLayout.addView(parameterValue);
                    }
                }
                else {
                    final MyAdapter aa = new MyAdapter(parameter.scale_points);
                    Spinner parameterValue = new Spinner(context);
                    parameterValue.setLayoutParams(new LayoutParams(240, PARAMETER_HEIGHT));
                    parameterValue.setAdapter(aa);
                    parameterValue.setPopupBackgroundResource(R.color.VeryDark);
                    parameterValue.setSelection(parameter.getIndexFromScalePointKey((int)parameter.current) );
                    parameterValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Map.Entry<Float, String> e = aa.getItem(position);
                            Message msg = mHandler.obtainMessage(40, (int)parent.getTag(), 0, e.getKey());
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }

                    });
                    parameterValue.setTag(parameter.parameter_index);
                    pLayout.addView(parameterValue);
                }
                scroller.addView(pLayout);

            }
            else {
                if( (parameter.flags & 0x80) != 0x80 )
                    Log.d("Ardmix", "output parameter found: " + parameter.name);
            }
//            pi++;
        }
        bInitEnabled = false;

    }

    private final FaderView.FaderViewListener parameterHandler = new FaderView.FaderViewListener() {

        @Override
        public void onFader(int id, int pos) {
            ArdourPlugin.InputParameter ip = currentPlugin.getParameter(id);
            if( (ip.flags & 0x02) == 0x02) {
                ip.current = (double)pos;
            }
            else
                ip.setCurrentFromFader(pos, 1000);
            Object[] plargs = new Object[2];
            plargs[0] = currentPlugin.getParameter(id).parameter_index;
            plargs[1] = ip.current;
            Message fm = onChangeHandler.obtainMessage(PLUGIN_PARAMETER_CHANGED, currentPlugin.getTrackId(), currentPlugin.getPluginId(), plargs);
            onChangeHandler.sendMessage(fm );
        }

        @Override
        public void onStartFade() {

        }

        @Override
        public void onStopFade(int id, int pos) {

        }
    };

    public class MyAdapter extends BaseAdapter {
        private final ArrayList mData;

        public MyAdapter(Map<Float, String> map) {
            mData = new ArrayList();
            mData.addAll(map.entrySet());
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Map.Entry<Float, String> getItem(int position) {
            return (Map.Entry) mData.get(position);
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View result;

            if (convertView == null) {
                result = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item, parent, false);
            } else {
                result = convertView;
            }

            Map.Entry<Float, String> item = getItem(position);
            TextView tw = (TextView) result.findViewById(R.id.itemText);
            tw.setText(item.getValue());
            tw.setTag(item.getValue());

            return result;
        }
    }

    private final Handler mHandler = new Handler() {

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
                    ArdourPlugin.InputParameter ip = currentPlugin.getParameter(pi);
                    if( (ip.flags & 0x02) == 0x02) {
                        ip.current = (double)msg.arg2;
                    }
                    else
                        ip.setCurrentFromFader(msg.arg2, 1000);
                    Object[] plargs = new Object[2];
                    plargs[0] = currentPlugin.getParameter(pi).parameter_index;
                    plargs[1] = ip.current;
                    Message fm = onChangeHandler.obtainMessage(PLUGIN_PARAMETER_CHANGED, currentPlugin.getTrackId(), currentPlugin.getPluginId(), plargs);
                    onChangeHandler.sendMessage(fm );
                    break;
                case 30:
//                    int pir = msg.arg1;
//                    ArdourPlugin.InputParameter ipr = currentPlugin.getParameter(pir);
                    break;
                case 40:
                    int pis = msg.arg1;
                    ArdourPlugin.InputParameter ips = currentPlugin.getParameter(pis);
                    if( ips.current != (float)msg.obj ) {
                        ips.current = (float)msg.obj;
                        Object[] plsargs = new Object[2];
                        plsargs[0] = currentPlugin.getParameter(pis).parameter_index;
                        plsargs[1] = ips.current;
                        Message fms = onChangeHandler.obtainMessage(PLUGIN_PARAMETER_CHANGED, currentPlugin.getTrackId(), currentPlugin.getPluginId(), plsargs);
                        onChangeHandler.sendMessage(fms );
                    }
                    break;
            }
        }
    };


    public void setOnChangeHandler(Handler onChangeHandler) {
        this.onChangeHandler = onChangeHandler;
    }

    @Override
    public void onClick(View v) {
        switch((String)v.getTag()) {
            case "resetPlugin":
                onChangeHandler.sendMessage(onChangeHandler.obtainMessage(PLUGIN_RESET, currentPlugin.getTrackId(), currentPlugin.getPluginId()) );
                break;
            case "pluginTitle":
//                this.removeAllViews();
                this.initLayout(false, track);
                bInitEnabled = true;
                if(currentPlugin.getPluginId() == track.pluginDescriptors.size()) {
                    onChangeHandler.sendMessage(onChangeHandler.obtainMessage(PLUGIN_DESCRIPTOR_REQUEST, currentPlugin.getTrackId(), 1));
                }
                else {
                    onChangeHandler.sendMessage(onChangeHandler.obtainMessage(PLUGIN_DESCRIPTOR_REQUEST, currentPlugin.getTrackId(), currentPlugin.getPluginId() + 1));
                }
                break;
            case "close":
                onChangeHandler.sendMessage(onChangeHandler.obtainMessage(SendsLayout.RESET_LAYOUT));
                break;

            case "next":
                onChangeHandler.sendMessage(onChangeHandler.obtainMessage(PLUGIN_NEXT));
                break;

            case "prev":
                onChangeHandler.sendMessage(onChangeHandler.obtainMessage(PLUGIN_PREV));
                break;

            case "bypass":
                onChangeHandler.sendMessage(onChangeHandler.obtainMessage(PLUGIN_BYPASS, currentPlugin.getTrackId(), currentPlugin.getPluginId(), !ttbBypass.getToggleState() ? 1 : 0));
                break;

            default:
                break;
        }
    }
}
