
package com.mindividual.plugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DifficultyCommand implements CommandExecutor {
    private final Plugin plugin;

    public DifficultyCommand(Plugin instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (split.length != 1) {
            player.sendMessage("Usage: /mydifficulty <level>");
            player.sendMessage("Usage: where <level> is one of...");
            player.sendMessage("Usage: peaceful, easy, normal, hard");
            return false;
        } else
        {
            String level = split[0];
            
            if (plugin.setPlayerDifficulty(player.getName(), level)){
                player.sendMessage("Your difficuly level has been set to " + level);
                plugin.reEvaluateDifficulty();
            }
            else {
                player.sendMessage("Invalid diffivulty level. see '/mydifficulty' for help.");
            }
            return true;
        }
    }
}
