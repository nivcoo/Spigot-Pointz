package fr.nivcoo.pointz.placeholder;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.placeholder.placeholder.MVDWPlaceHolderAPI;
import fr.nivcoo.pointz.placeholder.placeholder.PlaceHolderAPI;

public class PHManager {

	private static Pointz pointz = Pointz.get();

	public static void registerMVDW(String path) {

		PlaceholderAPI.registerPlaceholder(pointz, path, new MVDWPlaceHolderAPI(pointz));

	}

	public static void register() {

		new PlaceHolderAPI().register();

	}

}
