package fr.nivcoo.pointz.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SendCommand {
	
	public static void sendCommand(Player player, String cmds) {
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		for (String cmd : cmds.split("\\[[^\\[]*\\]")) {
			Bukkit.dispatchCommand(console, cmd.replace("{PLAYER}", player.getName()));
		}
	}
}
