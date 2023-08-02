package net.quackimpala7321.crm.util;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public final class ItemStackUtil {
    public static ItemStack fromJson(JsonObject json) {
        int count;
        try {
            count = json.get("count").getAsInt();
        } catch (Exception e) {
            count = 1;
        }

        Identifier id = Identifier.tryParse(json.get("item").getAsString());
        Item item = Registries.ITEM.get(id);

        return new ItemStack(item, count);
    }

    public static JsonObject toJson(ItemStack itemStack) {
        JsonObject json = new JsonObject();

        json.addProperty("item", Registries.ITEM.getId(itemStack.getItem()).toString());
        json.addProperty("count", itemStack.getCount());

        return json;
    }

    public static boolean equalsList(List<ItemStack> left, List<ItemStack> right) {
        if (left.size() != right.size()) return false;

        for (int i = 0; i < left.size(); i++) {
            if (!ItemStack.areEqual(left.get(i), right.get(i))) return false;
        }

        return true;
    }
}
