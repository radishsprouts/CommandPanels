package me.rockyhawk.commandpanels.builder.inventory.items.itemcomponents;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.builder.inventory.items.ItemComponent;
import me.rockyhawk.commandpanels.session.inventory.PanelItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DamageComponent implements ItemComponent {

    @Override
    public ItemStack apply(Context ctx, ItemStack itemStack, Player player, PanelItem item) {
        if (item.damage().equals("0")) return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;

        if (item.damage().equals("-1")) {
            meta.setUnbreakable(true);
        } else if (meta instanceof Damageable damageable) {
            try {
                damageable.setDamage(Integer.parseInt(ctx.text.parseTextToString(player, item.damage())));
            } catch (NumberFormatException ignored) {}
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
