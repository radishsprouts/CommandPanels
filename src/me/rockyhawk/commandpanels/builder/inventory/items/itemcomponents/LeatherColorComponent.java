package me.rockyhawk.commandpanels.builder.inventory.items.itemcomponents;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.builder.inventory.items.ItemComponent;
import me.rockyhawk.commandpanels.session.inventory.PanelItem;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherColorComponent implements ItemComponent {

    @Override
    public ItemStack apply(Context ctx, ItemStack itemStack, Player player, PanelItem item) {
        if (item.leatherColor() == null) return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof LeatherArmorMeta leatherMeta) {
            try {
                String[] rgb = ctx.text.parseTextToString(player, item.leatherColor()).split(",");
                Color colour = Color.fromRGB(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim()));
                leatherMeta.setColor(colour);
                itemStack.setItemMeta(leatherMeta);
            } catch (Exception ignored) {}
        }

        return itemStack;
    }
}
