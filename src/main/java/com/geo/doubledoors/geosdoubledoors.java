package com.geo.doubledoors;

import com.geo.doubledoors.features.AutoDoubleDoors;
import net.fabricmc.api.ModInitializer;

public class geosdoubledoors implements ModInitializer {

    @Override
    public void onInitialize() {
        AutoDoubleDoors.register();

    }
}
