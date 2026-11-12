package me.rockyhawk.commandpanels.commands.subcommands;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.commands.SubCommand;
import me.rockyhawk.commandpanels.formatter.language.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "commandpanels.command.reload";
    }

    @Override
    public boolean execute(Context ctx, CommandSender sender, String[] args) {
        ctx.scheduler.runAsync(ctx.plugin, () -> {
            ctx.fileHandler.updateConfigFiles();
            ctx.fileHandler.reloadPanels();
            ctx.text.lang.reloadTranslations();
            ctx.scheduler.runGlobal(ctx.plugin, () -> ctx.text.sendInfo(sender, Message.PLUGIN_RELOADED));
        });
        return true;
    }
}
