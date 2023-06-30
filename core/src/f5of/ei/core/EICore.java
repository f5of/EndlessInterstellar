package f5of.ei.core;

import f5of.annotations.Annotations;
import f5of.ei.content.EIContentLoader;
import f5of.ei.graphics.EIShaders;
import mindustry.Vars;
import mindustry.mod.Mod;

@Annotations.ModCore
public class EICore extends Mod {

    @Override
    public void init() {
        // TODO temperature
        //new Map();

        EIShaders.load();

        EIVars.ui.build();

        // TODO satellites
        //EIVars.satellitesController.start();

        Vars.ui.planet.planets.cam.far = 1000000f;
        Vars.ui.planet.planets.projector.setScaling(1f / 1000000f);
    }

    @Override
    public void loadContent() {
        EIContentLoader.load();
    }
}
