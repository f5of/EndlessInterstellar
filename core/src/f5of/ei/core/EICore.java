package f5of.ei.core;

import f5of.annotations.Annotations;
import f5of.ei.content.EIContentLoader;
import mindustry.mod.Mod;

@Annotations.ModCore
public class EICore extends Mod {
    @Override
    public void loadContent() {
        EIContentLoader.load();
    }
}