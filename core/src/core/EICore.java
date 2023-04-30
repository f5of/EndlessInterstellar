package core;

import annotations.Annotations;
import content.EIContentLoader;
import mindustry.mod.Mod;
import mindustry.world.blocks.storage.CoreBlock;

@Annotations.ModCore
public class EICore extends Mod {
    @Override
    public void loadContent() {
        EIContentLoader.load();
    }
}
