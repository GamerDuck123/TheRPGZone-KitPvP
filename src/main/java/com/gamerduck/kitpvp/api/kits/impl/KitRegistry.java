package com.gamerduck.kitpvp.api.kits.impl;

import com.gamerduck.kitpvp.api.kits.Kit;
import com.gamerduck.kitpvp.api.player.GamePlayer;
import lombok.Getter;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class KitRegistry {

    @Getter
    ArrayList<Kit> kits;
    public KitRegistry() {
        kits = new ArrayList<Kit>();
    }

    public Kit getKit(String name) {
        Optional<Kit> kit = kits.stream().filter(k -> k.getName().equalsIgnoreCase(name)).findFirst();
        return kit.isEmpty() ? null : kit.get();
    }

    public void register(Kit kit) {
        kits.add(kit);
    }

    public void openAvailableKits(GamePlayer p) {
        Inventory inv = new Inventory(InventoryType.CHEST_6_ROW, "Available Kits");
        List<Kit> available = kits.stream().filter(k -> p.hasPermission(k.getPermission())).collect(Collectors.toList());
        available.stream().forEachOrdered(k -> inv.addItemStack(k.getDisplayItem().withTag(Tag.String("kit"), k.getName())));
    }

    public void openBuyableKits(GamePlayer p) {
        Inventory inv = new Inventory(InventoryType.CHEST_6_ROW, "Buyable Kits");
        List<Kit> buyable = kits.stream().filter(k -> !p.hasPermission(k.getPermission())).collect(Collectors.toList());
        buyable.stream().forEachOrdered(k -> inv.addItemStack(k.getDisplayItem().withTag(Tag.String("kit"), k.getName())));
    }

}
