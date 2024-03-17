package de.benikum.statexport;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class Main extends JavaPlugin {
    private final String configPath = "stats";
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("statexport").setExecutor(new ExportCommand(this));
        getCommand("statexport").setTabCompleter(new ExportTabCompleter(this));
        getCommand("statleaderboard").setExecutor(new LeaderboardCommand(this));
        getCommand("statleaderboard").setTabCompleter(new LeaderboardTabCompleter());
    }
    
    public void printLeaderboard(Statistic statistic) {
        Map<String, Integer> playerNameIntegerMap = new HashMap<>();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            playerNameIntegerMap.put(offlinePlayer.getName(), offlinePlayer.getStatistic(statistic));
        }
        compileLeaderboard(statistic.name(), playerNameIntegerMap);
    }
    public void printLeaderboard(Statistic statistic, EntityType entityType) {
        Map<String, Integer> playerNameIntegerMap = new HashMap<>();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            playerNameIntegerMap.put(offlinePlayer.getName(), offlinePlayer.getStatistic(statistic, entityType));
        }
        compileLeaderboard(statistic.name() + "." + entityType.name(), playerNameIntegerMap);
    }
    public void printLeaderboard(Statistic statistic, Material material) {
        Map<String, Integer> playerNameIntegerMap = new HashMap<>();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            playerNameIntegerMap.put(offlinePlayer.getName(), offlinePlayer.getStatistic(statistic, material));
        }
        compileLeaderboard(statistic.name() + "." + material.name(), playerNameIntegerMap);
    }
    
    private void compileLeaderboard(String statistic, Map<String, Integer> playerIntegerMap) {
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(playerIntegerMap.entrySet());
        
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        
        int index = 0;
        
        StringBuilder lineString = new StringBuilder();
        for (int i = 0; i < (statistic.length() + 6); i++) {
            lineString.append("-");
        }
        
        Bukkit.broadcast(Component.text("§9§l" + lineString));
        Bukkit.broadcast(Component.text("§9§l---§c§l" + statistic + "§9§l---"));
        Bukkit.broadcast(Component.text("§9§l" + lineString));
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            index++;
            String name = entry.getKey();
            int value = entry.getValue();
            String colorprefix = "";
            if (index == 1) colorprefix = "§6";
            else if (index == 2) colorprefix = "§7";
            else if (index == 3) colorprefix = "§4";
            Bukkit.broadcast(Component.text(colorprefix + index + ". " + name + " : " + value));
        }
        Bukkit.broadcast(Component.text("§9§l" + lineString));
    }
    
    public void exportStatsTXT() {
        try {
            File file = new File(getDataFolder(), "player_stats.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                UUID playerId = offlinePlayer.getUniqueId();
                writer.write("Player: " + offlinePlayer.getName() + " (UUID: " + playerId + ")\n\n");
                
                for (String statisticName : getStatsInConfig()) {
                    String[] statisticNameArray = statisticName.split("\\.");
                    Statistic statistic;
                    try {
                        statistic = Statistic.valueOf(statisticNameArray[0]);
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                    Statistic.Type sType = statistic.getType();
                    if (sType == Statistic.Type.UNTYPED) {
                        writer.write(statistic.name() + ": " + offlinePlayer.getStatistic(statistic) + "\n");
                        continue;
                    } else if (statisticNameArray.length == 1) continue;
                    
                    if (sType == Statistic.Type.ENTITY) {
                        EntityType entityType;
                        try {
                            entityType = EntityType.valueOf(statisticNameArray[1]);
                            writer.write(statistic.name() + "." + entityType.name() + ": " + offlinePlayer.getStatistic(statistic, entityType) + "\n");
                        } catch (IllegalArgumentException ignored) {}
                    } else {
                        Material material;
                        try {
                            material = Material.valueOf(statisticNameArray[1]);
                            writer.write(statistic.name() + "." + material.name() + ": " + offlinePlayer.getStatistic(statistic, material) + "\n");
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
                
                writer.write("\n");
            }
            
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addToConfig(String value) {
        if (getStatsInConfig().contains(value)) return;
        FileConfiguration config = getConfig();
        if (!config.isList(configPath)) {
            config.set(configPath, new ArrayList<>());
        }
        List<String> list = config.getStringList(configPath);
        list.add(value);
        Collections.sort(list);
        config.set(configPath, list);
        saveConfig();
    }
    
    public void removeFromConfig(String value) {
        FileConfiguration config = getConfig();
        if (!config.isList(configPath)) {
            return;
        }
        List<String> list = config.getStringList(configPath);
        list.remove(value);
        config.set(configPath, list);
        saveConfig();
    }
    
    public List<String> getStatsInConfig() {
        FileConfiguration config = getConfig();
        if (!config.isList(configPath)) return new ArrayList<>();
        
        return config.getStringList(configPath);
    }
}
