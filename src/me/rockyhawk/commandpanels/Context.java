package me.rockyhawk.commandpanels;

import me.rockyhawk.commandpanels.builder.inventory.items.utils.CustomHeads;
import me.rockyhawk.commandpanels.commands.MainCommand;
import me.rockyhawk.commandpanels.formatter.Placeholders;
import me.rockyhawk.commandpanels.formatter.language.TextFormatter;
import me.rockyhawk.commandpanels.formatter.data.DataLoader;
import me.rockyhawk.commandpanels.interaction.openpanel.PanelOpenCommand;
import me.rockyhawk.commandpanels.scheduler.SchedulerAdapter;
import me.rockyhawk.commandpanels.scheduler.SchedulerFactory;
import me.rockyhawk.commandpanels.session.PanelSessionManager;
import me.rockyhawk.commandpanels.session.SessionDataUtils;
import me.rockyhawk.commandpanels.session.inventory.generator.GenerateManager;
import me.rockyhawk.commandpanels.session.inventory.listeners.ClickEvents;
import me.rockyhawk.commandpanels.session.inventory.listeners.InventoryEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

public class Context {
    public CommandPanels plugin;
    public SchedulerAdapter scheduler;
    public TextFormatter text;
    public FileHandler fileHandler;
    public PanelOpenCommand panelCommand;
    public DataLoader dataLoader;
    public GenerateManager generator;
    public CustomHeads customHeads;
    public PanelSessionManager sessionManager;

    public Context(CommandPanels pl) {
        plugin = pl;
        init();
    }

    private void init(){
        scheduler = SchedulerFactory.create();
        sessionManager = new PanelSessionManager();
        text = new TextFormatter(this);
        fileHandler = new FileHandler(this);
        panelCommand = new PanelOpenCommand(this);
        dataLoader = new DataLoader(this);
        generator = new GenerateManager(this);
        customHeads = new CustomHeads(this);

        // Register plugin command
        MainCommand mainCommand = new MainCommand(this);
        PluginCommand panelsCmd = plugin.getCommand("panels");
        if (panelsCmd != null) {
            panelsCmd.setExecutor(mainCommand);
            panelsCmd.setTabCompleter(mainCommand);
        }

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryEvents(this), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(panelCommand, plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ClickEvents(this), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new SessionDataUtils(this), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(generator, plugin);

        // Register proxy channels
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");

        // Register PlaceholderAPI (delayed by a tick by being in scheduler)
        scheduler.runGlobal(plugin, () -> {
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                new Placeholders(this).register();
            }
        });
    }
}
