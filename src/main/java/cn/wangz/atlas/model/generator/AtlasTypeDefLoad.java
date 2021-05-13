package cn.wangz.atlas.model.generator;

import java.io.File;
import java.io.IOException;

import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.atlas.type.AtlasType;
import org.apache.commons.io.FileUtils;

public class AtlasTypeDefLoad {

    public AtlasTypesDef loadAtlasTypesDef(File modelFile) throws IOException {
        String s = FileUtils.readFileToString(modelFile, "UTF-8");
        return AtlasType.fromJson(s, AtlasTypesDef.class);
    }

}
