package com.gamerduck.kitpvp.api.permissions;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.permission.Permission;
import net.minestom.server.permission.PermissionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Rank implements PermissionHandler, Serializable {
	
	private static final long serialVersionUID = 6522766582587113223L;
	public Set<Permission> permissions;
	public String prefix;
	public String suffix;
	@Getter public String name;

	public Rank() {}
	
	public Rank(String name, Component prefix, Component suffix) {
		permissions = new HashSet<Permission>();
		this.prefix = GsonComponentSerializer.gson().serialize(prefix);
		this.suffix = GsonComponentSerializer.gson().serialize(suffix);
		this.name = name;
	}
	public Rank(String name, String prefix, String suffix) {
		permissions = new HashSet<Permission>();
		this.prefix = prefix;
		this.suffix = suffix;
		this.name = name;
	}
	
	public Component getSuffix() {
		return Component.text(suffix);
	}
	
	public Component getPrefix() {
		return Component.text(prefix);
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
		return permissions.contains(permission);
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
