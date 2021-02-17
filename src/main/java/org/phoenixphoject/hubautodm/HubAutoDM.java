package org.phoenixphoject.hubautodm;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.ReloadCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public final class HubAutoDM extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.addDefault("automessage", "Выбери сервер для игры");
        config.addDefault("kickmessage", "Вы не успели выбрать сервер");
        config.addDefault("delay", 100);
        config.addDefault("needtokick", 10);
        config.options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("hadmreload").setExecutor(new HadmCommand());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            int kick = 0;
            @Override
            public void run() {
                if(kick < getConfig().getInt("needtokick"))
                {
                    event.getPlayer().sendMessage(getConfig().getString("automessage"));
                } else {
                    event.getPlayer().kickPlayer(getConfig().getString("kickmessage"));
                    this.cancel();
                }
                kick++;
            }
        }.runTaskTimer(this, 0, getConfig().getInt("delay"));
    }

    public class HadmCommand implements CommandExecutor {

        // This method is called, when somebody uses our command
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            reloadConfig();
            getConfig();
            return false;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
