package net.quackimpala7321.crm.util;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.quackimpala7321.crm.CauldronRecipe;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class CauldronRecipeProvider implements DataProvider {
    private final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;

    public CauldronRecipeProvider(FabricDataOutput output) {
        this.output = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "recipes");
    }

    public abstract void generateRecipe(Consumer<CauldronRecipe> consumer);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        final Set<CauldronRecipe> cauldronRecipes = Sets.newHashSet();

        this.generateRecipe(cauldronRecipes::add);

        final List<CompletableFuture<?>> futures = new ArrayList<>();

        for (CauldronRecipe recipe : cauldronRecipes) {
            futures.add(DataProvider.writeToPath(writer, recipe.toJson(), getOutputPath(recipe)));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private Path getOutputPath(CauldronRecipe recipe) {
        return pathResolver.resolveJson(recipe.getId());
    }

    @Override
    public String getName() {
        return "CauldronRecipes";
    }
}
