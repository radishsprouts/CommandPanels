package me.rockyhawk.commandpanels.builder.inventory.items.itemcomponents;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.builder.inventory.items.ItemComponent;
import me.rockyhawk.commandpanels.session.inventory.PanelItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomModelDataComponent implements ItemComponent {

    @Override
    public ItemStack apply(Context ctx, ItemStack itemStack, Player player, PanelItem item) {
        if (item.customModelData() == null) return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;

        String rawVal = ctx.text.parseTextToString(player, item.customModelData());
        try {
            int intVal = (int) Float.parseFloat(rawVal);
            meta.setCustomModelData(intVal);
        } catch (NumberFormatException ignored) {}

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
