package me.rockyhawk.commandpanels.builder.inventory.items.itemcomponents;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.builder.inventory.items.ItemComponent;
import me.rockyhawk.commandpanels.session.inventory.PanelItem;
import me.rockyhawk.commandpanels.util.ServerVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class TrimComponent implements ItemComponent {

    @Override
    public ItemStack apply(Context ctx, ItemStack itemStack, Player player, PanelItem item) {
        if (item.armorTrim() == null || !ServerVersion.isAtLeast(1, 20)) return itemStack;
        return TrimApplier.applyTrim(ctx, itemStack, player, item);
    }

    private static class TrimApplier {
        static ItemStack applyTrim(Context ctx, ItemStack itemStack, Player player, PanelItem item) {
            String[] trimList = ctx.text.parseTextToString(player, item.armorTrim()).split("\\s+");
            if (trimList.length != 2) return itemStack;

            String trimMaterialString = trimList[0].toLowerCase();
            String trimPatternString = trimList[1].toLowerCase();

            try {
                TrimMaterial trimMaterial = org.bukkit.Registry.TRIM_MATERIAL.get(NamespacedKey.fromString("minecraft:" + trimMaterialString));
                TrimPattern trimPattern = org.bukkit.Registry.TRIM_PATTERN.get(NamespacedKey.fromString("minecraft:" + trimPatternString));

                if (trimMaterial != null && trimPattern != null) {
                    ItemMeta meta = itemStack.getItemMeta();
                    if (meta instanceof ArmorMeta armorMeta) {
                        ArmorTrim trim = new ArmorTrim(trimMaterial, trimPattern);
                        armorMeta.setTrim(trim);
                        itemStack.setItemMeta(armorMeta);
                    }
                }
            } catch (Throwable ignored) {}

            return itemStack;
        }
    }
}
