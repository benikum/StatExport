package de.benikum.statexport;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaderboardCommand implements CommandExecutor {
    Main mainInstance;
    
    public LeaderboardCommand(Main mainInstance) {
        this.mainInstance = mainInstance;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (args.length == 0) return false;
        Player player = (Player) sender;
        
        String cmd0 = args[0].toUpperCase();
        Statistic statistic;
        try {
            statistic = Statistic.valueOf(cmd0);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cInvalid statistic name");
            return true;
        }
        Statistic.Type sType = statistic.getType();
        if (sType == Statistic.Type.UNTYPED) {
            mainInstance.printLeaderboard(statistic);
            return true;
        } else if (args.length == 1) {
            player.sendMessage("§cMust supply additional parameter for this statistic");
            return true;
        }
        String cmd1 = args[1].toUpperCase();
        if (sType == Statistic.Type.ENTITY) {
            EntityType entityType;
            try {
                entityType = EntityType.valueOf(cmd1);
                mainInstance.printLeaderboard(statistic, entityType);
            } catch (IllegalArgumentException ignored) {}
        } else {
            Material material;
            try {
                material = Material.valueOf(cmd1);
                mainInstance.printLeaderboard(statistic, material);
            } catch (IllegalArgumentException ignored) {}
        }
        return true;
    }
}
