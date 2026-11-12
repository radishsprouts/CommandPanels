package me.rockyhawk.commandpanels.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitScheduler implements SchedulerAdapter {

    @Override
    public TaskHandle runAtEntity(Plugin plugin, Player player, Runnable runnable) {
        BukkitTask task = Bukkit.getScheduler().runTask(plugin, runnable);
        return wrap(task);
    }

    @Override
    public TaskHandle runAtEntityLater(Plugin plugin, Player player, Runnable runnable, long delayTicks) {
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
        return wrap(task);
    }

    @Override
    public TaskHandle runAtEntityTimer(Plugin plugin, Player player, Runnable runnable, long delayTicks, long periodTicks) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks);
        return wrap(task);
    }

    @Override
    public TaskHandle runGlobal(Plugin plugin, Runnable runnable) {
        BukkitTask task = Bukkit.getScheduler().runTask(plugin, runnable);
        return wrap(task);
    }

    @Override
    public TaskHandle runGlobalLater(Plugin plugin, Runnable runnable, long delayTicks) {
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
        return wrap(task);
    }

    @Override
    public TaskHandle runAsync(Plugin plugin, Runnable runnable) {
        BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        return wrap(task);
    }

    @Override
    public TaskHandle runAsyncLater(Plugin plugin, Runnable runnable, long delayTicks) {
        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayTicks);
        return wrap(task);
    }

    @Override
    public boolean isFolia() {
        return false;
    }

    private TaskHandle wrap(BukkitTask task) {
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
