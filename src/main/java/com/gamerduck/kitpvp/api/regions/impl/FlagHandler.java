package com.gamerduck.kitpvp.api.regions.impl;

import com.gamerduck.kitpvp.api.Executor;
import com.gamerduck.kitpvp.api.interfaces.Manager;
import com.gamerduck.kitpvp.api.Server;
import com.gamerduck.kitpvp.api.player.GamePlayer;
import com.gamerduck.kitpvp.api.regions.Area;
import com.gamerduck.kitpvp.api.regions.Flag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;

import java.util.Optional;

public class FlagHandler implements Manager {
    public FlagHandler() {
    }

    @Override
    public void register() {
        MinecraftServer.getGlobalEventHandler().addListener(EntityAttackEvent.class, (e) -> {
            Executor.runAsync(() -> {
                if (e.getEntity() instanceof Player p
                        && e.getTarget() instanceof Player pl) {
                    GamePlayer attacker = Server.getPlayerManager().getPlayer(p.getUuid());
                    GamePlayer target = Server.getPlayerManager().getPlayer(pl.getUuid());
                    if (attacker.getCurrentArea().getFlags().containsKey(Flag.PVP)
                            && attacker.getCurrentArea().getFlags().get(Flag.PVP) == true
                            && target.getCurrentArea().getFlags().containsKey(Flag.PVP)
                            && target.getCurrentArea().getFlags().get(Flag.PVP) == true) {
                    } else {

                    }
                }
            });
        }).addListener(PlayerBlockPlaceEvent.class, e -> {
            Executor.runAsync(() -> {
                if (e.getPlayer().hasPermission("pvp.admin")) return;
                Optional<Area> blockArea = Server.getAreaManager().getAreas().values().stream().filter(area -> area.getCube().isInCube(e.getBlockPosition())).findFirst();
                Area area = blockArea.isEmpty() ? null : blockArea.get();
                if (area == null) e.setCancelled(true);
                if (area.getFlags().containsKey(Flag.BUILD)) {
                    e.setCancelled(area.getFlags().get(Flag.BUILD));
                } else {
                    e.setCancelled(Flag.BUILD.getDefaultValue());
                }
            });
        });

    }
}
