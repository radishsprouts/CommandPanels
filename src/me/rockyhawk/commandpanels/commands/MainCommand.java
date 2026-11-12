package me.rockyhawk.commandpanels.commands;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.commands.subcommands.*;
import me.rockyhawk.commandpanels.formatter.language.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final Context ctx;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public MainCommand(Context ctx) {
        this.ctx = ctx;
        registerSubCommands();
    }

    private void registerSubCommands() {
        register(new ReloadCommand());
        register(new OpenCommand());
        register(new GenerateCommand());
        register(new HelpCommand());
        register(new VersionCommand());
        register(new DataCommand());
    }

    private void register(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("commandpanels.command")) {
            ctx.text.sendError(sender, Message.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            ctx.text.sendError(sender, Message.COMMAND_SUBCOMMAND_HELP);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            ctx.text.sendError(sender, Message.COMMAND_UNKNOWN_SUBCOMMAND);
            return true;
        }

        if (subCommand.getPermission() != null && !subCommand.getPermission().isEmpty() && !sender.hasPermission(subCommand.getPermission())) {
            ctx.text.sendError(sender, Message.COMMAND_NO_PERMISSION);
            return true;
        }

        // Pass the remaining args after the subcommand name
        String[] subArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
        subCommand.execute(ctx, sender, subArgs);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        TabComplete suggestions = new TabComplete(ctx);
        Collection<String> result = suggestions.onTabComplete(sender, args);
        return result != null ? new ArrayList<>(result) : Collections.emptyList();
    }
}
