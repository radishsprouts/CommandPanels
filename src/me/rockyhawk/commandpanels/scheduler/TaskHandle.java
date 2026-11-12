package me.rockyhawk.commandpanels.scheduler;

public interface TaskHandle {
    void cancel();
    boolean isCancelled();
}
