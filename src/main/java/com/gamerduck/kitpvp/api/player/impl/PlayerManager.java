package com.gamerduck.kitpvp.api.player.impl;

import com.gamerduck.kitpvp.api.player.GamePlayer;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class PlayerManager {
    public final static Logger LOGGER = LoggerFactory.getLogger(PlayerManager.class);
    @Getter
    public final HashSet<GamePlayer> players;
    public PlayerManager() {
        players = new HashSet<GamePlayer>();
        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, (e) -> {
            addPlayer(new GamePlayer(e.getPlayer()));
        }).addListener(PlayerDisconnectEvent.class, (e) -> {
            removePlayer(getPlayer(e.getPlayer().getUuid()));
        });
    }

    public void addPlayer(GamePlayer player) {
        addPlayer(player, () -> {
            LOGGER.warn("Failed to add player to HashSet.. Something is wrong");
            player.kick("Something happened, please contact an admin");
        });
    }

    public void addPlayer(GamePlayer player, Runnable ifFailed) {
        players.add(player);
        if (!players.contains(player)) {ifFailed.run();}
    }

    public void removePlayer(GamePlayer player) {
        removePlayer(player, () -> {
            LOGGER.warn("Failed to remove player from HashSet.. Something is wrong");
        });
    }

    public void removePlayer(GamePlayer player, Runnable ifFailed) {
        players.remove(player);
        if (players.contains(player)) ifFailed.run();
    }

    public GamePlayer getPlayer(UUID uuid) {
        Optional<GamePlayer> player = players.stream().filter(p -> p.getUuid() == uuid).findFirst();
        return player.isEmpty() ? null : player.get();
    }

    public GamePlayer getPlayer(String name) {
        Optional<GamePlayer> player = players.stream().filter(p -> p.getUsername().equalsIgnoreCase(name)).findFirst();
        return player.isEmpty() ? null : player.get();
    }

    public void serialize() {

    }
    public void deserialize() {

    }
}
