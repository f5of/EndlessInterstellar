package f5of.ei.content;

import arc.func.Floatp;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Log;
import mindustry.content.Planets;
import mindustry.graphics.g3d.SunMesh;
import mindustry.type.Planet;

import static mindustry.Vars.universe;

public class EIPlanets {
    public static Floatp AU = () -> Planets.serpulo.orbitRadius;

    public static Planet smallWhite, alpha, beta;

    public static void load() {
        smallWhite = new Planet("small-white", Planets.sun, AU.get()){{
            alwaysUnlocked = true;

            drawOrbit = false;
            orbitRadius = 200f * AU.get();
            solarSystem = this;

            bloom = true;
            accessible = true;

            meshLoader = () -> new SunMesh(
                    this, 6,
                    5, 0.3, 1.7, 1.2, 1,
                    1.1f,
                    Color.valueOf("FCFBF5"),
                    Color.valueOf("47CBFE"),
                    Color.valueOf("37B5FF"),
                    Color.valueOf("C1FFFF"),
                    Color.valueOf("1777E6"),
                    Color.valueOf("4CCAFD")
            );
        }};

        alpha = new Planet("alpha", EIPlanets.smallWhite, 15){{
            alwaysUnlocked = true;

            drawOrbit = true;
            orbitRadius = 1200;
            solarSystem = this;

            bloom = true;
            accessible = true;

            meshLoader = () -> new SunMesh(
                    this, 6,
                    5, 0.3, 1.7, 1.2, 1,
                    1.1f,
                    Color.valueOf("FFD474"),
                    Color.valueOf("FFB853"),
                    Color.valueOf("F2A03D"),
                    Color.valueOf("D17D23"),
                    Color.valueOf("FFD272"),
                    Color.valueOf("99530E")
            );
        }
            @Override
            public float getRotation(){
                float offset = Mathf.randomSeed(-9, 360);
                return (offset + universe.secondsf() / (rotateTime / 360f)) % 360f;
            }
        };

        beta = new Planet("beta", EIPlanets.smallWhite, 8){{
            alwaysUnlocked = true;

            drawOrbit = true;
            orbitRadius = 600;
            solarSystem = this;

            bloom = true;
            accessible = true;

            meshLoader = () -> new SunMesh(
                    this, 6,
                    5, 0.3, 1.7, 1.2, 1,
                    1.1f,
                    Color.valueOf("FFD474"),
                    Color.valueOf("FFB853"),
                    Color.valueOf("F2A03D"),
                    Color.valueOf("D17D23"),
                    Color.valueOf("FFD272"),
                    Color.valueOf("99530E")
            );
        }
            @Override
            public float getRotation(){
                float offset = Mathf.randomSeed(-9, 360);
                return (offset + universe.secondsf() / (rotateTime / 360f) + 180f) % 360f;
            }
        };
    }
}
