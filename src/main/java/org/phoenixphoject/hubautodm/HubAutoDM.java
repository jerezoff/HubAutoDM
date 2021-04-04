package org.phoenixphoject.hubautodm;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;

public final class HubAutoDM extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveconfig();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("hadmreload").setExecutor(new HadmCommand());

        timeincreaser();
    }

    public void saveconfig() {
        FileConfiguration config = this.getConfig();
        config.addDefault("messages.automessage", "&eChoose the server to start playing");
        config.addDefault("messages.kickmessage", "&cTime is up, you didn't choose a server");
        config.addDefault("messages.title.title", "&eWelcome to the Hub!");
        config.addDefault("messages.title.subtitile", "&cPlease choose the server to play!");
        config.addDefault("settings.enabletitile", true);
        config.addDefault("settings.enableautomessage", true);
        config.addDefault("settings.enablekick", true);
        config.addDefault("settings.delay", 100);
        config.addDefault("settings.needtokick", 10);
        config.addDefault("settings.playsound.volume", "0.1");
        config.addDefault("settings.playsound.sound", "music.end");
        config.addDefault("settings.playsound.enable", true);
        config.addDefault("settings.daylightspeed.increaser", 1);
        config.addDefault("settings.daylightspeed.enable", true);
        config.options().copyDefaults(true);
        saveConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (getConfig().getBoolean("settings.enabletitile")) {
            title(event);
        }
        if(getConfig().getBoolean("settings.enableautomessage")) {
            if(getConfig().getBoolean("settings.enablekick")) {
                dmkick(event);
            } else {
                dm(event);
            }
        }

        if (getConfig().getBoolean("settings.playsound.enable")) { sound(event); }
    }

    public void timeincreaser() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(getConfig().getBoolean("settings.daylightspeed.enable")) {
                    long currtime = Bukkit.getWorld("world").getTime();
                    long increaser = getConfig().getLong("settings.daylightspeed.increaser");
                    Bukkit.getWorld("world").setTime(currtime + increaser);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 1, 0);
    }

    public class HadmCommand implements CommandExecutor {
        // This method is called, when somebody uses our command
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            Boolean daylight = getConfig().getBoolean("settings.daylightspeed.enable");
            reloadConfig();
            saveconfig();
            Boolean newdaylight = getConfig().getBoolean("settings.daylightspeed.enable");
            if(daylight != newdaylight) {
                timeincreaser();
            }
            return false;
        }
    }

    public void title(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.title.title")),ChatColor.translateAlternateColorCodes('&',  getConfig().getString("messages.title.subtitile")), 10, 100, 10);

            }
        }.runTaskLater(this, 20);
    }

    public void sound(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player p = event.getPlayer();
                Location loc = p.getLocation();
                float volume = Float.parseFloat(getConfig().getString("settings.playsound.volume"));
                float pitch = 1F;

                Sound[] SOUNDS = Sound.values();
                for (Sound sound : SOUNDS) {
                    p.stopSound(sound);
                }

                p.playSound(loc, getConfig().getString("settings.playsound.sound"), volume, pitch);
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
