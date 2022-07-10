package com.gamerduck.kitpvp.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public class Stop extends Command {

	public Stop() {
		super("stop");
		setDefaultExecutor(this::onCommand);
	}
	
	public void onCommand(CommandSender sender, CommandContext context) {
		MinecraftServer.stopCleanly();
	}
	
}
