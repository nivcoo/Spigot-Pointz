package fr.nivcoo.pointz.commands;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.constructor.PlayersInformations;
import fr.nivcoo.utilsz.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface CCommand extends Command {

    default void execute(JavaPlugin plugin, CommandSender sender, String[] args) {
        execute((Pointz) plugin, sender, args);
    }

    default List<String> tabComplete(JavaPlugin plugin, CommandSender sender, String[] args) {
        return tabComplete((Pointz) plugin, sender, args);
    }

    void execute(Pointz plugin, CommandSender sender, String[] args);

    List<String> tabComplete(Pointz plugin, CommandSender sender, String[] args);

    default PlayersInformations getWebsiteUser(Player p) {
        List<PlayersInformations> users = Pointz.get().getWebsiteAPI().getPlayersInfos(Collections.singletonList(p));
        if (users.size() > 0)
            return users.get(0);
        return null;
    }

    default List<String> getOnlinePlayersNames() {
        List<String> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            players.add(p.getName());
        }
        return players;
    }
}
