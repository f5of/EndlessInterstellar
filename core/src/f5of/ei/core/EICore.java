package f5of.ei.core;

import f5of.annotations.Annotations;
import f5of.ei.content.EIContentLoader;
import f5of.ei.graphics.EIShaders;
import mindustry.mod.Mod;

@Annotations.ModCore
public class EICore extends Mod {

    @Override
    public void init() {
        // TODO temperature
        //new Map();

        EIShaders.load();

        EIVars.ui.build();

        EIVars.satellitesController.start();
    }

    @Override
    public void loadContent() {
        EIContentLoader.load();
    }
}
