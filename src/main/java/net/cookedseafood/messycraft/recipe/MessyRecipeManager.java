package net.cookedseafood.messycraft.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public class MessyRecipeManager {
    private Map<Identifier, MessyRecipe> recipes;

    public MessyRecipeManager(Map<Identifier, MessyRecipe> recipes) {
        this.recipes = recipes;
    }

    public MessyRecipeManager() {
        this.recipes = new HashMap<>();
    }

    public static MessyRecipeManager of(Map<Identifier, MessyRecipe> recipes) {
        return new MessyRecipeManager(recipes);
    }

    public Map<Identifier, MessyRecipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(Map<Identifier, MessyRecipe> recipes) {
        this.recipes = recipes;
    }

    public MessyRecipe get(Identifier id) {
        return this.recipes.get(id);
    }

    public MessyRecipe put(Identifier id, MessyRecipe recipe) {
        return this.recipes.put(id, recipe);
    }

    public void putAll(Map<Identifier, MessyRecipe> recipes) {
        this.recipes.putAll(recipes);
    }

    public boolean containsKey(Identifier id) {
        return this.recipes.containsKey(id);
    }

    public boolean containsValue(MessyRecipe recipe) {
        return this.recipes.containsValue(recipe);
    }

    public MessyRecipe remove(Identifier id) {
        return this.recipes.remove(id);
    }

    public boolean remove(Identifier id, MessyRecipe recipe) {
        return this.recipes.remove(id, recipe);
    }

    public void clear() {
        this.recipes.clear();
    }

    public MessyRecipe replace(Identifier id, MessyRecipe recipe) {
        return this.recipes.replace(id, recipe);
    }

    public boolean replace(Identifier id, MessyRecipe oldRecipe, MessyRecipe newRecipe) {
        return this.recipes.replace(id, oldRecipe, newRecipe);
    }

    public void replaceAll(BiFunction<Identifier, MessyRecipe , MessyRecipe> function) {
        this.recipes.replaceAll(function);
    }

    public Set<Map.Entry<Identifier, MessyRecipe>> entrySet() {
        return this.recipes.entrySet();
    }

    public MessyRecipeManager copy() {
        return new MessyRecipeManager(this.recipes);
    }

    public MessyRecipeManager deepCopy() {
        return new MessyRecipeManager(
            this.recipes.entrySet()
                .stream()
                .map(entry -> Map.entry(
                    Identifier.of(entry.getKey().getNamespace(), entry.getKey().getPath()),
                    entry.getValue().deepCopy()
                ))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue))
        );
    }

    public static MessyRecipeManager fromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        return new MessyRecipeManager(
            nbtCompound.entrySet().stream()
                .map(entry -> Map.entry(
                    Identifier.of(entry.getKey()),
                    MessyRecipe.fromNbt((NbtCompound)entry.getValue(), wrapperLookup)
                ))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue))
        );
    }

    public NbtCompound toNbt(RegistryWrapper.WrapperLookup wrapperLookup) {
        return this.entrySet().stream()
            .map(entry -> Map.entry(
                entry.getKey().toString(),
                entry.getValue().toNbt(wrapperLookup)
            ))
        .<NbtCompound>collect(NbtCompound::new, (nbtCompound, entry) -> nbtCompound.put(entry.getKey(), entry.getValue()), (left, right) -> left.putAll(right));
    }
}
