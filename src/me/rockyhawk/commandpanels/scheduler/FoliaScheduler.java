package me.rockyhawk.commandpanels.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class FoliaScheduler implements SchedulerAdapter {

    @Override
    public TaskHandle runAtEntity(Plugin plugin, Player player, Runnable runnable) {
        ScheduledTask task = player.getScheduler().run(plugin, t -> runnable.run(), null);
        return wrap(task);
    }

    @Override
    public TaskHandle runAtEntityLater(Plugin plugin, Player player, Runnable runnable, long delayTicks) {
        ScheduledTask task = player.getScheduler().runDelayed(plugin, t -> runnable.run(), null, delayTicks);
        return wrap(task);
    }

    @Override
    public TaskHandle runAtEntityTimer(Plugin plugin, Player player, Runnable runnable, long delayTicks, long periodTicks) {
        ScheduledTask task = player.getScheduler().runAtFixedRate(plugin, t -> runnable.run(), null, delayTicks, periodTicks);
        return wrap(task);
    }

    @Override
    public TaskHandle runGlobal(Plugin plugin, Runnable runnable) {
        ScheduledTask task = Bukkit.getGlobalRegionScheduler().run(plugin, t -> runnable.run());
        return wrap(task);
    }

    @Override
    public TaskHandle runGlobalLater(Plugin plugin, Runnable runnable, long delayTicks) {
        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> runnable.run(), delayTicks);
        return wrap(task);
    }

    @Override
    public TaskHandle runAsync(Plugin plugin, Runnable runnable) {
        ScheduledTask task = Bukkit.getAsyncScheduler().runNow(plugin, t -> runnable.run());
        return wrap(task);
    }

    @Override
    public TaskHandle runAsyncLater(Plugin plugin, Runnable runnable, long delayTicks) {
        ScheduledTask task = Bukkit.getAsyncScheduler().runDelayed(plugin, t -> runnable.run(), delayTicks * 50L, TimeUnit.MILLISECONDS);
        return wrap(task);
    }

    @Override
    public boolean isFolia() {
        return true;
    }

    private TaskHandle wrap(ScheduledTask task) {
        return new TaskHandle() {
            @Override
            public void cancel() {
                if (task != null) task.cancel();
            }

            @Override
            public boolean isCancelled() {
                return task == null || task.isCancelled();
            }
        };
    }
}
