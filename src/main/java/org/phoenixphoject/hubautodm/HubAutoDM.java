package org.phoenixphoject.hubautodm;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class HubAutoDM extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.addDefault("messages.automessage", "&eChoose the server to start playing");
        config.addDefault("messages.kickmessage", "&cTime is up, you didn't choose a server");
        config.addDefault("messages.title", "&eWelcome to the Hub!");
        config.addDefault("messages.subtitile", "&cPlease choose the server to play!");
        config.addDefault("settings.enabletitile", true);
        config.addDefault("settings.enableautomessage", true);
        config.addDefault("settings.enablekick", true);
        config.addDefault("settings.delay", 100);
        config.addDefault("settings.needtokick", 10);
        config.options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("hadmreload").setExecutor(new HadmCommand());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (getConfig().getBoolean("settings.enabletitile")) {
            title(event);
        }
        if(getConfig().getBoolean("settings.enableautomessage")) {
            Boolean ekick = getConfig().getBoolean("settings.enablekick");
            if(ekick) {
                dmkick(event);
            } else if (!ekick) {
                dm(event);
            }
        }
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

    public void title(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&',  getConfig().getString("messages.title")),ChatColor.translateAlternateColorCodes('&',  getConfig().getString("messages.subtitile")), 10, 100, 10);
            }
        }.runTaskLater(this, 20);
    }

    public void dmkick(PlayerJoinEvent event) {
        new BukkitRunnable() {
            int kick = 0;
            @Override
            public void run() {
                if(kick < getConfig().getInt("settings.needtokick"))
                {
                    sendPlayer(event, getConfig().getString("messages.automessage"));
                } else {
                    event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&',  getConfig().getString("messages.kickmessage")));
                    this.cancel();
                }
                kick++;
            }
        }.runTaskTimer(this, 0, getConfig().getInt("settings.delay"));
    }

    public void dm(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendPlayer(event, getConfig().getString("messages.automessage"));
            }
        }.runTaskTimer(this, 0, getConfig().getInt("settings.delay"));
    }

    public void sendPlayer(PlayerJoinEvent event, String text) {
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',  text));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
