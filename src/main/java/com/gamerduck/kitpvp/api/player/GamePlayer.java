package com.gamerduck.kitpvp.api.player;

import com.gamerduck.kitpvp.api.Executor;
import com.gamerduck.kitpvp.api.Server;
import com.gamerduck.kitpvp.api.economy.Account;
import com.gamerduck.kitpvp.api.permissions.Rank;
import com.gamerduck.kitpvp.api.regions.Area;
import com.gamerduck.kitpvp.api.regions.Cuboid;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.permission.Permission;
import net.minestom.server.permission.PermissionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class GamePlayer extends Player implements PermissionHandler, CommandSender {

	@Getter Rank primaryRank;
	@Getter final HashSet<Rank> secondaryRanks;
	@Getter final HashMap<Currency, Account> accounts;
	@Getter final HashSet<Permission> permissions;
	@Getter @Setter Point positionOne;
	@Getter @Setter Point positionTwo;
	@Getter @Setter Cuboid currentCube;
	
	protected GamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
		super(uuid, username, playerConnection);
		this.accounts = new HashMap<Currency, Account>();
		this.permissions = new HashSet<Permission>();
		this.secondaryRanks = new HashSet<Rank>();
	}
	
	public GamePlayer(Player player) {
		this(player.getUuid(), player.getUsername(), player.getPlayerConnection());
	}

	public void logOut() {

	}

	public Area getCurrentArea() {
		Optional<Area> playerArea = Server.getAreaManager().getAreas().values().stream().filter(area -> area.isPlayerInArea(this)).findFirst();
		return playerArea.isEmpty() ? null : playerArea.get();
	}

	public void getCurrentArea(Consumer<Area> area) {
		Executor.runAsync(() -> {
			area.accept(getCurrentArea());
		});
	}

	/*
	 * Miscs
	 */

	public void sendMessage(Component comp) {
		sendMessage(comp);
	}

	public void sendMessage(String s) {
		sendMessage(Component.text(s));
	}
	
	public void sendMessages(ArrayList<String> strs) {
		strs.forEach(s -> sendMessage(s));
	}
	
	/*
	 * Economy
	 */
	
	public Account addAccount(Currency type, double amount) {
		return accounts.put(type, new Account(this.uuid, amount));
	}

	public Account getAccount(Currency type) {
		return accounts.get(type);
	}
	
	public double getBalance(Currency type) {
		return getAccount(type).getBalance();
	}
	
	public Account addBalance(Currency type, double amount) {
		return getAccount(type).add(amount);
	}
	
	public Account subtractBalance(Currency type, double amount) {
		return getAccount(type).subtract(amount);
	}

	/*
	 * Ranks / Permissions
	 */

	public boolean setPrimaryRank(Rank rank) {
		if (!Server.getRankManager().rankExists(rank)) return false;
		primaryRank = rank;
		return true;
	}

	public boolean hasSecondaryRank(Rank rank) {
		if (!Server.getRankManager().rankExists(rank)) return false;
		return secondaryRanks.contains(rank);
	}
	
	public void addSecondaryRank(Rank... rank) {
		for (Rank r : rank) secondaryRanks.add(r);
		
	}
	
	public void removeSecondaryRank(Rank... rank) {
		for (Rank r : rank) secondaryRanks.remove(r);
	}

	@Override
	public void addPermission(@NotNull Permission permission) {
		permissions.add(permission);
	}
	
	public void addPermission(@NotNull String permissionName) {
		addPermission(new Permission(permissionName));
	}
	
	@Override
	public @NotNull Set<Permission> getAllPermissions() {
		return permissions;
	}
	
	@Override
	public @Nullable Permission getPermission(String permissionName) {
		Optional<Permission> perm = permissions.stream().filter(p -> p.getPermissionName().equalsIgnoreCase(permissionName)).findFirst();
		return perm.isEmpty() ? null : perm.get();
	}
	
	@Override
	public boolean hasPermission(Permission permission) {
		HashSet<Permission> tempPerms = new HashSet<Permission>();
		tempPerms.addAll(primaryRank.getAllPermissions());
		tempPerms.addAll(permissions);
		secondaryRanks.forEach(r -> tempPerms.addAll(r.getAllPermissions()));
		return tempPerms.contains(permission);
	}
	
	@Override
	public boolean hasPermission(String permissionName) {
		return hasPermission(getPermission(permissionName));
	}
	
	@Override
	public void removePermission(@NotNull Permission permission) {
		permissions.remove(permission);
	}
	
	@Override
	public void removePermission(@NotNull String permissionName) {
		removePermission(getPermission(permissionName));
	}
}
