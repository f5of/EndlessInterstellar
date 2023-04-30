package content;

import mindustry.world.blocks.storage.CoreBlock;
import world.blocks.prower.BeamTransmitter;

public class EIBlocks {
    public static CoreBlock colonyCore;
    public static BeamTransmitter rayTransmitter;

    public static void load() {
        colonyCore = new CoreBlock("colony-core"){{
            health = 3600;
            armor = 5f;
            size = 4;
            unitCapModifier = 10;
            itemCapacity = 5000;
            alwaysUnlocked = true;
            hasPower = true;
            outputsPower = true;
            consumesPower = false;
            buildType = Build::new;
        }
            class Build extends CoreBlock.CoreBuild {
                @Override
                public float getPowerProduction() {
                    return 200f / 60f;
                }
            }
        };
    }
}
