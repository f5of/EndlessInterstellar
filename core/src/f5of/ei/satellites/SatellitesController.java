package f5of.ei.satellites;

import arc.math.geom.Intersector3D;
import arc.math.geom.Ray;
import arc.struct.Seq;
import f5of.ei.controllers.GenericController;

public class SatellitesController extends GenericController {
    public final Seq<Satellite> satellites = new Seq<>();

    public SatellitesController() {
        super(30);
    }

    public Satellite getSelected(Ray mouseRay) {
        return satellites.find(satellite -> Intersector3D.intersectRaySphere(mouseRay, satellite.position, 0.1f, null));
    }

    @Override
    public void update() {
        satellites.each(Satellite::update);
    }
}
