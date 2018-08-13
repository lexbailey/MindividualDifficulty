
package com.mindividual.plugin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
//import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final Plugin plugin;

    public PlayerListener(Plugin instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getLogger().info(player.getName() + " joined the server, checking difficulty setting.");
        String difficulty = plugin.getPlayerDifficulty(player.getName());
        player.sendMessage("Your difficulty setting is '" + difficulty + "'. You can change this with /mydifficulty");
        plugin.reEvaluateDifficulty();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getLogger().info(event.getPlayer().getName() + " left the server, checking difficulty setting.");
        plugin.reEvaluateDifficulty();
    }
/*
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.isDebugging(event.getPlayer())) {
            Location from = event.getFrom();
            Location to = event.getTo();

            plugin.getLogger().info(String.format("From %.2f,%.2f,%.2f to %.2f,%.2f,%.2f", from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ()));
        }
    }
*/
}
