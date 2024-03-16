package de.benikum.statexport;

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
        getCommand("csvexport").setExecutor(new StatCommand(this));
        getCommand("csvexport").setTabCompleter(new StatTabCompleter(this));
    }
    
    public void exportStatsTXT() {
        try {
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                File file = new File(getDataFolder(), offlinePlayer.getName() + ".txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter writer = new FileWriter(file);
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
                writer.close();
            }
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
