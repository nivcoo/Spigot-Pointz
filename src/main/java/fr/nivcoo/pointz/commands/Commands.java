package fr.nivcoo.pointz.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nivcoo.pointz.Points;
import fr.nivcoo.pointz.configuration.Config;
import fr.nivcoo.pointz.configuration.DataBase;

public class Commands implements CommandExecutor {
	Config config = new Config(new File("plugins" + File.separator + "Points" + File.separator + "config.yml"));
	Config message = new Config(new File("plugins" + File.separator + "Points" + File.separator + "message.yml"));
	String PrefixPoint = message.getString("prefix");
	String h = config.getString("host");
	String n = config.getString("name");
	String p = config.getString("pass");
	String db = config.getString("dbName");
	int po = config.getInt("port");
	public DataBase bdd = new DataBase(h, db, n, p);

	public void help(CommandSender p) {

		if (p.hasPermission("points.command")) {
			p.sendMessage(PrefixPoint + message.getString("command-title"));
			if (p.hasPermission("points.check"))
				p.sendMessage(message.getString("command-check") + message.getString("command-check-desc"));
			if (p.hasPermission("points.send"))
				p.sendMessage(message.getString("command-send") + message.getString("command-send-desc"));
			if (p.hasPermission("points.manage")) {
				p.sendMessage(message.getString("command-set") + message.getString("command-admin-desc"));
				p.sendMessage(message.getString("command-add") + message.getString("command-admin-desc"));
				p.sendMessage(message.getString("command-del") + message.getString("command-admin-desc"));
			}
			if (p.hasPermission("points.shop"))
				p.sendMessage(message.getString("command-shop") + message.getString("command-shop-desc"));
			if (p.hasPermission("points.gui"))
				p.sendMessage(message.getString("command-gui") + message.getString("command-gui-desc"));
		} else {
			p.sendMessage(PrefixPoint + message.getString("no-permission"));
		}
		return;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("pshop")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("points.shop")) {
					Points.guiShop.show(p.getPlayer());
				} else {
					p.sendMessage(PrefixPoint + message.getString("no-permission"));
				}
			} else {
				sender.sendMessage("�cSeulement les joueurs InGame peuvent ex�cuter cette commande.");
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("pgui")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (p.hasPermission("points.gui")) {
					Points.guiPS.show(p.getPlayer());
				} else {
					p.sendMessage(PrefixPoint + message.getString("no-permission"));
				}
			} else {
				sender.sendMessage("�cSeulement les joueurs InGame peuvent ex�cuter cette commande.");
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("points")) {
			if (args.length == 0) {
				this.help(sender);
				return true;

			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("send")) {
					if (sender instanceof Player) {
						Player p = (Player) sender;

						if (args[0].equalsIgnoreCase("check")) {
							if (p.hasPermission("points.check")) {
								String name = p.getName();
								int bc = bdd.getInt("SELECT money FROM users WHERE pseudo = '" + name + "';", 1);
								p.sendMessage(PrefixPoint + message.getString("check-command").replace("{1}", "" + bc));
							} else {
								p.sendMessage(PrefixPoint + message.getString("no-permission"));
							}
							return true;
						}

						if (args[0].equalsIgnoreCase("send")) {
							if (p.hasPermission("points.send")) {
								p.sendMessage(PrefixPoint + message.getString("command-send"));
							} else {
								p.sendMessage(PrefixPoint + message.getString("no-permission"));
							}
							return true;
						}

					} else {
						sender.sendMessage("�cSeulement les joueurs InGame peuvent ex�cuter cette commande.");
					}
				}

				else if (sender.hasPermission("points.manage")) {
					if (args[0].equalsIgnoreCase("set")) {
						sender.sendMessage(PrefixPoint + message.getString("command-set"));
						return true;
					} else if (args[0].equalsIgnoreCase("add")) {
						sender.sendMessage(PrefixPoint + message.getString("command-add"));
						return true;
					} else if (args[0].equalsIgnoreCase("del")) {
						sender.sendMessage(PrefixPoint + message.getString("command-del"));
						return true;
					}
				} else {
					sender.sendMessage(PrefixPoint + message.getString("no-permission"));
					return true;
				}

			}

		}

		if (args.length == 2)

		{
			if (args[0].equalsIgnoreCase("send")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (args[0].equalsIgnoreCase("send") && !args[1].equalsIgnoreCase("")) {
						if (p.hasPermission("points.send")) {
							p.sendMessage(PrefixPoint + message.getString("command-send"));
							return true;
						} else {
							p.sendMessage(PrefixPoint + message.getString("no-permission"));
							return true;
						}

					} else {
						sender.sendMessage(PrefixPoint + message.getString("no-permission"));
						return true;
					}
				} else {
					sender.sendMessage("�cSeulement les joueurs InGame peuvent ex�cuter cette commande.");
				}
			}
			else if (sender.hasPermission("points.manage")) {
				if (args[0].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("")) {
					sender.sendMessage(PrefixPoint + message.getString("command-set"));
					return true;
				}
				else if (args[0].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("")) {
					sender.sendMessage(PrefixPoint + message.getString("command-add"));
					return true;
				}
				else if (args[0].equalsIgnoreCase("del") && !args[1].equalsIgnoreCase("")) {
					sender.sendMessage(PrefixPoint + message.getString("command-del"));
					return true;
				}
			} else {
				sender.sendMessage(PrefixPoint + message.getString("no-permission"));
				return true;
			}

		}

		if (args.length == 3) {
			try {
				int numberArgs = Integer.parseInt(args[2]);
				if (sender.hasPermission("points.manage")) {
					if (args[0].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("") && numberArgs >= 0) {
						Player cible = Bukkit.getPlayer(args[1]);
						if (Bukkit.getOnlinePlayers().contains(cible)) {
							String name = sender.getName();
							String playersend = bdd
									.getString("SELECT pseudo FROM users WHERE pseudo = '" + args[1] + "';", 1);

							if (playersend.equalsIgnoreCase(args[1])) {

								/*
								 * bdd.sendRequest("UPDATE users SET money = " + numberArgs +
								 * " WHERE pseudo = '" + args[1] + "';");
								 */
								bdd.sendPreparedRequest("UPDATE", "users", "money", numberArgs, "pseudo", args[1]);
								sender.sendMessage(PrefixPoint + message.getString("command-set-own")
										.replace("{0}", "" + numberArgs).replace("{1}", args[1]));
								cible.sendMessage(PrefixPoint + message.getString("command-set-other")
										.replace("{0}", "" + name).replace("{1}", "" + numberArgs));
							} else {
								sender.sendMessage(PrefixPoint + message.getString("no-register"));
							}
						} else {
							sender.sendMessage(PrefixPoint + message.getString("not-connected"));
						}
						return true;
					}

					String name = sender.getName();
						if (args[0].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("") && numberArgs > 0) {
							Player cible = Bukkit.getPlayer(args[1]);
							if (Bukkit.getOnlinePlayers().contains(cible)) {
								String playersend = bdd
										.getString("SELECT pseudo FROM users WHERE pseudo = '" + args[1] + "';", 1);

								if (playersend.equalsIgnoreCase(args[1])) {
									int OldMoney = bdd
											.getInt("SELECT money FROM users WHERE pseudo = '" + args[1] + "';", 1);
									int NewMoney = OldMoney + numberArgs;
									/*
									 * bdd.sendRequest("UPDATE users SET money = " + NewMoney + " WHERE pseudo = '"
									 * + args[1] + "';");
									 */
									bdd.sendPreparedRequest("UPDATE", "users", "money", NewMoney, "pseudo", args[1]);
									sender.sendMessage(PrefixPoint + message.getString("command-add-own")
											.replace("{0}", "" + numberArgs).replace("{1}", args[1]));
									cible.sendMessage(PrefixPoint + message.getString("command-add-other")
											.replace("{0}", "" + name).replace("{1}", "" + numberArgs));

								} else {
									sender.sendMessage(PrefixPoint + message.getString("no-register"));
								}
							} else {
								sender.sendMessage(PrefixPoint + message.getString("not-connected"));
							}
							return true;
						} else if (args[0].equalsIgnoreCase("del") && !args[1].equalsIgnoreCase("") && numberArgs > 0) {
							Player cible = Bukkit.getPlayer(args[1]);
							if (Bukkit.getOnlinePlayers().contains(cible)) {
								String playersend = bdd
										.getString("SELECT pseudo FROM users WHERE pseudo = '" + args[1] + "';", 1);

								if (playersend.equalsIgnoreCase(args[1])) {
									int OldMoney = bdd
											.getInt("SELECT money FROM users WHERE pseudo = '" + args[1] + "';", 1);
									int NewMoney = OldMoney - numberArgs;
									if (NewMoney >= 0) {
										/*
										 * bdd.sendRequest("UPDATE users SET money = " + NewMoney + " WHERE pseudo = '"
										 * + args[1] + "';");
										 */
										bdd.sendPreparedRequest("UPDATE", "users", "money", NewMoney, "pseudo",
												args[1]);
										sender.sendMessage(PrefixPoint + message.getString("command-del-own")
												.replace("{0}", "" + numberArgs).replace("{1}", args[1]));
										cible.sendMessage(PrefixPoint + message.getString("command-del-other")
												.replace("{0}", "" + name).replace("{1}", "" + numberArgs));
									} else {
										sender.sendMessage(PrefixPoint + "Le joueur n'a pas autant d'argent.");
									}
								} else {
									sender.sendMessage(PrefixPoint + message.getString("no-register"));
								}
							} else {
								sender.sendMessage(PrefixPoint + message.getString("not-connected"));
							}
							return true;
						}
				} else {
					sender.sendMessage(PrefixPoint + message.getString("no-permission"));
					return true;
				}
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (args[0].equalsIgnoreCase("send") && !args[1].equalsIgnoreCase("") && numberArgs > 0) {

						if (p.hasPermission("points.send")) {
							Player cible = Bukkit.getPlayer(args[1]);
							if (Bukkit.getOnlinePlayers().contains(cible)) {
								String name = p.getName();
								String PlayerCibleWeb = bdd
										.getString("SELECT pseudo FROM users WHERE pseudo = '" + args[1] + "';", 1);
								if (PlayerCibleWeb.equalsIgnoreCase(args[1])) {
									String PlayerSendWeb = bdd
											.getString("SELECT pseudo FROM users WHERE pseudo = '" + name + "';", 1);
									if (PlayerSendWeb.equalsIgnoreCase(name)) {
										int GetMoneyPlayerSend = bdd
												.getInt("SELECT money FROM users WHERE pseudo = '" + name + "';", 1);
										if (GetMoneyPlayerSend >= numberArgs) {
											int PlayerSendMoneyFinal = GetMoneyPlayerSend - numberArgs;
											/*
											 * bdd.sendRequest("UPDATE users SET money = " + PlayerSendMoneyFinal +
											 * " WHERE pseudo = '" + name + "';");
											 */
											bdd.sendPreparedRequest("UPDATE", "users", "money", PlayerSendMoneyFinal,
													"pseudo", name);
											int GetMoneyCibleSend = bdd.getInt(
													"SELECT money FROM users WHERE pseudo = '" + args[1] + "';", 1);
											int PlayerCibleMoneyFinal = GetMoneyCibleSend + numberArgs;
											bdd.sendRequest("UPDATE users SET money = " + PlayerCibleMoneyFinal
													+ " WHERE pseudo = '" + args[1] + "';");
											p.sendMessage(PrefixPoint + message.getString("send-old").replace("{1}",
													"" + GetMoneyPlayerSend));
											p.sendMessage(PrefixPoint + message.getString("send-new").replace("{1}",
													"" + PlayerSendMoneyFinal));
											cible.sendMessage(PrefixPoint + message.getString("send-cible")
													.replace("{0}", name).replace("{1}", "" + numberArgs));
										} else {
											p.sendMessage(PrefixPoint + message.getString("no-require-money"));
										}

									} else {
										p.sendMessage(PrefixPoint + message.getString("no-register-own"));
									}
								} else {
									p.sendMessage(PrefixPoint + message.getString("no-register"));
								}
							} else {
								p.sendMessage(PrefixPoint + message.getString("not-connected"));
							}
						} else {
							p.sendMessage(PrefixPoint + message.getString("no-permission"));
						}
						return true;
					}
				} else {
					sender.sendMessage("�cSeulement les joueurs InGame peuvent ex�cuter cette commande.");
				}
			} catch (NumberFormatException ex) {
				sender.sendMessage(PrefixPoint + message.getString("only-number"));
			}

		}
		return false;
	}

}
