package net.cookedseafood.messycraft.recipe;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;

public class MessyRecipe {
    private MessyIngredient ingredients;
    private MessyItemStack result;

    public MessyRecipe(MessyIngredient ingredients, MessyItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
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
}
