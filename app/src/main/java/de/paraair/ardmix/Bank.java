package de.paraair.ardmix;

import java.util.ArrayList;

/**
 * Created by onkel on 19.10.16.
 */

public class Bank implements Cloneable{

    ToggleTextButton button = null;

    public ToggleTextButton getButton() {
        return button;
    }

    enum BankType { ALL, AUDIO, BUS };

    private ArrayList<Strip> strips = new ArrayList();
    private String name;
    private BankType type = BankType.ALL;

    public Bank() {}

    public Bank(String name) {
        setName(name);
    }

    public void add(String name, int remoteId, boolean enabled) {
        Strip strip = new Strip();
        strip.id = remoteId;
        strip.name = name;
        strip.enabled = enabled;
        int insert_index = 0;
        for(Strip p: strips) {
            if (p.id < remoteId)
                insert_index++;
        }
        strips.add(insert_index, strip);
    }

    public boolean contains(int remoteId) {
        for(Strip s: strips)
            if (s.id == remoteId)
                return true;
        return false;
    }

    public void remove(int id) {
        for( Strip s: strips){
            if( s.id == id) {
                strips.remove(s);
                return;
            }
        }
        return;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if( button != null )
            button.setAllText(name);
        this.name = name;
    }

    public class Strip {
        /** remote id of the contained track (1-n) */

        public int id;
        public String name;
        public boolean enabled;
    }

    public ArrayList<Strip> getStrips() {
        return strips;
    }

    public void setType(BankType type) {
        this.type = type;
    }

    public BankType getType() {
        return type;
    }

    protected Bank clone() {
        Bank clone = new Bank(name);
        for(Strip strip: strips ) {
            clone.add(strip.name, strip.id, strip.enabled);
        }
        return clone ;
    }
}
