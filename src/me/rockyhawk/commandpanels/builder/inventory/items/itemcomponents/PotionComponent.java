package me.rockyhawk.commandpanels.builder.inventory.items.itemcomponents;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.builder.inventory.items.ItemComponent;
import me.rockyhawk.commandpanels.session.inventory.PanelItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.List;

public class PotionComponent implements ItemComponent {

    @Override
    public ItemStack apply(Context ctx, ItemStack itemStack, Player player, PanelItem item) {
        if (item.potion() == null) return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof PotionMeta potionMeta) {
            try {
                String[] effectType = ctx.text.parseTextToString(player, item.potion()).split("\\s+");
                PotionType newData = PotionType.valueOf(effectType[0].toUpperCase());
                List<PotionEffect> effects = newData.getPotionEffects();
                if (!effects.isEmpty()) {
                    potionMeta.addCustomEffect(effects.get(0), true);
                    itemStack.setItemMeta(potionMeta);
                }
            } catch (Exception ignored) {}
        }

        return itemStack;
    }
}
