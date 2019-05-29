package fr.nivcoo.pointz.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.configuration.Config;

public class GuiCommands implements CommandExecutor {
	private Config message = Pointz.getMessages();
	String prefix = message.getString("prefix");

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("pshop")) {

				if (p.hasPermission("pointz.shop")) {
					Pointz.guiShop.show(p.getPlayer(), 0);
				} else {
					p.sendMessage(message.getString("no-permission", prefix));
				}
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("pconverter")) {
				if (p.hasPermission("pointz.converter")) {
					Pointz.guiShop.show(p.getPlayer(), 1);
				} else {
					p.sendMessage(message.getString("no-permission", prefix));
				}

				return true;
			}
		} else {
			sender.sendMessage(message.getString("no-player", prefix));
		}
		return false;
	}

}
