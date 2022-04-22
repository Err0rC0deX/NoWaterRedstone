package net.fabricmc.err.nowaterredstone.commands;

import net.fabricmc.err.nowaterredstone.Config;

import net.minecraft.text.LiteralText;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class Enable implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		context.getSource().sendFeedback(new LiteralText("enabled: " + Config.enable()), false);
		return Command.SINGLE_SUCCESS;
	}

	public static int update(CommandContext<ServerCommandSource> context, boolean value) {
		if (Config.enable() != value) {
			Config.enable(value);
			Config.write();
			context.getSource().sendFeedback(new LiteralText("enabled: " + Config.enable()), true);
		}
		return Command.SINGLE_SUCCESS;
	}
}
