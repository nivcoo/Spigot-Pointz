package fr.nivcoo.pointz.placeholder.placeholder;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;

import fr.nivcoo.pointz.Pointz;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolderAPI extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		// TODO Auto-generated method stub
		return "nivcoo";
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return "pointz";
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "0.0.1";
	}

	@Override
	public String onRequest(OfflinePlayer player, String identifier) {

		if (identifier.equals("get_money")) {

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
