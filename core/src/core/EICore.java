package core;

import annotations.Annotations;
import arc.Core;
import content.EIContentLoader;
import files.EIAtlasLoader;
import mindustry.mod.Mod;

@Annotations.ModCore
public class EICore extends Mod {
    @Override
    public void loadContent() {
        EIContentLoader.load();
    }
}
