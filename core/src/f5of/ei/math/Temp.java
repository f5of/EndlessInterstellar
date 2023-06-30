package f5of.ei.math;

import arc.func.Prov;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import arc.struct.Seq;

// TODO poor
public class Temp {
    public static final ObjectMap<Class<?>, TypeEntry<?>> types = new ObjectMap<>();

    static {
        setConstructor(Vec2.class, Vec2::new);
        setConstructor(Vec3.class, Vec3::new);
        setConstructor(Mat3D.class, Mat3D::new);
    }

    public static void setConstructor(Class<?> clazz, Prov<?> constructor) {
        types.put(clazz, new TypeEntry<>(constructor));
    }

    public static <T> T get(Class<T> clazz) {
        return (T) types.get(clazz).get();
    }

    public static <T> void ret(T object) {
        types.get(object.getClass()).ret(object);
    }

    public static <T> void ret(T... objects) {
        for (T object : objects)
            types.get(object.getClass()).ret(object);
    }

    private final static class TypeEntry<T> {
        public Seq<T> objects;
        public Seq<Boolean> bools;
        public Prov<T> constructor;
        public int index;

        public TypeEntry(Prov<T> c) {
            constructor = c;

            objects = new Seq<>();
            objects.add(constructor.get());
            bools = new Seq<>();
            bools.add(false);
            index++;
        }

        public T get() {
            T out = null;
            for (int i = 0; i < objects.size; i++)
                if (!bools.get(i))
                    out = objects.get(i);
            if (out == null)
                out = add();
            return out;
        }

        public void ret(Object obj) {
            bools.set(objects.indexOf((T) obj), true);
        }

        T add() {
            objects.add(constructor.get());
            bools.add(false);
            return  objects.peek();
        }
    }
}
