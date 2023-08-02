package net.quackimpala7321.crm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CauldronRecipesMod implements ModInitializer {
    public static final String MOD_ID = "crm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final CauldronRecipeManager CAULDRON_RECIPE_MANAGER = new CauldronRecipeManager();

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(CAULDRON_RECIPE_MANAGER);
    }
}
