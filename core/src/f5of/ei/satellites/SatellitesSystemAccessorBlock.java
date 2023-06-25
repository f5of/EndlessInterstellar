package f5of.ei.satellites;

import arc.scene.ui.layout.Table;
import f5of.ei.core.EIVars;
import f5of.ei.ui.EIUI;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class SatellitesSystemAccessorBlock extends Block {
    public SatellitesSystemAccessorBlock(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
        group = BlockGroup.logic;

        envEnabled = Env.any;
    }

    public class SatellitesSystemAccessorBuild extends Building {
        @Override
        public void buildConfiguration(Table table) {
            EIVars.ui.satellitesDialog.show();
        }
    }
}
