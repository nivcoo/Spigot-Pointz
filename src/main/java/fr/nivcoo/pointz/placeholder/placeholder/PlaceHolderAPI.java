package fr.nivcoo.pointz.placeholder.placeholder;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.nivcoo.pointz.commands.Commands;
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
    public String onRequest(OfflinePlayer player, String identifier){
  
        if(identifier.equals("get_money")){
			int money = 0;
			try {
				money = Commands.getMoneyPlayer((Player) player);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return String.valueOf(money);
        }

        return null;
    }

}
