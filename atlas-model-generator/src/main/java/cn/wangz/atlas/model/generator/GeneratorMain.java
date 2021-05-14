package cn.wangz.atlas.model.generator;

import java.io.File;

import com.github.javaparser.utils.SourceRoot;

public class GeneratorMain {

    private static final String MODEL_SOURCE_PATH = "atlas-models/src/main/java";

    public static void main(String[] args) {
        System.out.println(args);
        String rooPath = MODEL_SOURCE_PATH;
        if (args.length > 0) {
            rooPath = args[0];
        }
        System.out.println(rooPath);
        File file = new File(MODEL_SOURCE_PATH);
        SourceRoot sourceRoot = new SourceRoot(file.toPath());
        AtlasTypeCache.getAtlasTypes()
                .forEach(atlasTypesDef -> {
                    new AtlasTypesDefGenerator(atlasTypesDef, sourceRoot).generate();
                });
    }

}
