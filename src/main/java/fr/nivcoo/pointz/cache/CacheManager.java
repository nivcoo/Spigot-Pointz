package fr.nivcoo.pointz.cache;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.constructor.PlayersInformations;
import fr.nivcoo.pointz.utils.WebsiteAPI;
import fr.nivcoo.utilsz.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CacheManager implements Listener {

    private Pointz pointz;
    private Config config;

    private WebsiteAPI websiteAPI;

    private List<PlayersInformations> playersInformation;

    private BukkitRunnable br;

    public CacheManager() {
        pointz = Pointz.get();
        playersInformation = new ArrayList<>();
        config = pointz.getConfiguration();
        websiteAPI = pointz.getWebsiteAPI();
        startScheduler();
    }

    public List<PlayersInformations> getAllPlayersCount(List<Player> players) {
        return websiteAPI.getPlayersInfos(players);
    }

    public void startScheduler() {
        br = new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                List<PlayersInformations> list = getAllPlayersCount(players);
                playersInformation = new ArrayList<>();
                playersInformation.addAll(list);
            }

        };
        if (config.getBoolean("hooks.placeholder-api"))
            br.runTaskTimerAsynchronously(pointz, 1, 20 * 2);
    }

    public void stopScheduler() {
        if (br != null)
            br.cancel();
    }

    public PlayersInformations getPlayerInformations(Player player) {
        PlayersInformations p = null;
        for (PlayersInformations pi : playersInformation) {
            if (pi.getUsername().equals(player.getName()))
                p = pi;
        }
        if (p == null) {
            playersInformation.add(getPlayerCountFromWebsite(player));
        }
        return p;
    }


    public PlayersInformations getPlayerCountFromWebsite(Player player) {
        List<PlayersInformations> list = websiteAPI.getPlayersInfos(Collections.singletonList(player));
        if (list == null)
            return null;
        return list.get(0);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        getPlayerInformations(e.getPlayer());
    }

}
