package net.cookedseafood.messycraft.api;

import net.cookedseafood.messycraft.recipe.MessyRecipe;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface CraftMessyRecipeCallback {
    Event<CraftMessyRecipeCallback> EVENT = EventFactory.createArrayBacked(CraftMessyRecipeCallback.class,
        (listeners) -> (player, recipeId, times) -> {
            for (CraftMessyRecipeCallback listener : listeners) {
                ActionResult result = listener.interact(player, recipeId, times);

                if(result != ActionResult.PASS) {
                    return result;
                }
            }

        return ActionResult.PASS;
    });

    ActionResult interact(PlayerEntity player, MessyRecipe recipeId, int times);
}
