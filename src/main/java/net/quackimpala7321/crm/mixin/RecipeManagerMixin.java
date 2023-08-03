package net.quackimpala7321.crm.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.quackimpala7321.crm.CauldronRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    // This makes the recipe map ignore the cauldron recipes
    @Inject(
            method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At("HEAD")
    )
    public void applyMixin(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        List<Map.Entry<Identifier, JsonElement>> myRecipes = map.entrySet().stream().filter(entry -> {
            JsonObject json = entry.getValue().getAsJsonObject();

            Identifier identifier;
            try {
                identifier = Identifier.tryParse(json.get("type").getAsString());
                if(identifier == null) return false;
                return Objects.equals(identifier.toString(), CauldronRecipeManager.CAULDRON_RECIPE_ID.toString());
            } catch (Exception e) {
                return false;
            }
        }).toList();

       myRecipes.forEach(entry -> map.remove(entry.getKey(), entry.getValue()));
    }
}
