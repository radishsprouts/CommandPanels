package me.rockyhawk.commandpanels.session;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PanelSessionManager {

    private final Map<UUID, ActiveSession> sessions = new ConcurrentHashMap<>();

    public void openSession(Player player, Panel panel, Inventory inventory) {
        if (player == null) return;
        sessions.put(player.getUniqueId(), new ActiveSession(panel, inventory));
    }

    public void closeSession(Player player) {
        if (player == null) return;
        sessions.remove(player.getUniqueId());
    }

    public ActiveSession getSession(Player player) {
        if (player == null) return null;
        return sessions.get(player.getUniqueId());
    }

    public boolean hasSession(Player player) {
        return player != null && sessions.containsKey(player.getUniqueId());
    }

    public record ActiveSession(Panel panel, Inventory inventory) {}
}
