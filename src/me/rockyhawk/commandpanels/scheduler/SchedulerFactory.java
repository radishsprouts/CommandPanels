package me.rockyhawk.commandpanels.scheduler;

import me.rockyhawk.commandpanels.util.ServerVersion;

public class SchedulerFactory {
    public static SchedulerAdapter create() {
        if (ServerVersion.isFolia()) {
            return new FoliaScheduler();
        } else {
            return new BukkitScheduler();
        }
    }
}
