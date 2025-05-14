package net.cookedseafood.messycraft.recipe;

import java.util.HashMap;
import java.util.Map;
import net.cookedseafood.genericregistry.registry.Registries;
import net.cookedseafood.messycraft.api.CraftMessyRecipeCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MessyRecipe {
    private MessyIngredient ingredients;
    private MessyItemStack result;

    public MessyRecipe(MessyIngredient ingredients, MessyItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    /**
     * Craft the recipe of the id at the times, if there is enough ingredients.
     * 
     * <p>This modifies player's inventory if successfully crafted.</p>
     * 
     * @param recipeId
     * @param times
     * @param player
     * @return true if successfully crafted.
     */
    public static boolean craft(Identifier recipeId, int times, ServerPlayerEntity player) {
        MessyRecipe recipe = Registries.get(MessyRecipe.class, recipeId);
        if (recipe == null) {
            return false;
        }

        return recipe.deepCopy().craft(times, player);
    }

    /**
     * Craft the recipe at the times, if there is enough ingredients.
     * 
     * <p>This modifies player's inventory if successfully crafted.</p>
     * 
     * @param times
     * @param player
     * @return true if successfully crafted.
     */
    public boolean craft(int times, ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        MessyIngredient ingredients = this.getIngredients();
        ingredients.times(times);
        if (!ingredients.isIn(inventory)) {
            return false;
        }

        CraftMessyRecipeCallback.EVENT.invoker().interact(player, this, times);

        ingredients.removeFrom(inventory);

        MessyItemStack result = this.getResult();
        result.times(times);
        result.insertTo(inventory);
        return true;
    }

    public MessyIngredient getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(MessyIngredient ingredients) {
        this.ingredients = ingredients;
    }

    public MessyItemStack getResult() {
        return this.result;
    }

    public void setResult(MessyItemStack result) {
        this.result = result;
    }

    public MessyRecipe copy() {
        return new MessyRecipe(this.ingredients, this.result);
    }

    public MessyRecipe deepCopy() {
        return new MessyRecipe(this.ingredients.deepCopy(), this.result.deepCopy());
    }

    public static MessyRecipe fromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        return new MessyRecipe(
            MessyIngredient.fromNbt(nbtCompound.getList("ingredients", NbtElement.COMPOUND_TYPE), wrapperLookup),
            MessyItemStack.fromNbt(nbtCompound.getCompound("result"), wrapperLookup)
        );
    }

    public NbtCompound toNbt(RegistryWrapper.WrapperLookup wrapperLookup) {
        return new NbtCompound(
            new HashMap<>(
                Map.<String,NbtElement>of(
                    "ingredients",
                    this.ingredients.toNbt(wrapperLookup),
                    "result",
                    this.result.toNbt(wrapperLookup)
                )
            )
        );
    }
}
