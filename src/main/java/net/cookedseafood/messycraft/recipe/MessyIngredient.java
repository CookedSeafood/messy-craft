package net.cookedseafood.messycraft.recipe;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;

public class MessyIngredient {
    private Set<MessyItemStack> ingredients;

    public MessyIngredient(Set<MessyItemStack> ingredients) {
        this.ingredients = ingredients;
    }

    public MessyIngredient() {
        this.ingredients = new HashSet<>();
    }

    public boolean isIn(PlayerInventory inventory) {
        return this.stream().allMatch(itemStack -> itemStack.isIn(inventory));
    }

    public void removeFrom(PlayerInventory inventory) {
        this.forEach(itemStack -> itemStack.removeFrom(inventory));
    }

    public Set<MessyItemStack> getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(Set<MessyItemStack> ingredients) {
        this.ingredients = ingredients;
    }

    public boolean add(MessyItemStack itemStack) {
        return this.ingredients.add(itemStack);
    }

    public boolean addAll(Collection<MessyItemStack> itemStacks) {
        return this.ingredients.addAll(itemStacks);
    }

    public boolean contains(MessyItemStack itemStack) {
        return this.ingredients.contains(itemStack);
    }

    public boolean containsAll(Collection<MessyItemStack> itemStacks) {
        return this.ingredients.containsAll(itemStacks);
    }

    public boolean remove(MessyItemStack itemStack) {
        return this.ingredients.remove(itemStack);
    }

    public boolean removeAll(Collection<MessyItemStack> itemStacks) {
        return this.ingredients.removeAll(itemStacks);
    }

    public void clear() {
        this.ingredients.clear();
    }

    public void forEach(Consumer<? super MessyItemStack> action) {
        this.ingredients.forEach(action);
    }

    public Iterator<MessyItemStack> iterator() {
        return this.ingredients.iterator();
    }

    public Stream<MessyItemStack> stream() {
        return this.ingredients.stream();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.forEach(itemStack ->
            builder.append(itemStack.getCount() + " " + itemStack.getCustomIdOrId() + " + ")
        );

        return builder.substring(0, builder.length() - 3);
    }

    public MessyIngredient copy() {
        return new MessyIngredient(this.ingredients);
    }

    public MessyIngredient deepCopy() {
        return new MessyIngredient(
            this.stream()
                .map(MessyItemStack::deepCopy)
                .collect(Collectors.toSet())
        );
    }

    public static MessyIngredient fromNbt(NbtList nbtList, RegistryWrapper.WrapperLookup wrapperLookup) {
        return new MessyIngredient(
            nbtList.stream()
                .map(NbtCompound.class::cast)
                .map(itemStack -> MessyItemStack.fromNbt(itemStack, wrapperLookup))
                .collect(Collectors.toSet())
        );
    }
}
