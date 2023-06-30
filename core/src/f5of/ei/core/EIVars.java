package f5of.ei.core;

import f5of.ei.files.InternalFileTree;
import f5of.ei.ui.EIUI;

public class EIVars {
    public static InternalFileTree internalFileTree = new InternalFileTree(EICore.class);

    public static EIUI ui = new EIUI();

    // TODO satellites
    //public static SatellitesController satellitesController = new SatellitesController();
}
