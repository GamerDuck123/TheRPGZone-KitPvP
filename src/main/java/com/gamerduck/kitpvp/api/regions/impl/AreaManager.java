package com.gamerduck.kitpvp.api.regions.impl;

import com.gamerduck.kitpvp.api.Server;
import com.gamerduck.kitpvp.api.player.GamePlayer;
import com.gamerduck.kitpvp.api.regions.Area;
import com.gamerduck.kitpvp.api.regions.Cuboid;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.item.Material;

import java.util.HashMap;
import java.util.Optional;

public class AreaManager {
    @Getter
    final HashMap<String, Area> areas;
    public AreaManager() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerUseItemOnBlockEvent.class, (e) -> {
            if (e.getItemStack().material() == Material.WOODEN_AXE) {
                GamePlayer p = Server.getPlayerManager().getPlayer(e.getPlayer().getUuid());
                if (p.hasPermission("pvp.admin")) {
                    p.setPositionOne(e.getPosition());
                    if (p.getPositionTwo() != null) {
                        p.setCurrentCube(new Cuboid(e.getInstance(), p.getPositionOne(), p.getPositionTwo()));
                        p.sendMessage("Position one set... (" + p.getCurrentCube().getBlocks().size() + " blocks selected)");
                    } else p.sendMessage("Position one set...");

                }
            }

        }).addListener(PlayerBlockInteractEvent.class, e -> {

        });
        areas = new HashMap<String, Area>();
    }

    public void addArea(String name, Area area) {
        areas.put(name, area);
    }

    public void removeArea(String name) {
        areas.remove(name);
    }

    public Area getArea(String name) {
        Optional<String> area = areas.keySet().stream().filter(key -> key.equalsIgnoreCase(name)).findFirst();
        return area.isEmpty() ? null : areas.get(area);
    }

    public void serialize() {

    }
    public void deserialize() {

    }
}
