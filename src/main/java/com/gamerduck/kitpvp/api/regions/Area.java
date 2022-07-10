package com.gamerduck.kitpvp.api.regions;

import com.gamerduck.kitpvp.api.Executor;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Area implements Cloneable {
	@Getter Cuboid cube;
	@Getter HashMap<Flag, Boolean> flags;
	protected Area(Cuboid cube) {
		this.cube = cube;
		this.flags = new HashMap<Flag, Boolean>();
	}

	public boolean isPlayerInArea(Player p) {
		return cube.isInCube(p.getPosition());
	}

	public void isPlayerInArea(Player p, Consumer<Boolean> isInArea) {
		Executor.runAsync(() -> {
			isInArea.accept(cube.isInCube(p.getPosition()));
		});
	}

	public void allPlayersInArea(Consumer<List<Player>> players) {
		Executor.runAsync(() -> {
			players.accept(MinecraftServer.getConnectionManager().getOnlinePlayers().stream().filter(p -> cube.isInCube(p.getPosition())).collect(Collectors.toList()));
		});
	}

	public void addFlag(Flag flag, Boolean value) {
		flags.put(flag, value);
	}
}
