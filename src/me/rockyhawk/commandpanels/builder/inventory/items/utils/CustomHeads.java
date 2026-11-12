package me.rockyhawk.commandpanels.builder.inventory.items.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.rockyhawk.commandpanels.Context;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class CustomHeads {

    private final Context ctx;

    public CustomHeads(Context ctx) {
        this.ctx = ctx;
    }

    public ItemStack getCustomHead(String base64Texture) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (base64Texture == null || base64Texture.isBlank()) return skull;

        if (skull.getItemMeta() instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
            profile.setProperty(new ProfileProperty("textures", base64Texture));
            skullMeta.setPlayerProfile(profile);
            skull.setItemMeta(skullMeta);
        }
        return skull;
    }

    public ItemStack getPlayerHeadSync(String playerName) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (playerName == null || !playerName.matches("^[!-~]{1,16}$")) return skull;

        if (skull.getItemMeta() instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(playerName);
            skullMeta.setPlayerProfile(profile);
            skull.setItemMeta(skullMeta);
        }
        return skull;
    }
}
