package net.fabricmc.err.nowaterredstone.commands;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.arguments.BoolArgumentType;

import net.fabricmc.err.nowaterredstone.NoWaterRedstone;

public class Commands {

	public class Permissions {
		public static int Player = 0;
		public static int Player_No_Spawn_Protection = 1;
		public static int Player_Commander = 2;
		public static int Server_Commander = 3;
		public static int OP = 4;
	}

	public static void initialize(){
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			LiteralCommandNode<ServerCommandSource> modNode = CommandManager
				.literal(NoWaterRedstone.MODID)
				.build();

			LiteralCommandNode<ServerCommandSource> versionNode = CommandManager
				.literal("version")
				.executes(new Version())
				.build();
				
			LiteralCommandNode<ServerCommandSource> enableNode = CommandManager
				.literal("enable")
				.requires(source -> source.hasPermissionLevel(Permissions.OP))
				.then(
					RequiredArgumentBuilder.<ServerCommandSource, Boolean>argument("value", BoolArgumentType.bool())
					.executes(context -> Enable.update(context, BoolArgumentType.getBool(context, "value")))
				)
				.executes(new Enable())
				.build();
			
			dispatcher.getRoot().addChild(modNode);
			modNode.addChild(versionNode);
			modNode.addChild(enableNode);
        });
	}
}
