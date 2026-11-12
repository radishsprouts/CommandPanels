package me.rockyhawk.commandpanels.interaction.openpanel;

import me.rockyhawk.commandpanels.Context;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

public class CommandRegister {
    private final Context ctx;
    private final CommandMap commandMap;

    public CommandRegister(Context ctx) {
        this.ctx = ctx;
        CommandMap map = null;
        try {
            map = Bukkit.getCommandMap();
        } catch (Throwable ignored) {}
        this.commandMap = map;
    }

    // Commands will register on panel load
    // Server will need reload for changes
    public void registerPanelCommand(String command) {
        if (commandMap == null || command.isEmpty()) return;

        // Check if already registered
        if (Bukkit.getPluginCommand(command) != null) return;
        if (commandMap.getCommand(command) != null) return;

        Command customCmd = new Command(command) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                // Handled in PanelOpenCommand via PlayerCommandPreprocessEvent
                return true;
            }
        };
        customCmd.setDescription("CommandPanels custom panel command");

        commandMap.register(ctx.plugin.getName(), customCmd);
    }
}
