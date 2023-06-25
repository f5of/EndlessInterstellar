package f5of.ei.satellites;

import arc.func.Cons;
import arc.graphics.*;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import f5of.ei.core.EIVars;
import f5of.ei.graphics.EIShaders;
import f5of.ei.math.Temp;
import f5of.ei.satellites.parts.SatellitePart;
import mindustry.content.Planets;
import mindustry.type.Planet;

public class Satellite {
    public static float planetMass = 0.00001f;

    static public Mesh quad;
    static public Texture iconTexture;

    static {
        float[] vertices = new float[]{
                -0.5f, -0.5f, 0, 0, 0,
                -0.5f, 0.5f, 0, 0, 1,
                0.5f, -0.5f, 0, 1, 0,
                -0.5f, 0.5f, 0, 0, 1,
                0.5f, 0.5f, 0, 1, 1,
                0.5f, -0.5f, 0, 1, 0
        };
        quad = new Mesh(true, vertices.length, 0, VertexAttribute.position3, VertexAttribute.texCoords);
        quad.getVerticesBuffer().limit(vertices.length);
        quad.getVerticesBuffer().put(vertices, 0, vertices.length);

        iconTexture = new Texture(TextureData.load(EIVars.internalFileTree.child("sprites/satellite.png"), false));
    }

    public static Satellite create(Planet owner, float orbitHeight, Vec3 origin, Vec3 flightOrigin, Cons<Satellite> cons) {
        Satellite out = new Satellite();

        Vec3 temp1 = Temp.get(Vec3.class);

        out.planet = owner;
        out.position.set(out.planet.position).add(temp1.set(origin).nor().scl(orbitHeight));
        out.velocity.set(flightOrigin);

        Temp.ret(temp1);

        cons.get(out);
        return out;
    }

    public static void startToCircleOrbit(Satellite satellite) {
        float v = Mathf.sqrt(planetMass / satellite.position.dst(satellite.planet.position));
        satellite.velocity.nor().scl(v);
    }

    public Seq<SatellitePart> parts = new Seq<>();

    public Planet planet = Planets.serpulo;
    public Vec3 position = new Vec3();
    public Vec3 velocity = new Vec3();

    public Mat3D model = new Mat3D();

    public ObjectMap<String, Object> data = new ObjectMap<>();

    public Satellite() {
        model.setToScaling(0.1f, 0.1f, 0.1f);
    }

    static Vec3 temp = new Vec3();
    public void update() {
        float dist = planet.position.dst2(position);
        float f = planetMass / dist;

        velocity.add(temp.set(planet.position).sub(position).nor().scl(f));

        position.add(velocity);

        parts.each(SatellitePart::update);
    }

    public void renderOrbit(){

    }

    static Mat3D trans = new Mat3D();
    public void draw() {
        renderOrbit();

        trans.setTranslation(position);

        iconTexture.bind();
        EIShaders.satelliteShader.bind();
        EIShaders.satelliteShader.apply();
        EIShaders.satelliteShader.setUniformMatrix4("u_model", model.val);
        EIShaders.satelliteShader.setUniformMatrix4("u_trans", trans.val);
        EIShaders.satelliteShader.setUniformMatrix4("u_proj",
                EIVars.ui.satellitesDialog.satellitesRenderer.cam.combined.val);
        quad.render(EIShaders.satelliteShader, Gl.triangles);

        parts.each(SatellitePart::draw);
    }

}

