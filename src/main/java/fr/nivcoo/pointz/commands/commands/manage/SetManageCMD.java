package fr.nivcoo.pointz.commands.commands.manage;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.commands.CCommand;
import fr.nivcoo.pointz.constructor.PlayersInformations;
import fr.nivcoo.utilsz.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetManageCMD implements CCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("set");
    }

    @Override
    public String getPermission() {
        return "pointz.command.manage.set";
    }

    @Override
    public String getUsage() {
        return "set <player> <number>";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    public void execute(Pointz plugin, CommandSender sender, String[] args) {
        Config message = Pointz.get().getMessages();
        String prefix = plugin.getPrefix();
        int numberArg_2;
        try {
            numberArg_2 = Integer.parseInt(args[2]);
            if (numberArg_2 < 0) {
                sender.sendMessage(message.getString("positive-number", prefix));
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(message.getString("only-number", prefix));
            return;
        }

        String targetName = String.valueOf(args[1]);

        Player target = Bukkit.getPlayer(targetName);
        PlayersInformations user_target = getWebsiteUser(targetName);

        String name = sender.getName();

        if (user_target != null) {
            plugin.getWebsiteAPI().setMoneyPlayer(targetName, numberArg_2);
            if (!name.equals(targetName))
                sender.sendMessage(message.getString("command-set-own", prefix,
                        String.valueOf(numberArg_2), targetName));
            if (target != null && Bukkit.getOnlinePlayers().contains(target))
                target.sendMessage(message.getString("command-set-other", prefix,
                        name, String.valueOf(numberArg_2)));
        } else {
            sender.sendMessage(message.getString("no-register", prefix));
        }


    }

    @Override
    public List<String> tabComplete(Pointz plugin, CommandSender sender, String[] args) {
        if (args.length == 2)
            return getOnlinePlayersNames();
        else if (args.length == 3)
            return Arrays.asList("1", "2", "3", "4");
        return new ArrayList<>();
    }

}
