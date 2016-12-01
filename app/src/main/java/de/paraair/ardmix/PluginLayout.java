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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by onkel on 21.10.16.
 */

public class PluginLayout extends LinearLayout implements View.OnClickListener  {

    public static final int MSG_WHAT_PLUGIN_CHANGED = 28;
    public static final int MSG_WHAT_PLUGIN_RESET = 29;
    public static final int MSG_WHAT_PLUGIN_REQUEST = 22;
    public static final int MSG_WHAT_PLUGIN_NEXT = 31;
    public static final int MSG_WHAT_PLUGIN_PREV = 32;
    public static final int MSG_WHAT_PLUGIN_ENABLE = 35;
    private static final int NAVBUTTON_HEIGHT = 36;


    private static final int PARAMETER_HEIGHT = 32;

    private ArdourPlugin plugin;

    ToggleTextButton ttbBypass;

    private Context context;

    HashMap<Integer, String> plugins;

    private Handler onChangeHandler;
    private TextView pluginDescription;
    private Button resetPlugin;

    public PluginLayout(Context context) {
        super(context);
        this.context = context;
    }

    public void initLayout(boolean inlude_request, HashMap<Integer, String> plugins) {

        this.plugins = plugins;
        pluginDescription = new TextView(context);
        pluginDescription.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        pluginDescription.setTextSize(18);
        pluginDescription.setPadding(4,4,4,4);
        pluginDescription.setTextColor(Color.WHITE);
        pluginDescription.setTag("pluginTitle");
        if( plugins.size() > 0 ) {
            if (plugins.size() > 1)
                pluginDescription.setOnClickListener(this);
        }
        else
            pluginDescription.setText("No FX present");
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
        btnClose.setText("Close");
        btnClose.setOnClickListener(this);
        btnLayout.addView(btnClose);

        if( plugins.size() > 0 ) {
            resetPlugin = new Button(context);
            resetPlugin.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, NAVBUTTON_HEIGHT));
            resetPlugin.setText("reset");
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
            ttbBypass.setTag("BYPASS");
            ttbBypass.setAllText("BYPASS");
            ttbBypass.setOnClickListener(this);
            ttbBypass.onColor = 0xA0FF40FF;
//        ttbBypass.offColor = 0x20FF40FF;
            btnLayout.addView(ttbBypass);
            ttbBypass.setAutoToggle(true);

        }

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

        addView(btnLayout);

        if( inlude_request) {
            Message fm = onChangeHandler.obtainMessage(MSG_WHAT_PLUGIN_REQUEST, getId(), 0);
            onChangeHandler.sendMessage(fm);
        }

    }

    public void init(Track track, ArdourPlugin plugin) {
        pluginDescription.setText("(" + (plugin.getPluginId() + 1) + "/" + plugins.size() + ") - " + track.plugins.get(plugin.getPluginId()+1) + " - " + track.name);
        resetPlugin.setId(plugin.getPluginId());
        this.plugin = plugin;
        ttbBypass.setToggleState(!plugin.enabled);
        int pi = 0;


        for(ArdourPlugin.InputParameter parameter: plugin.getParameters()) {
            if( (parameter.flags & 0x80) == 0x80 && (parameter.flags & 0x100) != 0x100) {
                LinearLayout pLayout = new LinearLayout(context);
                pLayout.setOrientation(HORIZONTAL);
                pLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

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
                        parameterValue.setTag(pi);
                        pLayout.addView(parameterValue);
                    }
                    else {
                        FaderView parameterValue = new FaderView(context);
                        parameterValue.param = parameter;
                        parameterValue.setLayoutParams(new LayoutParams(240, PARAMETER_HEIGHT));
                        parameterValue.setMax(1000);
                        parameterValue.setOrientation(FaderView.Orientation.HORIZONTAL);
                        parameterValue.setId(pi);
                        parameterValue.setProgress(parameter.getFaderFromCurrent(1000));
                        parameterValue.setOnChangeHandler(mHandler);
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
                    parameterValue.setTag(pi);
                    pLayout.addView(parameterValue);
                }
                addView(pLayout);

            }
            else {
                if( (parameter.flags & 0x80) != 0x80 )
                    Log.d("Ardmix", "output parameter found: " + parameter.name);
            }
            pi++;
        }


    }

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

    private Handler mHandler = new Handler() {

        /* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {


            switch (msg.what) {
                case 10:
//                    Message bfm = onChangeHandler.obtainMessage(MSG_WHAT_BLOCK_SCROLL);
//                    onChangeHandler.sendMessage(bfm );
                    break;
                case 20:
                    int pi = msg.arg1;
                    ArdourPlugin.InputParameter ip = plugin.getParameter(pi);
                    ip.setCurrentFromFader(msg.arg2, 1000);
                    Object[] plargs = new Object[2];
                    plargs[0] = plugin.getParameter(pi).parameter_index;
                    plargs[1] = ip.current;
                    Message fm = onChangeHandler.obtainMessage(MSG_WHAT_PLUGIN_CHANGED, plugin.getTrackId(), plugin.getPluginId(), plargs);
                    onChangeHandler.sendMessage(fm );
//                    Message ufm = onChangeHandler.obtainMessage(MSG_WHAT_UNBLOCK_SCROLL);
//                    onChangeHandler.sendMessage(ufm );
                    break;
                case 30:
                    int pir = msg.arg1;
                    ArdourPlugin.InputParameter ipr = plugin.getParameter(pir);
//                    Object[] plargs = new Object[2];
//                    plargs[0] = plugin.getParameter(pir).parameter_index;
//                    plargs[1] = ipr.current;
//                    Message fm = onChangeHandler.obtainMessage(MSG_WHAT_PLUGIN_CHANGED, plugin.getTrackId(), plugin.getPluginId(), plargs);
//                    onChangeHandler.sendMessage(fm );
                    break;
//                    track.setTrackVolumeOnSeekBar(false)
                case 40:
                    int pis = msg.arg1;
                    ArdourPlugin.InputParameter ips = plugin.getParameter(pis);
                    if( ips.current != (float)msg.obj ) {
                        ips.current = (float)msg.obj;
                        Object[] plsargs = new Object[2];
                        plsargs[0] = plugin.getParameter(pis).parameter_index;
                        plsargs[1] = ips.current;
                        Message fms = onChangeHandler.obtainMessage(MSG_WHAT_PLUGIN_CHANGED, plugin.getTrackId(), plugin.getPluginId(), plsargs);
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
                onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_PLUGIN_RESET, plugin.getTrackId(), plugin.getPluginId()) );
                break;
            case "pluginTitle":
                this.removeAllViews();
                this.initLayout(false, plugins);
                if(plugin.getPluginId() == plugins.size()-1) {
                    onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_PLUGIN_REQUEST, getId(), 0));
                }
                else {
                    onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_PLUGIN_REQUEST, getId(), plugin.getPluginId()+1));
                }
                break;
            case "close":
                onChangeHandler.sendMessage(onChangeHandler.obtainMessage(SendsLayout.MSG_WHAT_RESET_LAYOUT));
                break;

            case "next":
                onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_PLUGIN_NEXT));
                break;

            case "prev":
                onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_PLUGIN_PREV));
                break;

            case "bypass":
                onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_PLUGIN_ENABLE, getId(), plugin.getPluginId(), !ttbBypass.getToggleState() ? 1 : 0));
                break;

            default:
                break;
        }
    }
}
