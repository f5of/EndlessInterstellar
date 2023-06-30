package f5of.ei.content;

import mindustry.content.TechTree;

public class EITechTree extends TechTree {
    public static TechNode root;

    public static void load() {
        root = nodeRoot("EI", EIBlocks.colonyCore, false, () -> {

        });
    }
}
