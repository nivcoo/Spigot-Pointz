package fr.nivcoo.pointz.commands;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.constructor.PlayersInformations;
import fr.nivcoo.pointz.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Commands implements CommandExecutor {
    private Config message = Pointz.get().getMessages();
    String prefix = message.getString("prefix");

    public void help(CommandSender p) {

        if (p.hasPermission("pointz.command")) {
            p.sendMessage(message.getString("command-title", prefix));
            if (p.hasPermission("pointz.check"))
                p.sendMessage(message.getString("command-check") + message.getString("command-check-desc"));
            if (p.hasPermission("pointz.send"))
                p.sendMessage(message.getString("command-send") + message.getString("command-send-desc"));
            if (p.hasPermission("pointz.manage")) {
                p.sendMessage(message.getString("command-set") + message.getString("command-admin-desc"));
                p.sendMessage(message.getString("command-add") + message.getString("command-admin-desc"));
                p.sendMessage(message.getString("command-del") + message.getString("command-admin-desc"));
            }
            if (p.hasPermission("pointz.shop"))
                p.sendMessage(message.getString("command-shop") + message.getString("command-shop-desc"));
            if (p.hasPermission("pointz.converter"))
                p.sendMessage(message.getString("command-converter") + message.getString("command-converter-desc"));
        } else {
            p.sendMessage(message.getString("no-permission", prefix));
        }
        return;
    }

    public static void sendCommand(Player player, String cmds) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        for (String cmd : cmds.split("\\[\\{\\+\\}\\]")) {
            Bukkit.dispatchCommand(console, cmd.replace("{PLAYER}", player.getName()));
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("pointz")) {
            if (args.length == 0) {
                this.help(sender);
                return false;
            }
            Pointz pointz = Pointz.get();
            Bukkit.getScheduler().runTaskAsynchronously(Pointz.get(), () -> {

                if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("send")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;

                        List<PlayersInformations> users = pointz.getWebsiteAPI().getPlayersInfos(Collections.singletonList(player));
                        PlayersInformations user = users.get(0);

                        if (args[0].equalsIgnoreCase("check")) {
                            if (player.hasPermission("pointz.check")) {
                                if (user == null) {
                                    sender.sendMessage(message.getString("no-register-own", prefix));
                                    return;
                                }
                                double money = user.getMoney();

                                player.sendMessage(message.getString("check-command", prefix, String.valueOf(money)));
                            } else {
                                player.sendMessage(message.getString("no-permission", prefix));
                            }
                        } else if (args[0].equalsIgnoreCase("send")) {
                            if (player.hasPermission("pointz.send")) {

                                if (args.length == 3 && (!args[1].isEmpty() && Integer.parseInt(args[2]) > 0)) {
                                    int numberArg_2 = Integer.parseInt(args[2]);
                                    Player cible = Bukkit.getPlayer(args[1]);
                                    List<PlayersInformations> users_cible = pointz.getWebsiteAPI().getPlayersInfos(Collections.singletonList(cible));
                                    PlayersInformations user_cible = users_cible.get(0);
                                    String playerName = player.getName();

                                    if (cible != null && cible != player) {
                                        if (user_cible != null) {
                                            if (user != null) {
                                                double getPlayer_money = user.getMoney();

                                                if (getPlayer_money >= numberArg_2) {

                                                    double getPlayer_money_after = getPlayer_money - numberArg_2;
                                                    pointz.getWebsiteAPI().setMoneyPlayer(player,
                                                            getPlayer_money_after);
                                                    double getCible_money = user_cible.getMoney();

                                                    double getCible_money_after = getCible_money + numberArg_2;
                                                    pointz.getWebsiteAPI().setMoneyPlayer(cible, getCible_money_after);
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

                                } else {
                                    player.sendMessage(message.getString("command-send", prefix));
                                }
                            } else {
                                player.sendMessage(message.getString("no-permission", prefix));
                            }

                        }

                    } else {
                        sender.sendMessage(message.getString("no-player", prefix));
                    }
                } else if ((args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add")
                        || args[0].equalsIgnoreCase("del")) && args.length == 3 && (!args[1].isEmpty())) {
                    try {
                        if (Integer.parseInt(args[2]) < 0) {
                            sender.sendMessage(message.getString("positive-number", prefix));
                            return;
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(message.getString("only-number", prefix));
                        return;
                    }

                    int numberArg_2 = Integer.parseInt(args[2]);

                    Player cible = Bukkit.getPlayer(args[1]);
                    List<PlayersInformations> users_cible = pointz.getWebsiteAPI().getPlayersInfos(Collections.singletonList(cible));
                    PlayersInformations user_cible = users_cible.get(0);

                    if (sender.hasPermission("pointz.manage")) {
                        String name = sender.getName();
                        if (args[0].equalsIgnoreCase("set")) {
                            if (!args[1].isEmpty() && Integer.parseInt(args[2]) >= 0) {
                                if (Bukkit.getOnlinePlayers().contains(cible)) {
                                    if (user_cible != null) {
                                        pointz.getWebsiteAPI().setMoneyPlayer(cible, numberArg_2);
                                        if (sender != cible)
                                            sender.sendMessage(message.getString("command-set-own", prefix,
                                                    String.valueOf(numberArg_2), String.valueOf(args[1])));
                                        cible.sendMessage(message.getString("command-set-other", prefix,
                                                name, String.valueOf(numberArg_2)));
                                        return;
                                    } else {
                                        sender.sendMessage(message.getString("no-register", prefix));
                                    }

                                }
                            } else {
                                sender.sendMessage(message.getString("command-set", prefix));
                            }

                        } else if (args[0].equalsIgnoreCase("add")) {
                            if (!args[1].isEmpty() && Integer.parseInt(args[2]) >= 0) {
                                if (Bukkit.getOnlinePlayers().contains(cible)) {
                                    if (user_cible != null) {

                                        double playerMoney = user_cible.getMoney();

                                        double newPlayerMoney = playerMoney + numberArg_2;
                                        pointz.getWebsiteAPI().setMoneyPlayer(cible, newPlayerMoney);
                                        if (sender != cible)
                                            sender.sendMessage(message.getString("command-add-own", prefix,
                                                    String.valueOf(numberArg_2), args[1]));
                                        cible.sendMessage(message.getString("command-add-other", prefix, name,
                                                String.valueOf(numberArg_2)));

                                    } else {
                                        sender.sendMessage(message.getString("no-register", prefix));
                                    }
                                } else {
                                    sender.sendMessage(message.getString("not-connected", prefix));
                                }

                            } else {
                                sender.sendMessage(message.getString("command-add", prefix));
                            }

                        } else if (args[0].equalsIgnoreCase("del")) {
                            if (Bukkit.getOnlinePlayers().contains(cible)) {

                                if (user_cible != null) {

                                    double playerMoney = user_cible.getMoney();

                                    double newPlayerMoney = playerMoney - numberArg_2;
                                    if (newPlayerMoney >= 0) {
                                        pointz.getWebsiteAPI().setMoneyPlayer(cible, newPlayerMoney);
                                        if (sender != cible)
                                            sender.sendMessage(message.getString("command-del-own", prefix,
                                                    String.valueOf(numberArg_2), args[1]));
                                        cible.sendMessage(message.getString("command-del-other", prefix, name,
                                                String.valueOf(numberArg_2)));

                                    } else {
                                        sender.sendMessage("Le joueur n'a pas autant d'argent.");
                                    }

                                } else {
                                    sender.sendMessage(message.getString("no-register", prefix));
                                }
                            } else {
                                sender.sendMessage(message.getString("not-connected", prefix));
                            }
                        }
                    } else {
                        sender.sendMessage(message.getString("no-permission", prefix));
                    }
                } else {
                    sender.sendMessage(message.getString("syntax-error", prefix));
                }
            });

        }

        return false;
    }

}
