package net.cookedseafood.messycraft.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang3.mutable.MutableInt;

import net.cookedseafood.genericregistry.registry.Registries;
import net.cookedseafood.messycraft.api.CraftMessyRecipeCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static net.cookedseafood.messycraft.MessyCraft.LOGGER;
import static net.cookedseafood.messycraft.MessyCraft.PATH_PATTERN;

public class MessyRecipe {
    private MessyIngredient ingredients;
    private MessyItemStack result;

    public MessyRecipe(MessyIngredient ingredients, MessyItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    /**
     * Craft the recipe of the id at the times if there is enough ingredients.
     * 
     * <p>Player's inventory will be modified if successfully crafted.</p>
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
     * Craft the recipe at the times if there is enough ingredients.
     * 
     * <p>Player's inventory will be modified if successfully crafted.</p>
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
            MessyIngredient.fromNbt(nbtCompound.getListOrEmpty("ingredients"), wrapperLookup),
            MessyItemStack.fromNbt(nbtCompound.getCompoundOrEmpty("result"), wrapperLookup)
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

    public static void reload(ResourceManager resourceManager, RegistryWrapper.WrapperLookup wrapperLookup) {
        Registries.remove(MessyRecipe.class);
        load(resourceManager, wrapperLookup);
    }

    public static void load(ResourceManager resourceManager, RegistryWrapper.WrapperLookup wrapperLookup) {
        MutableInt loadedCount = new MutableInt(0);
        resourceManager.findResources("messy_recipe", path -> path.getPath().endsWith(".json"))
            .forEach((resourceId, resource) -> {
                JsonObject recipeJsonObject;
                try {
                    BufferedReader reader = resource.getReader();
                    recipeJsonObject = new Gson().fromJson(reader, JsonObject.class);
                    reader.close();
                } catch (JsonSyntaxException | JsonIOException | IOException e) {
                    LOGGER.error("[Messy-Craft] Failed to load recipe from " + resourceId.toString(), e);
                    return;
                }

                if (recipeJsonObject == null) {
                    LOGGER.error("[Messy-Craft] Failed to load recipe from " + resourceId.toString() + ": Json is at EOF.");
                    return;
                }

                Matcher matcher = PATH_PATTERN.matcher(resourceId.getPath());
                if (!matcher.matches()) {
                    LOGGER.error("[Messy-Craft] Failed to load recipe from " + resourceId.toString() + ": Invalid path.");
                    return;
                }

                Identifier recipeId = Identifier.of(resourceId.getNamespace(), matcher.group(2));
                NbtCompound recipeNbtCompound = (NbtCompound)JsonOps.INSTANCE.convertMap(NbtOps.INSTANCE, recipeJsonObject);
                MessyRecipe recipe = MessyRecipe.fromNbt(recipeNbtCompound, wrapperLookup);
                Registries.register(recipeId, recipe);
                loadedCount.increment();
            });
        LOGGER.info("[Messy-Craft] Loaded " + loadedCount.intValue() + " recipes.");
    }
}
