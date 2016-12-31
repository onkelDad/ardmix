/**
 * 
 */
package de.paraair.ardmix;

import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * @author lincoln
 *
 */
public class Track extends Fader {


	enum TrackType  {
		MASTER, AUDIO, MIDI, BUS, SEND, RECEIVE, PAN
    }

    @NonNull
	static final String getTrackTypeName(TrackType type) {
		switch (type) {
			case AUDIO:
				return "Audio";
			case BUS:
				return "BUS";
			case MIDI:
				return "Midi";
			case SEND:
				return "Send";
			case RECEIVE:
				return "Receive";
			case PAN:
				return "Pan";
			case MASTER:
				return "Master";
		}
		return "unknown track type";
	}

	public int remoteId;

	public TrackType type;
	public String name;
	public int source_id = 0;
	public int trackVolume = 0;
    public float panPosition = 0.5f;
	public int meter;

	public int currentSendVolume;
	public boolean currentSendEnable;

	public boolean recEnabled = false;
	public boolean soloEnabled = false;
	public boolean muteEnabled = false;
	public boolean stripIn = false;
	public boolean soloIsolateEnabled = false;
	public boolean soloSafeEnabled = false;

	// private
	private boolean trackVolumeOnSeekBar = false;
	//helper

	public int sendCount = 0;

	public final HashMap<Integer, ArdourPlugin> pluginDescriptors = new HashMap<>();


	public void setTrackVolumeOnSeekBar(boolean val){
		trackVolumeOnSeekBar = val;
	}
	public boolean getTrackVolumeOnSeekBar(){
		return trackVolumeOnSeekBar;
	}


	public void addPlugin(int pluginIndex, String pluginName, int enabled) {
		ArdourPlugin plugin = new ArdourPlugin(remoteId, pluginIndex, 1);
		plugin.setName(pluginName);
		plugin.enabled = (enabled > 0);
		pluginDescriptors.put(pluginIndex, plugin);
	}

	public ArdourPlugin getPluginDescriptor(int pluginIndex) {
		return pluginDescriptors.get(pluginIndex );
	}


}
