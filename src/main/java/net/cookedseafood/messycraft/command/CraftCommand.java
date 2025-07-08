package net.cookedseafood.messycraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.cookedseafood.messycraft.recipe.MessyRecipe;
import net.cookedseafood.messycraft.suggestion.MessyRecipeSuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CraftCommand {
    private static final SimpleCommandExceptionType INGREDIENT_INSUFFICIENT_EXCEPTION =
        new SimpleCommandExceptionType(Text.literal("Missing materials."));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        LiteralCommandNode<ServerCommandSource> craft = dispatcher.register(
            CommandManager.literal("craft")
            .then(
                CommandManager.argument("item", StringArgumentType.string())
                .suggests(new MessyRecipeSuggestionProvider())
                .executes(context -> craft(context.getSource(), StringArgumentType.getString(context, "item")))
                .then(
                    CommandManager.argument("count", IntegerArgumentType.integer(1))
                    .executes(context -> craft(context.getSource(), StringArgumentType.getString(context, "item"), IntegerArgumentType.getInteger(context, "count")))
                )
            )
        );
        dispatcher.register(CommandManager.literal("c").redirect(craft));
    }

    public static int craft(ServerCommandSource source, String item) throws CommandSyntaxException {
        return craft(source, item, 1);
    }

    public static int craft(ServerCommandSource source, String item, int count) throws CommandSyntaxException {
        boolean isSuccessful = MessyRecipe.craft(Identifier.of(item.replace('.', ':')), count, source.getPlayerOrThrow());

        if (isSuccessful) {
            return 1;
        } else {
            throw INGREDIENT_INSUFFICIENT_EXCEPTION.create();
        }
    }
}
