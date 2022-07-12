package com.gamerduck.kitpvp.commands;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public class Test extends Command {

    public Test() {
        super("test");
        setDefaultExecutor(this::onCommand);
    }

    public void onCommand(CommandSender sender, CommandContext context) {
//		Player p = (Player) sender;
//		User user = PrisonMain.getInstance().getServer().getPlayer(p);
//		user.setPrimaryRank(new Rank("PrimaryRank", Component.empty(), Component.empty()));
//		user.addSecondaryRank(new Rank("SecondaryRank", Component.empty(), Component.empty()), 
//				new Rank("SecondaryRank2", Component.empty(), Component.empty()));
//		user.addPermission("test.permissions");
//		Mine mine = new Mine("TestMines", new Cuboid(p.getInstance(), new Pos(10, 50, 10), new Pos(-10, 100, -10)));
//		mine.addBlock(Block.DIAMOND_BLOCK, 100d);
//		mine.refill();
    }

}
