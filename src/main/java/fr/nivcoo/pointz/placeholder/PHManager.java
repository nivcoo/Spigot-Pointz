package fr.nivcoo.pointz.placeholder;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import fr.nivcoo.pointz.Pointz;

public class PHManager {

	private static Pointz pointz = Pointz.get();

	public static void registerMVDW(String path) {

		PlaceholderAPI.registerPlaceholder(pointz, path, new fr.nivcoo.pointz.placeholder.placeholder.MVDWPlaceHolderAPI(pointz));

	}

	public static void register() {

		new fr.nivcoo.pointz.placeholder.placeholder.PlaceHolderAPI().register();

	}

}
