package me.rockyhawk.commandpanels.builder.inventory.items.itemcomponents;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.builder.inventory.items.ItemComponent;
import me.rockyhawk.commandpanels.session.inventory.PanelItem;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BannerComponent implements ItemComponent {

    @Override
    public ItemStack apply(Context ctx, ItemStack itemStack, Player player, PanelItem item) {
        if (item.banner().isEmpty()) return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof BannerMeta bannerMeta) {
            List<Pattern> patterns = new ArrayList<>();
            for (String temp : item.banner()) {
                temp = ctx.text.parseTextToString(player, temp);
                String[] dyePattern = temp.split(",");
                if (dyePattern.length < 2) continue;

                try {
                    DyeColor color = DyeColor.valueOf(dyePattern[0].trim().toUpperCase());
                    String typeStr = dyePattern[1].trim().toUpperCase();
                    PatternType type = PatternType.getByIdentifier(dyePattern[1].trim().toLowerCase());
                    if (type == null) {
                        try {
                            type = PatternType.valueOf(typeStr);
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (type != null) {
                        patterns.add(new Pattern(color, type));
                    }
                } catch (Exception ignored) {}
            }

            bannerMeta.setPatterns(patterns);
            itemStack.setItemMeta(bannerMeta);
        }

        return itemStack;
    }
}
