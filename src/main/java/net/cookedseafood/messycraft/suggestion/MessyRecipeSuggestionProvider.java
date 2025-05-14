package net.cookedseafood.messycraft.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.cookedseafood.genericregistry.registry.Registries;
import net.cookedseafood.messycraft.recipe.MessyRecipe;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class MessyRecipeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        Registries.get(MessyRecipe.class).entrySet()
            .forEach(recipeEntry -> {
                Identifier id = recipeEntry.getKey();
                MessyRecipe recipe = recipeEntry.getValue();

                String candidate = id.toString().replace(':', '.');
                if (CommandSource.shouldSuggest(builder.getRemaining(), candidate)) {
                    builder.suggest(candidate, recipe.getIngredients()::toString);
                }
            });

        return builder.buildFuture();
    }
}
