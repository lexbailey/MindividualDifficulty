
package com.mindividual.plugin;

import java.lang.Math;
import java.util.HashMap;
import java.util.Collection;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private final PlayerListener playerListener = new PlayerListener(this);
    private final File diffFile = new File("mindividual.properties");

    Properties difficulties;

    @Override
    public void onDisable() {
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        getLogger().info("Disabling Mindivdual Difficulty");
    }

    @Override
    public void onEnable() {
        difficulties = new Properties();
        if (!diffFile.exists()){
            try{
                FileOutputStream defaultProps = new FileOutputStream(diffFile);
                defaultProps.write("min=easy\n".getBytes("UTF-8"));
                defaultProps.close();
            }
            catch (Exception e){
                getLogger().severe("Unable to write default individual difficulty settings. Message:" + e.toString());
            }
        }
        try{
            difficulties.load(new FileInputStream(diffFile));
        }
        catch (Exception e){
            getLogger().severe("Unable to load individual difficulty settings. Message:" + e.toString());
        }

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);

        // Register our commands
        getCommand("mydifficulty").setExecutor(new DifficultyCommand(this));

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    private int diffStringToInt(String difficulty){
        if (difficulty.equals("peaceful")){return 1;}
        if (difficulty.equals("easy")){return 2;}
        if (difficulty.equals("normal")){return 3;}
        if (difficulty.equals("hard")){return 4;}
        return -1;
    }

    private String intToDiffString(int difficulty){
        if (difficulty == 1){return "peaceful";}
        if (difficulty == 2){return "easy";}
        if (difficulty == 3){return "normal";}
        if (difficulty == 4){return "hard";}
        return "Invalid difficulty level";
    }

    private Difficulty intToDifficulty(int difficulty){
        if (difficulty == 1){return Difficulty.PEACEFUL;}
        if (difficulty == 2){return Difficulty.EASY;}
        if (difficulty == 3){return Difficulty.NORMAL;}
        if (difficulty == 4){return Difficulty.HARD;}
        return Difficulty.PEACEFUL;
    }

    public boolean setPlayerDifficulty(String player, String difficulty){
        if (diffStringToInt(difficulty) < 0){
            return false;
        }
        difficulties.setProperty("player_"+player, difficulty);
        try{
            FileOutputStream diffOut = new FileOutputStream(diffFile);
            difficulties.store(diffOut, null);
        }
        catch (Exception e){
            getLogger().severe("Unable to save individual difficulty settings. Message:" + e.toString());
        }
        return true;
    }

    public String getPlayerDifficulty(String player){
        String defaultMin = difficulties.getProperty("min", "easy"); 
        return difficulties.getProperty("player_" + player, defaultMin);
    }

    public void reEvaluateDifficulty(){
        Collection<? extends Player> online = Bukkit.getOnlinePlayers();
        if (online.size() > 0){
            String defaultMin = difficulties.getProperty("min", "easy"); 
            int newLevel = 4;
            World w = null;
            for (Player p: online){
                getLogger().info( "Check level for " + p.getName() );
                int playerMax = diffStringToInt(difficulties.getProperty("player_" + p.getName(), defaultMin));
                if (playerMax < 0){
                    p.sendMessage("Warning! Your difficulty settings is corrupt. Update it with the /mydifficulty command.");
                }
                else{
                    newLevel = Math.min(playerMax, newLevel);
                }
                w = p.getWorld(); // Lazy hacky way to get a reference to 'the' world
            }
            getLogger().info( "New difficulty level is " + intToDiffString(newLevel) );
            if (w != null){
                w.setDifficulty(intToDifficulty(newLevel));
                Bukkit.broadcastMessage("Difficulty setting is now '" + intToDiffString(newLevel) + "'");
            }
            else{
                getLogger().severe("Failed to set difficulty, player in invalid world");
                Bukkit.broadcastMessage("Error updating difficulty setting.");
            }
        }
    }

}
