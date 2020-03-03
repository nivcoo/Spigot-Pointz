package fr.nivcoo.pointz.placeholder;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.placeholder.placeholder.MVDWPlaceHolderAPI;

public class RegisterMVDWPAPI {
	
	public RegisterMVDWPAPI(String name, Pointz pointz) {
		PlaceholderAPI.registerPlaceholder(pointz, "pointz_get_money",
				new MVDWPlaceHolderAPI(pointz));
	}

}
