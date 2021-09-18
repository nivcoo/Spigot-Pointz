package fr.nivcoo.pointz.placeholder;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.constructor.PlayersInformations;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
            Player p = player.getPlayer();
            if (p == null)
                return "0";
            PlayersInformations user = Pointz.get().getCacheManager().getPlayerInformations(p);
            String money = "0";
            if (user == null)
                return money;
            money = String.valueOf(user.getMoney());
            return money;
        }

        return null;
    }

}
