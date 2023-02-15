package cz.neumimto.townycreative;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.stream.Collectors;

public final class TownyCreative extends JavaPlugin {

    public static Logger logger;

    public TownyCreative() {
        super();
    }

    protected TownyCreative(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public static void log(String c) {
        logger.info(c);
    }

    @Override
    public void onEnable() {
        logger = getSLF4JLogger();
        PaperCommandManager manager = new PaperCommandManager(this);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");
        GamemodeService gamemodeService = new GamemodeService();
        gamemodeService.initConfig(file);

        manager.getCommandCompletions().registerAsyncCompletion("nation",
                c -> TownyUniverse.getInstance().getNations().stream().map(Nation::getName).collect(Collectors.toList()));

        manager.getCommandContexts().registerContext(Nation.class, c -> {
            String name = c.popFirstArg();
            Nation nation = TownyUniverse.getInstance().getNation(name);
            if (nation == null) {
                throw new InvalidCommandArgument("Unknown nation");
            } else {
                return nation;
            }
        });

        manager.registerCommand(new Commands(gamemodeService));

        Bukkit.getServer().getPluginManager().registerEvents(new BlockListener(gamemodeService), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
