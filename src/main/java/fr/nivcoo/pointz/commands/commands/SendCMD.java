package fr.nivcoo.pointz.commands.commands;

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

public class SendCMD implements CCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("send");
    }

    @Override
    public String getPermission() {
        return "pointz.command.send";
    }

    @Override
    public String getUsage() {
        return "send <player> <number>";
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
        return false;
    }

    public void execute(Pointz plugin, CommandSender sender, String[] args) {

        Player player = (Player) sender;
        PlayersInformations user = getWebsiteUser(player);
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

        Player cible = Bukkit.getPlayer(args[1]);
        PlayersInformations user_cible = getWebsiteUser(cible);

        String playerName = player.getName();

        if (cible != null && cible != player) {
            if (user_cible != null) {
                if (user != null) {
                    double getPlayer_money = user.getMoney();

                    if (getPlayer_money >= numberArg_2) {

                        double getPlayer_money_after = getPlayer_money - numberArg_2;
                        plugin.getWebsiteAPI().setMoneyPlayer(player,
                                getPlayer_money_after);
                        double getCible_money = user_cible.getMoney();

                        double getCible_money_after = getCible_money + numberArg_2;
                        plugin.getWebsiteAPI().setMoneyPlayer(cible, getCible_money_after);
                        player.sendMessage(message
                                .getString("send-old", prefix,
                                        String.valueOf(getPlayer_money))
                                .replace("{1}", "" + getPlayer_money));
                        player.sendMessage(message.getString("send-new", prefix,
                                String.valueOf(getPlayer_money_after)));
                        cible.sendMessage(message.getString("send-cible", prefix,
                                playerName, String.valueOf(numberArg_2)));

                    } else {
                        player.sendMessage(message.getString("no-require-money", prefix));
                    }

                } else {
                    player.sendMessage(message.getString("no-register-own", prefix));
                }
            } else {
                player.sendMessage(message.getString("no-register", prefix));
            }
        } else {
            player.sendMessage(message.getString("not-connected", prefix));
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
