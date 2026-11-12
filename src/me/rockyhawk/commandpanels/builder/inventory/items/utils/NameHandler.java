package me.rockyhawk.commandpanels.builder.inventory.items.utils;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.session.inventory.PanelItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class NameHandler {

    private final Context ctx;
    private final LoreFormatter loreFormatter;

    public NameHandler(Context ctx) {
        this.ctx = ctx;
        this.loreFormatter = new LoreFormatter(ctx);
    }

    public ItemStack setName(ItemStack item, PanelItem panelItem, Player player) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Hide attributes if requested
        if (!Boolean.parseBoolean(ctx.text.parseTextToString(player, panelItem.attributes()))) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            try {
                meta.addItemFlags(ItemFlag.valueOf("HIDE_ADDITIONAL_TOOLTIP"));
            } catch (IllegalArgumentException ignored) {}
            try {
                meta.addItemFlags(ItemFlag.valueOf("HIDE_POTION_EFFECTS"));
            } catch (IllegalArgumentException ignored) {}
        }

        // Display name
        String name = panelItem.displayName();
        if (!name.isEmpty()) {
            meta.displayName(ctx.text.parseTextToComponent(player, name));
        }

        // Lore
        List<String> lore = panelItem.lore();
        if (!lore.isEmpty()) {
            List<Component> formattedLore = loreFormatter.format(lore, player);
            meta.lore(formattedLore);
        }

        item.setItemMeta(meta);
        return item;
    }
}
