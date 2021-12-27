package fr.nivcoo.pointz.placeholder;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.constructor.PlayersInformations;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceHolderAPI extends PlaceholderExpansion {

    private Pointz pointz;

    public PlaceHolderAPI() {
        pointz = Pointz.get();
    }

    @Override
    public @NotNull String getAuthor() {
        return pointz.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "pointz";
    }

    @Override
    public @NotNull String getVersion() {
        return pointz.getDescription().getVersion();
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
