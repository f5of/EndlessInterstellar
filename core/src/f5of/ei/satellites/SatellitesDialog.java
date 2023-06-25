package f5of.ei.satellites;

import arc.Core;
import arc.graphics.Gl;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.g3d.Camera3D;
import arc.graphics.g3d.PlaneBatch3D;
import arc.graphics.g3d.VertexBatch3D;
import arc.graphics.gl.FrameBuffer;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Ray;
import arc.math.geom.Vec3;
import arc.scene.Element;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Tmp;
import f5of.ei.core.EIVars;
import f5of.ei.graphics.EIShaders;
import f5of.ei.ui.EIUI;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.graphics.g3d.PlanetRenderer;
import mindustry.type.Planet;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class SatellitesDialog extends BaseDialog {
    public SatellitesRenderer satellitesRenderer = new SatellitesRenderer();
    public PlanetParams state = new PlanetParams();

    public Table selectedSatelliteMenu;

    public Element canvas;

    public float zoom = 1f;

    static Vec3 temp1 = new Vec3();
    public SatellitesDialog() {
        super("@sattelitesdialog");

        shown(this::build);
        hidden(() -> {
            destroy();
            destroySelectedSatelliteMenu();
        });

        // configure listeners
        {
            clicked(KeyCode.mouseRight, () -> {
                destroySelectedSatelliteMenu();
                Satellite satellite = EIVars.satellitesController.getSelected(satellitesRenderer.cam.getMouseRay());
                if (satellite != null)
                    buildSelectedSatelliteMenu(satellite);
            });

            dragged((cx, cy) -> {
                if(Core.input.getTouches() > 1) return;

                Vec3 pos = state.camPos;

                float upV = pos.angle(Vec3.Y);
                float xscale = 9f, yscale = 10f;
                float margin = 1;

                float speed = 1f - Math.abs(upV - 90) / 90f;

                pos.rotate(state.camUp, cx / xscale * speed);

                float amount = cy / yscale;
                amount = Mathf.clamp(upV + amount, margin, 180f - margin) - upV;

                pos.rotate(Tmp.v31.set(state.camUp).rotate(state.camDir, 90), amount);
            });

            addListener(new InputListener(){
                @Override
                public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY){
                    if(event.targetActor == SatellitesDialog.this){
                        zoom = Mathf.clamp(zoom + amountY / (10f / zoom), 1, 2000f);
                    }
                    return true;
                }
            });

            addCaptureListener(new ElementGestureListener(){
                float lastZoom = -1f;

                @Override
                public void zoom(InputEvent event, float initialDistance, float distance){
                    if(lastZoom < 0){
                        lastZoom = zoom;
                    }

                    zoom = (Mathf.clamp(initialDistance / distance * lastZoom, 1, 2000f));
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                    lastZoom = zoom;
                }
            });
        }
    }

    void destroySelectedSatelliteMenu() {
        selectedSatelliteMenu.clear();
        selectedSatelliteMenu.userObject = null;
        selectedSatelliteMenu.visible = false;
    }

    void separator(Table t) {
        t.image(Tex.whiteui, Pal.accent).growX().height(2).pad(5);
        t.row();
    }

    void buildSelectedSatelliteMenu(Satellite satellite) {
        Runnable r = () -> {
            EIVars.ui.satellitesDialog.satellitesRenderer.cam.project(temp1.set(satellite.position));
            selectedSatelliteMenu.setPosition(temp1.x, temp1.y);
        };
        r.run();
        selectedSatelliteMenu.update(r);

        selectedSatelliteMenu.userObject = satellite;
        selectedSatelliteMenu.visible = true;

        selectedSatelliteMenu.margin(10);

        selectedSatelliteMenu.table(info -> {
            info.background(Styles.black6);
            info.add("@settings");
            info.row();
            separator(info);

            info.label(() -> {
                temp1.set(satellite.velocity).scl(10000);
                return Strings.format("Velocity: @", Strings.fixed(temp1.len(), 1));
            }).fillX();
            info.row();

            info.button("@satellitedestroy", Styles.nonet, () -> {
                EIVars.satellitesController.satellites.remove(satellite);
                destroySelectedSatelliteMenu();
            }).fillX().left();
            info.row();
            info.button("@configsatellite", Styles.nonet, () -> {
                EIVars.ui.satelliteAccessDialog.show(satellite);
            }).fillX().left();
            info.row();
        }).minWidth(300);

        selectedSatelliteMenu.right().bottom();
    }

    static Vec3 o = new Vec3(1, 1, 0).nor();
    public void build() {
        cont.fill(t -> {
            canvas = new Element() {
                @Override
                public void draw() {
                    super.draw();
                    satellitesRenderer.render(state);
                }
            };
            canvas.setFillParent(true);
            t.add(canvas).center();
        });

        {
            Table t = new Table();
            cont.addChild(t);
            t.visible = false;
            selectedSatelliteMenu = t;
        }

        cont.fill(t -> {
            t.button("@back", this::hide).pad(3).margin(10).width(200);
            t.left().bottom();
        });

        cont.fill(t -> {
            t.button("@createsatellite", Styles.flatt, () -> {
                EIVars.satellitesController.satellites.add(Satellite.create(Planets.serpulo, 2,
                        new Vec3(0, 0, 1), o.rotate(new Vec3(1.0f, 0, 0), 10).nor(), s -> {
                    Satellite.startToCircleOrbit(s);
                }));
            }).pad(3).margin(10).width(200);
            t.right().top();
        });

        // build planet selector
        cont.fill(t -> {
            ObjectMap<Planet, Seq<Planet>> stars = new ObjectMap<>();
            Vars.content.planets().each(planet -> {
                if (!planet.visible()) return;
                Planet star = getPlanetStar(planet);
                if (!stars.containsKey(star))
                    stars.put(star, new Seq<>());
                stars.get(star).add(planet);
            });


            stars.each((star, planets) -> {
                t.table(st -> {
                    st.setBackground(Styles.black6);
                    Collapser collapser = new Collapser((table) -> {
                        planets.each(planet -> {
                            table.button(planet.localizedName, getPlanetIcon(planet), Styles.flatTogglet, () -> {
                                state.planet = planet;
                                state.solarSystem = star;
                            }).width(200).height(40).update(bb -> bb.setChecked(state.planet == planet))
                                    .with(e -> e.marginLeft(10)).padLeft(25).get().setColor(planet.iconColor);
                            table.row();
                        });
                    }, true);
                    st.button(star.localizedName, getPlanetIcon(star), Styles.flatTogglet, collapser::toggle).with(e -> e.marginLeft(10)).width(225).height(40);
                    st.row();
                    st.add(collapser);
                }).left();
                t.row();
            });

            t.left().top();
        });
    }

    TextureRegionDrawable getPlanetIcon(Planet planet) {
        return Icon.icons.get(planet.icon + "Small", Icon.icons.get(planet.icon, Icon.commandRallySmall));
    }

    Planet getPlanetStar(Planet planet) {
        return planet.parent == null ? planet : getPlanetStar(planet.parent);
    }

    public void destroy() {
        cont.clear();
        Sounds.back.play();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        state.zoom = Mathf.lerpDelta(state.zoom, zoom, 0.4f);
    }

    public final class SatellitesRenderer {
        public VertexBatch3D batch = new VertexBatch3D(false, true, 0);
        public FrameBuffer buffer = new FrameBuffer(2, 2, true);
        public PlaneBatch3D projector = new PlaneBatch3D();

        public Camera3D cam = new Camera3D();

        public SatellitesRenderer() {
            projector.setScaling(1f / 10000f);
            cam.fov = 60f;
            cam.far = 10000f;
            cam.near = 0.1f;
        }

        public void render(PlanetParams params){
            Draw.flush();
            Gl.clear(Gl.depthBufferBit);
            Gl.enable(Gl.depthTest);
            Gl.depthMask(true);

            Gl.disable(Gl.cullFace);
            //Gl.cullFace(Gl.back);

            int w = params.viewW <= 0 ? Core.graphics.getWidth() : params.viewW;
            int h = params.viewH <= 0 ? Core.graphics.getHeight() : params.viewH;

            Vars.renderer.planets.bloom.blending = !params.drawSkybox;

            cam.up.set(Vec3.Y);

            cam.resize(w, h);
            params.camPos.setLength((params.planet.radius + params.planet.camRadius) * PlanetRenderer.camLength + (params.zoom-1f) * (params.planet.radius + params.planet.camRadius) * 2);

            if(params.otherCamPos != null){
                cam.position.set(params.otherCamPos).lerp(params.planet.position, params.otherCamAlpha).add(params.camPos);
            }else{
                cam.position.set(params.planet.position).add(params.camPos);
            }
            cam.lookAt(params.planet.position);
            cam.update();
            params.camUp.set(cam.up);
            params.camDir.set(cam.direction);

            Vars.renderer.planets.projector.proj(cam.combined);
            batch.proj(cam.combined);

            Vars.renderer.planets.bloom.resize(w, h);
            Vars.renderer.planets.bloom.capture();

            if(params.drawSkybox){
                Vec3 lastPos = Tmp.v31.set(cam.position);
                cam.position.setZero();
                cam.update();

                Gl.depthMask(false);

                Vars.renderer.planets.skybox.render(cam.combined);

                Gl.depthMask(true);

                cam.position.set(lastPos);
                cam.update();
            }

            renderPlanet(params.solarSystem, params);
            renderTransparent(params.solarSystem, params);

            EIVars.satellitesController.satellites.each(Satellite::draw);

            Vars.renderer.planets.bloom.render();

            Gl.enable(Gl.blend);

            Gl.disable(Gl.cullFace);
            Gl.disable(Gl.depthTest);

            cam.update();

            Draw.flush();
        }

        public void renderPlanet(Planet planet, PlanetParams params){
            if(!planet.visible()) return;

            cam.update();

            if(cam.frustum.containsSphere(planet.position, planet.clipRadius)){
                planet.draw(params, cam.combined, planet.getTransform(Vars.renderer.planets.mat));
            }

            for(Planet child : planet.children){
                renderPlanet(child, params);
            }
        }

        public void renderTransparent(Planet planet, PlanetParams params){
            if(!planet.visible()) return;

            planet.drawClouds(params, cam.combined, planet.getTransform(Vars.renderer.planets.mat));

            if(cam.frustum.containsSphere(planet.position, planet.clipRadius) && planet.parent != null && planet.hasAtmosphere && (params.alwaysDrawAtmosphere || Core.settings.getBool("atmosphere"))){
                planet.drawAtmosphere(Vars.renderer.planets.atmosphere, cam);
            }

            for(Planet child : planet.children){
                renderTransparent(child, params);
            }

            batch.proj(cam.combined);
        }
    }
}
