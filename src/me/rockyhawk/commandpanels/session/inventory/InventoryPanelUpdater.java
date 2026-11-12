package me.rockyhawk.commandpanels.session.inventory;

import me.rockyhawk.commandpanels.scheduler.TaskHandle;
import org.bukkit.persistence.PersistentDataContainer;
import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.builder.inventory.InventoryPanelBuilder;
import me.rockyhawk.commandpanels.builder.inventory.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

public class InventoryPanelUpdater {

    private TaskHandle heartbeatTask;
    private TaskHandle baseTask;

    // The observer values
    private final Map<String, Boolean> lastObservedPermStates = new HashMap<>();
    private final Map<String, Map<String, String>> lastObservedVisualValues = new HashMap<>();
    private final Map<String, String> lastObservedConditionValues = new HashMap<>();

    // Condition observer checks one placeholder per heartbeat run, cycling through the list
    private String[] conditionNodes = new String[0];
    private int conditionIndex = 0;

    // shared, built once per panel-open in start()
    private NamespacedKey itemIdKey;
    private NamespacedKey baseIdKey;
    private NamespacedKey fillItemKey;
    private ItemBuilder itemBuilder;

    public void start(Context ctx, Player p, InventoryPanel panel) {
        stop(); // always clean slate

        itemIdKey = new NamespacedKey(ctx.plugin, "item_id");
        baseIdKey = new NamespacedKey(ctx.plugin, "base_item_id");
        fillItemKey = new NamespacedKey(ctx.plugin, "fill_item");
        itemBuilder = new ItemBuilder(ctx, new InventoryPanelBuilder(ctx, p));

        conditionNodes = panel.getObserver().getConditionPlaceholders().toArray(new String[0]);
        conditionIndex = 0;

        startHeartbeat(ctx, p, panel);

        int updateInterval = parseDelay(panel.getUpdateInterval());
        if (updateInterval > 0) {
            startBase(ctx, p, panel, updateInterval);
        }
    }

    // Check the panel instance is still open
    private boolean stillOpen(Player p, InventoryPanel panel) {
        InventoryHolder holder = p.getOpenInventory().getTopInventory().getHolder();
        return holder instanceof InventoryPanel && holder == panel;
    }

    // permission and condition observer, runs fast since it is cheap to run
    private void startHeartbeat(Context ctx, Player p, InventoryPanel panel) {
        heartbeatTask = ctx.scheduler.runAtEntityTimer(ctx.plugin, p, () -> {
            boolean refresh = false;

            if (ctx.fileHandler.config.getBoolean("permission-observer")) {
                refresh = checkSet(panel.getObserver().getPerms(), lastObservedPermStates, p::hasPermission);
            }

            if (!refresh && conditionNodes.length > 0 && ctx.fileHandler.config.getBoolean("condition-observer")) {
                String node = conditionNodes[conditionIndex];
                conditionIndex = (conditionIndex + 1) % conditionNodes.length;

                String current = ctx.text.applyPlaceholders(p, node);
                String previous = lastObservedConditionValues.put(node, current);
                refresh = previous != null && !previous.equals(current);
            }

            if (!stillOpen(p, panel)) { stop(); return; }
            if (refresh) panel.open(ctx, p, false);
        }, 2, 2);
    }

    // For checking if observed placeholders have changed or not
    private <T> boolean checkSet(HashSet<String> keys, Map<String, T> cache, Function<String, T> resolver) {
        for (String key : keys) {
            T current = resolver.apply(key);
            T previous = cache.put(key, current);
            if (previous != null && !previous.equals(current)) return true;
        }
        return false;
    }

    // The logic that actually changes items for base updater
    private void applyItem(Inventory inv, int slot, InventoryPanel panel, PanelItem panelItem, String baseItemId) {
        ItemStack newItem = itemBuilder.buildItem(panel, panelItem);
        newItem.editPersistentDataContainer(c -> c.set(baseIdKey, PersistentDataType.STRING, baseItemId));
        inv.setItem(slot, newItem);
    }

    private int parseDelay(String delayStr) {
        if (delayStr != null && delayStr.matches("\\d+")) {
            return Integer.parseInt(delayStr);
        }
        return 20; // default
    }

    // Iterator for the items in the base updater
    private void forEachManagedItem(Inventory inv, InventoryPanel panel, SlotVisitor visitor) {
        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack item = inv.getItem(slot);
            if (item == null || item.getType().isAir()) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (!container.has(itemIdKey, PersistentDataType.STRING) ||
                    container.has(fillItemKey, PersistentDataType.STRING)) continue;

            String itemId = container.get(itemIdKey, PersistentDataType.STRING);
            String baseItemId = container.get(baseIdKey, PersistentDataType.STRING);
            PanelItem panelItem = panel.getItems().get(itemId);

            visitor.visit(slot, itemId, baseItemId, panelItem);
        }
    }
    @FunctionalInterface
    private interface SlotVisitor {
        void visit(int slot, String itemId, String baseItemId, PanelItem panelItem);
    }

    // the general updater for placeholder changes which also handles animations
    private void startBase(Context ctx, Player p, InventoryPanel panel, int updateInterval) {
        baseTask = ctx.scheduler.runAtEntityTimer(ctx.plugin, p, () -> {
            if (!stillOpen(p, panel)) { stopBase(); return; }

            Inventory inv = p.getOpenInventory().getTopInventory();

            forEachManagedItem(inv, panel, (slot, itemId, baseItemId, panelItem) -> {
                PanelItem toApply = null;
                if (!panelItem.animate().isEmpty()) {
                    // there is an animation frame configured
                    PanelItem animateItem = panel.getItems().get(panelItem.animate());
                    toApply = (animateItem != null) ? animateItem : panelItem;
                } else if (!baseItemId.equals(itemId)) {
                    // no animation, but currently showing a non-base frame, revert to base
                    toApply = panel.getItems().get(baseItemId);
                } else {
                    // no animation, check for any placeholder changes on item
                    Map<String, String> itemCache = lastObservedVisualValues.computeIfAbsent(itemId, k -> new HashMap<>());
                    if (checkSet(panel.getObserver().getVisualPlaceholders(itemId), itemCache,
                            node -> ctx.text.applyPlaceholders(p, node))) {
                        toApply = panelItem;
                    }
                }

                if(toApply != null) applyItem(inv, slot, panel, toApply, baseItemId);
            });
        }, updateInterval, updateInterval);
    }

    public void stop() {
        stopHeartbeat();
        stopBase();
    }

    private void stopHeartbeat() {
        if (heartbeatTask != null) { heartbeatTask.cancel(); heartbeatTask = null; }
    }

    private void stopBase() {
        if (baseTask != null) { baseTask.cancel(); baseTask = null; }
    }
}