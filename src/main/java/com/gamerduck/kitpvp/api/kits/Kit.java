package com.gamerduck.kitpvp.api.kits;

import com.gamerduck.kitpvp.api.player.GamePlayer;
import lombok.Getter;
import net.minestom.server.item.ItemStack;

import java.util.List;

public abstract class Kit {
    @Getter public ItemStack displayItem;
    @Getter public String name;
    @Getter public double cost;
    @Getter public List<ItemStack> items;
    @Getter public List<GamePlayer> playersWithKit;
    @Getter public String permission;
    public Kit(ItemStack displayItem, String name, double cost, String permission, List<ItemStack> items, List<GamePlayer> playersWithKit) {
        this.displayItem = displayItem;
        this.name = name;
        this.cost = cost;
        this.permission = permission;
        this.items = items;
        this.playersWithKit = playersWithKit;
    }
}
