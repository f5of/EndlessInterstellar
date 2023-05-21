package f5of.processors;

import arc.util.Reflect;
import f5of.annotations.Annotations;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import com.squareup.javapoet.*;
import mindustry.ctype.Content;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@SupportedOptions({"ProjectName", "OutputPackage"})
@SupportedSourceVersion(SourceVersion.RELEASE_16)
@SupportedAnnotationTypes("f5of.annotations.Annotations.Load")
public class LoadProcessor extends AbstractProcessor {
    String projectName;
    String outputPackage;

    Types types;
    Elements elements;

    Messager messager;

    int generation = 0;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        Log.logger = (l, t) -> {
            messager.printMessage(l == Log.LogLevel.debug ? Diagnostic.Kind.NOTE:
                            l == Log.LogLevel.info ? Diagnostic.Kind.NOTE :
                                    l == Log.LogLevel.warn ? Diagnostic.Kind.WARNING :
                                            l == Log.LogLevel.err ? Diagnostic.Kind.NOTE :
                                                    Diagnostic.Kind.OTHER,
                    Log.format((
                            l == Log.LogLevel.debug ? "&lc&fb" :
                                    l == Log.LogLevel.info ? "&fb" :
                                            l == Log.LogLevel.warn ? "&ly&fb" :
                                                    l == Log.LogLevel.err ? "&lr&fb" :
                                                            "") + t + "&fr"));
        };
        super.init(processingEnv);
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();

        projectName = get("ProjectName", "none");
        outputPackage = get("OutputPackage", "none");

        Log.info("Handling project @.", projectName);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (generation == 0)
            processLoad(roundEnv);

        generation++;
        return true;
    }

    void processLoad(RoundEnvironment roundEnv) {
        ObjectMap<TypeElement, Seq<VariableElement>> elements = new ObjectMap<>();

        roundEnv.getElementsAnnotatedWith(Annotations.Load.class).forEach(element -> {
            if (element instanceof VariableElement v) {
                if (!v.getModifiers().contains(Modifier.PUBLIC) ||
                        v.getModifiers().contains(Modifier.FINAL) ||
                        v.getModifiers().contains(Modifier.STATIC)) {
                    Log.warn("Fields annotated with annotations.Annotations.Load should" +
                            " be public, not final and not static.");
                    return;
                }
                if (!elements.containsKey((TypeElement) v.getEnclosingElement()))
                    elements.put((TypeElement) v.getEnclosingElement(), new Seq<>());
                elements.get((TypeElement) v.getEnclosingElement()).add(v);
            }
        });

        var builder = TypeSpec.classBuilder("Loads").addModifiers(Modifier.PUBLIC);
        var loader = MethodSpec.methodBuilder("load").addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.get(Content.class), "content").build());

        elements.each((type, elems) -> {
            String typeName = type.toString();
            loader.beginControlFlow(Strings.format("if (content instanceof @ c)", typeName));
            elems.each(elem -> {
                String elemName = elem.toString();
                Annotations.Load anno = elem.getAnnotation(Annotations.Load.class);
                loader.addCode(Strings.format("c.@ = arc.Core.atlas.find((\"@@@\").replaceAll(@, c.name));",
                        elemName, anno.prefix(), anno.name(), anno.postfix(), "\"@\""));
            });
            loader.endControlFlow();
        });

        builder.addMethod(loader.build());

        try {
            Log.info("Writing file @.", outputPackage + "." + projectName);
            JavaFile.builder(outputPackage + "." + projectName,
                    builder.build()).build().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            Log.err("Class not witten.", e);
        }
    }

    String get(String name, String def) {
        String t = processingEnv.getOptions().get(name);
        return t != null ? t : def;
    }
}
