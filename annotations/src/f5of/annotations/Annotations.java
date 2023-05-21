package f5of.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotations {
    public @interface ModCore{

    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.FIELD)
    public @interface Load {
        String prefix() default "";
        String postfix() default "";
        String name() default "@";
    }
}
