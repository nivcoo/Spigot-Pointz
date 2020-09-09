package fr.nivcoo.pointz.placeholder.placeholder;

import java.util.HashMap;

import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import fr.nivcoo.pointz.Pointz;

public class MVDWPlaceHolderAPI implements PlaceholderReplacer {

	Pointz pointz;

	public MVDWPlaceHolderAPI(Pointz pointz) {
		this.pointz = pointz;
	}

	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
		if (event.getPlaceholder().equalsIgnoreCase("pointz_get_money")) {
			Player player = event.getPlayer();

			HashMap<String, String> user = Pointz.get().getUserWebsite().get(player.getName());

			String money = "0";
			if (user == null || user.get("error") == "true")
				return money;
			money = user.get("money");
			return String.valueOf(money);

		}
		return null;
	}

}
