package net.cookedseafood.messycraft.command;

import com.mojang.brigadier.CommandDispatcher;
import net.cookedseafood.messycraft.MessyCraft;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class MessyCraftCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(
			CommandManager.literal("messycraft")
			.then(
				CommandManager.literal("version")
				.executes(context -> executeVersion((ServerCommandSource)context.getSource()))
			)
		);
	}

	public static int executeVersion(ServerCommandSource source) {
		source.sendFeedback(() -> Text.literal("Messy Craft " + MessyCraft.VERSION_MAJOR + "." + MessyCraft.VERSION_MINOR + "." + MessyCraft.VERSION_PATCH), false);
		return 0;
	}
}
