package net.cookedseafood.messycraft;

import java.util.regex.Pattern;
import net.cookedseafood.messycraft.command.CraftCommand;
import net.cookedseafood.messycraft.command.MessyCraftCommand;
import net.cookedseafood.messycraft.recipe.MessyRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
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
    public static final String MOD_NAMESPACE = "messy_craft";

    public static final byte VERSION_MAJOR = 0;
    public static final byte VERSION_MINOR = 4;
    public static final byte VERSION_PATCH = 1;

    public static final Identifier RECIPE_LOADER_ID = Identifier.of(MOD_NAMESPACE, "recipe_loader");
    public static final Pattern PATH_PATTERN = Pattern.compile("^(.+\\/)*(.+)\\.(.+)$");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(RECIPE_LOADER_ID, wrapperLookup -> {
            return new SimpleSynchronousResourceReloadListener() {
                private final Identifier id = RECIPE_LOADER_ID;

                @Override
                public Identifier getFabricId() {
                    return id;
                }

                @Override
                public void reload(ResourceManager resourceManager) {
                    MessyRecipe.reload(resourceManager, wrapperLookup);
                }
            };
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> MessyCraftCommand.register(dispatcher, registryAccess));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CraftCommand.register(dispatcher, registryAccess));
    }
}
