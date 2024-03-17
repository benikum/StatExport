package de.benikum.statexport;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardTabCompleter implements TabCompleter {
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> tabCompleteList = new ArrayList<>();
        if (args.length == 1) {
            for (Statistic statistic : Statistic.values()) {
                tabCompleteList.add(statistic.name());
            }
        } else if (args.length == 2) {
            Statistic statistic;
            try {
                statistic = Statistic.valueOf(args[0]);
                Statistic.Type sType = statistic.getType();
                
                if (sType == Statistic.Type.ENTITY) {
                    for (EntityType entityType : EntityType.values()) {
                        tabCompleteList.add(entityType.name());
                    }
                }
                if (sType == Statistic.Type.ITEM || sType == Statistic.Type.BLOCK) {
                    for (Material material : Material.values()) {
                        tabCompleteList.add(material.name());
                    }
                }
            } catch (IllegalArgumentException ignored) {}
        }
        return tabCompleteList;
    }
}