package me.rockyhawk.commandpanels.builder.inventory.items.itemcomponents;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.builder.inventory.items.ItemComponent;
import me.rockyhawk.commandpanels.session.inventory.PanelItem;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantedComponent implements ItemComponent {

    @Override
    public ItemStack apply(Context ctx, ItemStack itemStack, Player player, PanelItem item) {
        if (item.enchantments().isEmpty()) return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;

        for (String enchantment : item.enchantments()) {
            String[] value = ctx.text.parseTextToString(player, enchantment)
                    .toLowerCase()
                    .split("\\s+");

            if (value.length < 2) continue;

            String enchantKey = value[0].contains(":") ? value[0] : "minecraft:" + value[0];
            int level;
            try {
                level = Integer.parseInt(value[1]);
            } catch (NumberFormatException e) {
                level = 1;
            }

            NamespacedKey key = NamespacedKey.fromString(enchantKey);
            Enchantment enchantmentVal = (key != null) ? Enchantment.getByKey(key) : null;
            if (enchantmentVal == null) {
                enchantmentVal = Enchantment.getByName(value[0].toUpperCase());
            }

            if (enchantmentVal != null) {
                if (meta instanceof EnchantmentStorageMeta storageMeta) {
                    storageMeta.addStoredEnchant(enchantmentVal, level, true);
                } else {
                    meta.addEnchant(enchantmentVal, level, true);
                }
            }
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
