package f5of.ei.ui;

import f5of.ei.satellites.SatelliteAccessDialog;
import f5of.ei.satellites.SatellitesDialog;

public class EIUI {
    public SatellitesDialog satellitesDialog;
    public SatelliteAccessDialog satelliteAccessDialog;

    public EIUI() {
        satellitesDialog = new SatellitesDialog();
        satelliteAccessDialog = new SatelliteAccessDialog();
    }

    public void build() {

    }
}
