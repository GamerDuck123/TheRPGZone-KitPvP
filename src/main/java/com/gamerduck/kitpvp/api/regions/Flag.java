package com.gamerduck.kitpvp.api.regions;

import lombok.Getter;

public enum Flag {
    PVP(false, "PvP" , "region.bypass.pvp"),
    BUILD(false, "Build", "region.bypass.pvp");

    boolean defaultValue;
    @Getter String bypassPerm;
    @Getter String name;

    Flag(boolean defaultValue, String name, String bypassPerm) {
        this.defaultValue = defaultValue;
        this.name = name;
        this.bypassPerm = bypassPerm;
    }

    public boolean getDefaultValue() {return defaultValue;}
}
