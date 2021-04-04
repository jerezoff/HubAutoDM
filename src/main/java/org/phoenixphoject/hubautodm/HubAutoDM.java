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
        config.addDefault("settings.automessage.text", "&eChoose the server to start playing");
        config.addDefault("settings.automessage.enable", true);
        config.addDefault("settings.automessage.delay", 100);
        config.addDefault("settings.kick.message", "&cTime is up, you didn't choose a server");
        config.addDefault("settings.kick.needmessages", 10);
        config.addDefault("settings.kick.enable", true);
        config.addDefault("settings.title.title", "&eWelcome to the Hub!");
        config.addDefault("settings.title.subtitile", "&cPlease choose the server to play!");
        config.addDefault("settings.title.enable", true);
        config.addDefault("settings.playsound.volume", "0.1");
        config.addDefault("settings.playsound.sound", "music.end");
        config.addDefault("settings.playsound.enable", true);
        config.addDefault("settings.daylightspeed.increaser", 1);
        config.addDefault("settings.daylightspeed.enable", true);
        config.addDefault("settings.daylightspeed.world", "world");
        config.options().copyDefaults(true);
        saveConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (getConfig().getBoolean("settings.title.enable")) {
            title(event);
        }
        if(getConfig().getBoolean("settings.automessage.enable")) {
            if(getConfig().getBoolean("settings.kick.enable")) {
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
                    long currtime = Bukkit.getWorld(getConfig().getString("settings.daylightspeed.world")).getTime();
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
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',  "&a✔ &fHADM reloaded correctly!"));
            return true;
        }
    }

    public void title(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', getConfig().getString("settings.title.title")),ChatColor.translateAlternateColorCodes('&',  getConfig().getString("settings.title.subtitile")), 10, 100, 10);

            }
        }.runTaskLater(this, 20);
    }

    public void sound(PlayerJoinEvent event) {
        Sound[] SOUNDS = Sound.values();
        for (Sound sound : SOUNDS) {
            event.getPlayer().stopSound(sound);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Player p = event.getPlayer();
                Location loc = p.getLocation();
                float volume = Float.parseFloat(getConfig().getString("settings.playsound.volume"));
                float pitch = 1F;
                p.playSound(loc, getConfig().getString("settings.playsound.sound"), volume, pitch);
            }
        }.runTaskLater(this, 20);
    }

    public void dmkick(PlayerJoinEvent event) {
        new BukkitRunnable() {
            int kick = 0;
            @Override
            public void run() {
                if(kick < getConfig().getInt("settings.kick.needmessages"))
                {
                    sendPlayer(event, getConfig().getString("settings.automessage.text"));
                } else {
                    event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&',  getConfig().getString("settings.kick.message")));
                    this.cancel();
                }
                kick++;
            }
        }.runTaskTimer(this, 0, getConfig().getInt("settings.automessage.delay"));
    }

    public void dm(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendPlayer(event, getConfig().getString("settings.automessage.text"));
            }
        }.runTaskTimer(this, 0, getConfig().getInt("settings.automessage.delay"));
    }

    public void sendPlayer(PlayerJoinEvent event, String text) {
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',  text));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
