package fr.nivcoo.pointz.placeholder.placeholder;

import java.sql.SQLException;

import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.commands.Commands;

public class MVDWPlaceHolderAPI implements PlaceholderReplacer {
	
	Pointz pointz;

	public MVDWPlaceHolderAPI(Pointz pointz) {
		this.pointz = pointz;
	}

	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
		if (event.getPlaceholder().equalsIgnoreCase("pointz_get_money")) {
			Player player = event.getPlayer();
			int money = 0;
			try {
				money = Commands.getMoneyPlayer(player);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return String.valueOf(money);
		}
		return null;
	}

	

}
