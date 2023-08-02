package net.quackimpala7321.crm;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.quackimpala7321.crm.util.ItemStackUtil;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CauldronRecipe {
    private final Identifier id;
    private final List<ItemStack> input;

    private final Item output;
    private final int outputCount;
    private final int experience;

    public CauldronRecipe(Identifier id, List<ItemStack> input, Item output, int outputCount, int experience) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.outputCount = outputCount;
        this.experience = experience;
    }

    public ItemStack getOutput() {
        return new ItemStack(this.output, this.outputCount);
    }

    public List<ItemStack> getInput() {
        return this.input;
    }

    public int getExperience() {
        return this.experience;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("type", CauldronRecipeManager.CAULDRON_RECIPE_ID.toString());
        json.addProperty("experience", this.experience);

        JsonArray input = new JsonArray();
        this.input.forEach(itemStack -> input.add(ItemStackUtil.toJson(itemStack)));
        json.add("ingredients", input);

        JsonObject output = new JsonObject();
        output.addProperty("item", Registries.ITEM.getId(this.output).toString());
        output.addProperty("count", this.outputCount);
        json.add("result", output);

        return json;
    }

    public Identifier getId() {
        return this.id;
    }

    public static class Builder {
        private Identifier id;
        private final List<ItemStack> input = Lists.newArrayList();
        private ItemStack output;
        private int experience;

        public static Builder create() {
            return new Builder();
        }

        private void setId(Identifier id) {
            this.id = id;
        }

        public Builder input(ItemStack ... input) {
            this.input.addAll(Arrays.asList(input));
            return this;
        }

        public Builder output(ItemStack output) {
            this.output = output;
            return this;
        }

        public Builder experience(int experience) {
            this.experience = experience;
            return this;
        }

        public CauldronRecipe build(Consumer<CauldronRecipe> consumer, Identifier id) {
            this.setId(id);
            CauldronRecipe recipe = new CauldronRecipe(this.id, this.input, this.output.getItem(), this.output.getCount(), this.experience);
            consumer.accept(recipe);

            return recipe;
        }
    }
}
