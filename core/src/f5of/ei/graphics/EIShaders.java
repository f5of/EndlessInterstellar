package f5of.ei.graphics;

import arc.graphics.gl.Shader;
import f5of.ei.core.EIVars;

public class EIShaders {
    public static SatelliteShader satelliteShader;

    public static void load() {
        satelliteShader = new SatelliteShader();
    }

    public static class SatelliteShader extends Shader {
        public SatelliteShader() {
            super(EIVars.internalFileTree.child("shaders/quad.vert"),
                    EIVars.internalFileTree.child("shaders/quad.frag"));
        }
    }
}
