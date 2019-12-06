package fr.nivcoo.pointz.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.configuration.Config;
import fr.nivcoo.pointz.configuration.DataBase;

public class Commands implements CommandExecutor {
	private Config message = Pointz.get().getMessages();
	String prefix = message.getString("prefix");
	private static DataBase bdd = Pointz.get().getBdd();

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

			if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("send")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;

					if (args[0].equalsIgnoreCase("check")) {
						if (player.hasPermission("pointz.check")) {
							try {
								int money = getMoneyPlayer(player);

								player.sendMessage(message.getString("check-command", prefix, String.valueOf(money)));
							} catch (SQLException e) {
								e.printStackTrace();
							}
						} else {
							player.sendMessage(message.getString("no-permission", prefix));
						}
						return true;
					}

					else if (args[0].equalsIgnoreCase("send")) {
						if (player.hasPermission("pointz.send")) {

							if (args.length == 3 && (!args[1].isEmpty() && Integer.parseInt(args[2]) > 0)) {
								int numberArg_2 = Integer.parseInt(args[2]);
								Player cible = Bukkit.getPlayer(args[1]);
								String playerName = player.getName();

								if (cible != null && cible != player) {
									try {

										String playerCible_web = getPseudoPlayer(cible);

										if (playerCible_web != null && playerCible_web.equalsIgnoreCase(args[1])) {
											String player_web = this.getPseudoPlayer(player);
											if (player_web.equalsIgnoreCase(playerName)) {
												try {
													int getPlayer_money = getMoneyPlayer(player);

													if (getPlayer_money >= numberArg_2) {

														int getPlayer_money_after = getPlayer_money - numberArg_2;
														this.setPlayerMoney(player, getPlayer_money_after);
														int getCible_money = 0;

														getCible_money = Commands.getMoneyPlayer(cible);

														int getCible_money_after = getCible_money + numberArg_2;
														setPlayerMoney(cible, getCible_money_after);
														player.sendMessage(message
																.getString("send-old", prefix,
																		String.valueOf(getPlayer_money))
																.replace("{1}", "" + getPlayer_money));
														player.sendMessage(message.getString("send-new", prefix,
																String.valueOf(getPlayer_money_after)));
														cible.sendMessage(message.getString("send-cible", prefix,
																String.valueOf(playerName),
																String.valueOf(numberArg_2)));

													} else {
														player.sendMessage(
																message.getString("no-require-money", prefix));
													}
												} catch (SQLException e) {
													e.printStackTrace();
												}

											} else {
												player.sendMessage(message.getString("no-register-own", prefix));
											}
										} else {
											player.sendMessage(message.getString("no-register", prefix));
										}
									} catch (SQLException e1) {
										e1.printStackTrace();
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
						return true;

					}

				} else {
					sender.sendMessage(message.getString("no-player", prefix));
				}
			}

			else if ((args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add")
					|| args[0].equalsIgnoreCase("del")) && args.length == 3 && (!args[1].isEmpty())) {
				try {
					if (Integer.parseInt(args[2]) < 0) {
						sender.sendMessage(message.getString("positive-number", prefix));
						return false;
					}
				} catch (NumberFormatException e) {
					sender.sendMessage(message.getString("only-number", prefix));
					return false;
				}

				int numberArg_2 = Integer.parseInt(args[2]);

				Player cible = Bukkit.getPlayer(args[1]);
				if (sender.hasPermission("pointz.manage")) {
					String name = sender.getName();
					if (args[0].equalsIgnoreCase("set")) {
						if (args.length == 3 && (!args[1].isEmpty() && Integer.parseInt(args[2]) >= 0)) {
							if (Bukkit.getOnlinePlayers().contains(cible)) {
								try {
									String player_web = getPseudoPlayer(cible);

									if (player_web.equalsIgnoreCase(args[1])) {
										setPlayerMoney(cible, numberArg_2);
										if (sender != cible)
											sender.sendMessage(message.getString("command-set-own", prefix,
													String.valueOf(numberArg_2), String.valueOf(args[1])));
										cible.sendMessage(message.getString("command-set-other", prefix,
												String.valueOf(name), String.valueOf(numberArg_2)));
										return true;
									} else {
										sender.sendMessage(message.getString("no-register", prefix));
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}

							}
						} else {
							sender.sendMessage(message.getString("command-set", prefix));
						}
						return false;

					} else if (args[0].equalsIgnoreCase("add")) {
						if (args.length == 3 && (!args[1].isEmpty() && Integer.parseInt(args[2]) >= 0)) {
							if (Bukkit.getOnlinePlayers().contains(cible)) {
								try {
									String player_web = getPseudoPlayer(cible);

									if (player_web.equalsIgnoreCase(args[1])) {

										int playerMoney = getMoneyPlayer(cible);

										int newPlayerMoney = playerMoney + numberArg_2;
										setPlayerMoney(cible, newPlayerMoney);
										if (sender != cible)
											sender.sendMessage(message.getString("command-add-own", prefix,
													String.valueOf(numberArg_2), args[1]));
										cible.sendMessage(message.getString("command-add-other", prefix, name,
												String.valueOf(numberArg_2)));
										return true;

									} else {
										sender.sendMessage(message.getString("no-register", prefix));
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
							} else {
								sender.sendMessage(message.getString("not-connected", prefix));
							}

						} else {
							sender.sendMessage(message.getString("command-add", prefix));
						}
						return false;

					} else if (args[0].equalsIgnoreCase("del")) {
						if (Bukkit.getOnlinePlayers().contains(cible)) {
							try {
								String player_web = getPseudoPlayer(cible);

								if (player_web.equalsIgnoreCase(args[1])) {

									int playerMoney = getMoneyPlayer(cible);

									int newPlayerMoney = playerMoney - numberArg_2;
									if (newPlayerMoney >= 0) {
										setPlayerMoney(cible, newPlayerMoney);
										if (sender != cible)
											sender.sendMessage(message.getString("command-del-own", prefix,
													String.valueOf(numberArg_2), args[1]));
										cible.sendMessage(message.getString("command-del-other", prefix, name,
												String.valueOf(numberArg_2)));
										return true;

									} else {
										sender.sendMessage("Le joueur n'a pas autant d'argent.");
									}

								} else {
									sender.sendMessage(message.getString("no-register", prefix));
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						} else {
							sender.sendMessage(message.getString("not-connected", prefix));
						}
						return false;
					}
				} else {
					sender.sendMessage(message.getString("no-permission", prefix));
				}
			} else {
				sender.sendMessage(message.getString("syntax-error", prefix));
			}

		}

		return false;
	}

	public static int getMoneyPlayer(Player player) throws SQLException {
		PreparedStatement ps = null;
		Connection c = null;
		ResultSet rs = null;
		try {
			c = bdd.getConnection();
			ps = c.prepareStatement("SELECT money FROM users WHERE pseudo = ?");

			ps.setString(1, player.getName());
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("money");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ps.close();
			c.close();
		}
		return 0;

	}

	private String getPseudoPlayer(Player player) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			c = bdd.getConnection();
			ps = c.prepareStatement("SELECT pseudo FROM users WHERE pseudo = ?");

			ps.setString(1, player.getName());
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("pseudo");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.close(c, ps, rs);
		}
		return null;

	}

	private void setPlayerMoney(Player player, int money) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = bdd.getConnection();
			ps = c.prepareStatement("UPDATE users SET money = ? WHERE pseudo = ?");

			ps.setInt(1, money);
			ps.setString(2, player.getName());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.close(c, ps, null);
		}

	}

	private void close(Connection c, PreparedStatement ps, ResultSet rs) {
		try {
			if (c != null)
				c.close();
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			System.out.println("Error while closing database c: " + e);
		}
	}

}
