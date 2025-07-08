package net.cookedseafood.messycraft;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.cookedseafood.genericregistry.registry.Registries;
import net.cookedseafood.messycraft.command.CraftCommand;
import net.cookedseafood.messycraft.command.MessyCraftCommand;
import net.cookedseafood.messycraft.recipe.MessyRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessyCraft implements ModInitializer {
    public static final String MOD_ID = "messy-craft";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final byte VERSION_MAJOR = 0;
    public static final byte VERSION_MINOR = 3;
    public static final byte VERSION_PATCH = 0;

    public static final String MOD_NAMESPACE = "messy_craft";
    public static final Identifier RECIPE_LOADER_ID = Identifier.of(MOD_NAMESPACE, "recipe_loader");
    public static final Pattern PATH_PATTERN = Pattern.compile("^(.+\\/)*(.+)\\.(.+)$");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("[Messy-Craft] Loaded!");

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(RECIPE_LOADER_ID, wrapperLookup -> {
            return new SimpleSynchronousResourceReloadListener() {
                private final Identifier id = RECIPE_LOADER_ID;

                @Override
                public Identifier getFabricId() {
                    return id;
                }

                @Override
                public void reload(ResourceManager resourceManager) {
                    reloadMessyRecipe(resourceManager, wrapperLookup);
                }
            };
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> MessyCraftCommand.register(dispatcher, registryAccess));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CraftCommand.register(dispatcher, registryAccess));
    }

    public static void reloadMessyRecipe(ResourceManager resourceManager, RegistryWrapper.WrapperLookup wrapperLookup) {
        LOGGER.info("[Messy-Craft] Reloading recipes!");

        Registries.remove(MessyRecipe.class);
        resourceManager.findResources("messy_recipe", path -> path.getPath().endsWith(".json"))
            .entrySet()
            .forEach((resourceEntry) -> {
                Identifier resourceId = resourceEntry.getKey();
                Resource resource = resourceEntry.getValue();

                JsonObject recipeJsonObject;
                try {
                    recipeJsonObject = new Gson().fromJson(resource.getReader(), JsonObject.class);
                } catch (JsonSyntaxException | JsonIOException | IOException e) {
                    LOGGER.error("[Messy-Craft] Failed to load recipe " + resourceId.toString() + ".");
                    e.printStackTrace();
                    return;
                }

                NbtCompound recipeNbtCompound = (NbtCompound)JsonOps.INSTANCE.convertMap(NbtOps.INSTANCE, recipeJsonObject);
                MessyRecipe recipe = MessyRecipe.fromNbt(recipeNbtCompound, wrapperLookup);

                String resourceNamespace = resourceId.getNamespace();
                String resourcePath = resourceId.getPath();
                Matcher resourcePathMatcher = PATH_PATTERN.matcher(resourcePath);
                resourcePathMatcher.matches();
                String recipeName = resourcePathMatcher.group(2); // Get file name
                Identifier recipeId = Identifier.of(resourceNamespace, recipeName);

                Registries.register(recipeId, recipe);
            });
    }
}
