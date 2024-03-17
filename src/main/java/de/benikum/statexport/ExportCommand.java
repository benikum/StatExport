package de.benikum.statexport;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExportCommand implements CommandExecutor {
    Main mainInstance;
    
    public ExportCommand(Main mainInstance) {
        this.mainInstance = mainInstance;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        // valid sender
        if (args.length == 0) return false;
        // some parameters given
        Player player = (Player) sender;
        String cmd0 = args[0].toLowerCase();
        
        if (args.length == 1) {
            if (cmd0.equals("export")) {
                mainInstance.exportStatsTXT();
                player.sendMessage("§aExported all playerstats");
            }
        } else {
            String cmd1 = args[1].toUpperCase();
            if (cmd0.equals("add")) {
                Statistic statistic;
                try {
                    statistic = Statistic.valueOf(cmd1);
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cInvalid statistic name");
                    return true;
                }
                Statistic.Type sType = statistic.getType();
                if (sType == Statistic.Type.UNTYPED) {
                    mainInstance.addToConfig(cmd1);
                    player.sendMessage("§aAdded statistic: " + cmd1);
                    return true;
                } else if (args.length == 2) {
                    player.sendMessage("§cMust supply additional parameter for this statistic");
                    return true;
                }
                
                String cmd2 = args[2].toUpperCase();
                if (sType == Statistic.Type.ENTITY) {
                    EntityType entityType;
                    try {
                        entityType = EntityType.valueOf(cmd2);
                        mainInstance.addToConfig(cmd1 + "." + entityType.name());
                        player.sendMessage("§aAdded statistic: " + cmd1 + "." + entityType.name());
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("§cStatistic EntityType parameter Invalid");
                        return true;
                    }
                } else {
                    Material material;
                    try {
                        material = Material.valueOf(cmd2);
                        mainInstance.addToConfig(cmd1 + "." + material.name());
                        player.sendMessage("§aAdded statistic: " + cmd1 + "." + material.name());
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("§cStatistic Material parameter Invalid");
                        return true;
                    }
                }
            } else if (cmd0.equals("remove")) {
                mainInstance.removeFromConfig(cmd1);
                player.sendMessage("§aRemoved statistic: " + cmd1);
            }
        }
        return true;
    }
}
