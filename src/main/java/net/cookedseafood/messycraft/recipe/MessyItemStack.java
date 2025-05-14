package net.cookedseafood.messycraft.recipe;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class MessyItemStack {
    private ItemStack itemStack;

    public MessyItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static MessyItemStack of(ItemStack itemStack) {
        return new MessyItemStack(itemStack);
    }

    public static MessyItemStack of(ItemConvertible item) {
        return MessyItemStack.of(new ItemStack(item));
    }

    public boolean isIn(PlayerInventory inventory) {
        int count = this.getCount();

        int presentedCount = 0;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (ItemStack.areItemsAndComponentsEqual(itemStack, this.itemStack)) {
                presentedCount += itemStack.getCount();
                if (presentedCount >= count) {
                    return true;
                }
            }
        }

        return false;
    }

    public int removeFrom(PlayerInventory inventory) {
        return inventory.remove(itemStack -> ItemStack.areItemsAndComponentsEqual(itemStack, this.itemStack), this.getCount(), inventory.player.playerScreenHandler.getCraftingInput());
    }

    public boolean insertTo(PlayerInventory inventory) {
        return inventory.insertStack(this.itemStack);
    }

    public boolean removeFrom(MessyIngredient ingredients) {
        return ingredients.remove(this);
    }

    public void times(int times) {
        this.setCount(this.getCount() * times);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Identifier getId() {
        return this.itemStack.getId();
    }

    public String getIdAsString() {
        return this.itemStack.getIdAsString();
    }

    public String getCustomIdOrId() {
        return this.itemStack.getCustomIdOrId();
    }

    public String getCustomId() {
        return this.itemStack.getCustomId();
    }

    public int getCount() {
        return this.itemStack.getCount();
    }

    public void setCount(int count) {
        this.itemStack.setCount(count);
    }

    public ComponentMap getComponents() {
        return this.itemStack.getComponents();
    }

    public ComponentChanges getComponentChanges() {
        return this.itemStack.getComponentChanges();
    }

    public <T> T set(ComponentType<? super T> type, @Nullable T value) {
        return this.itemStack.set(type, value);
    }

    @Override
    public String toString() {
        return this.itemStack.toString();
    }

    public MessyItemStack copy() {
        return new MessyItemStack(this.itemStack);
    }

    public MessyItemStack deepCopy() {
        return new MessyItemStack(this.itemStack.copy());
    }

    public static MessyItemStack fromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        return new MessyItemStack(ItemStack.fromNbt(wrapperLookup, nbtCompound).get());
    }

    public NbtElement toNbt(RegistryWrapper.WrapperLookup wrapperLookup) {
        return this.itemStack.toNbt(wrapperLookup);
    }
}
