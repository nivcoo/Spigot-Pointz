package fr.nivcoo.pointz.commands.commands;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.commands.CCommand;
import fr.nivcoo.pointz.constructor.PlayersInformations;
import fr.nivcoo.utilsz.config.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckCMD implements CCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("check");
    }

    @Override
    public String getPermission() {
        return "pointz.command.check";
    }

    @Override
    public String getUsage() {
        return "check";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    public void execute(Pointz plugin, CommandSender sender, String[] args) {

        Player player = (Player) sender;
        String playerName = player.getName();
        PlayersInformations user = getWebsiteUser(playerName);
        Config message = Pointz.get().getMessages();
        String prefix = plugin.getPrefix();
        if (user == null) {
            sender.sendMessage(message.getString("no-register-own", prefix));
            return;
        }
        double money = user.getMoney();

        player.sendMessage(message.getString("check-command", prefix, String.valueOf(money)));
    }

    @Override
    public List<String> tabComplete(Pointz plugin, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

}
