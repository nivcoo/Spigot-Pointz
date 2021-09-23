package fr.nivcoo.pointz.commands.commands.gui;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.commands.CCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConverterGuiCMD implements CCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("converter");
    }

    @Override
    public String getPermission() {
        return "pointz.command.gui.converter";
    }

    @Override
    public String getUsage() {
        return "converter";
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
        Pointz.get().getInventoryManager().openInventory(Pointz.get().getInventories().getConvertInventory(), player);
    }

    @Override
    public List<String> tabComplete(Pointz plugin, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

}
