package com.gamerduck.kitpvp.api.kits.impl;

import com.gamerduck.kitpvp.api.Server;
import com.gamerduck.kitpvp.api.kits.Kit;
import lombok.Getter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.permission.Permission;

import java.util.ArrayList;
import java.util.List;

public class FighterKit extends Kit {
    @Getter public List<ItemStack> items = new ArrayList<ItemStack>();
    @Getter public List<ItemStack> playersWithKit = new ArrayList<ItemStack>();
    public FighterKit() {
        super(null, "Fighter", "kits.figher", 0.0, items, playersWithKit);
        Server.getKitRegistry().register(this);
    }
}
