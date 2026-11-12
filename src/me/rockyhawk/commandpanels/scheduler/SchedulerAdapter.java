package me.rockyhawk.commandpanels.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface SchedulerAdapter {
    TaskHandle runAtEntity(Plugin plugin, Player player, Runnable runnable);
    TaskHandle runAtEntityLater(Plugin plugin, Player player, Runnable runnable, long delayTicks);
    TaskHandle runAtEntityTimer(Plugin plugin, Player player, Runnable runnable, long delayTicks, long periodTicks);

    TaskHandle runGlobal(Plugin plugin, Runnable runnable);
    TaskHandle runGlobalLater(Plugin plugin, Runnable runnable, long delayTicks);

    TaskHandle runAsync(Plugin plugin, Runnable runnable);
    TaskHandle runAsyncLater(Plugin plugin, Runnable runnable, long delayTicks);

    boolean isFolia();
}
