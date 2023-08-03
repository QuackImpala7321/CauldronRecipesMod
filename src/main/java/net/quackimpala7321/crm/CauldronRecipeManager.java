package net.quackimpala7321.crm;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.quackimpala7321.crm.util.ItemStackUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class CauldronRecipeManager extends SinglePreparationResourceReloader<Map<Identifier, CauldronRecipe>> implements IdentifiableResourceReloadListener {
    public static final Identifier CAULDRON_RECIPE_ID = new Identifier(CauldronRecipesMod.MOD_ID, "cauldron");
    private final Map<Identifier, CauldronRecipe> recipes = new HashMap<>();

    public Map<Identifier, CauldronRecipe> getRecipes() {
        return this.recipes;
    }

    private CauldronRecipe deserialize(Identifier identifier, JsonObject json) {
        int experience;
        try {
            experience = json.get("experience").getAsInt();
        } catch (Exception e) {
            experience = 0;
        }

        List<ItemStack> input = new ArrayList<>();
        for (JsonElement jsonElement : json.getAsJsonArray("ingredients")) {
            input.add(ItemStackUtil.fromJson((JsonObject) jsonElement));
        }
        input.sort(Comparator.comparing(ItemStack::getTranslationKey));
        JsonObject result = json.getAsJsonObject("result");

        String idString = result.get("item").getAsString();
        Identifier id = Identifier.tryParse(idString);

        int count;
        try {
            count = result.get("count").getAsInt();
        } catch (Exception e) {
            count = 1;
        }

        return new CauldronRecipe(identifier, input, Registries.ITEM.get(id), count, experience);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(CauldronRecipesMod.MOD_ID, "cauldron_recipes");
    }

    @Override
    protected Map<Identifier, CauldronRecipe> prepare(ResourceManager manager, Profiler profiler) {
        CauldronRecipesMod.LOGGER.info("Loading cauldron recipes");
        Map<Identifier, CauldronRecipe> preparedRecipes = new HashMap<>();

        for (var resourceEntry : manager.findResources("recipes", identifier -> identifier.getPath().endsWith(".json")).entrySet()) {
            Identifier id = resourceEntry.getKey();
            Resource resource = resourceEntry.getValue();

            try (BufferedReader reader = resource.getReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (!Objects.equals(json.get("type").getAsString(), CAULDRON_RECIPE_ID.toString())) continue;

                preparedRecipes.put(id, this.deserialize(id, json));
            } catch (IOException e) {
                CauldronRecipesMod.LOGGER.warn("Could not load recipe {}", id.getPath());
            }
        }

        return preparedRecipes;
    }

    @Override
    protected void apply(Map<Identifier, CauldronRecipe> prepared, ResourceManager manager, Profiler profiler) {
        this.recipes.clear();
        this.recipes.putAll(prepared);
        CauldronRecipesMod.LOGGER.info("Loaded cauldron recipes");
    }
}
