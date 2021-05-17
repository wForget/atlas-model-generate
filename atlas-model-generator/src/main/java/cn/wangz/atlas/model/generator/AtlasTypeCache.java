package cn.wangz.atlas.model.generator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.atlas.model.typedef.AtlasBaseTypeDef;
import org.apache.atlas.model.typedef.AtlasRelationshipEndDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.atlas.type.AtlasType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import cn.wangz.atlas.model.generator.utils.StringHelper;

public class AtlasTypeCache {

    private static final String PACKAGE_PREFIX = "cn.wangz.atlas.model.entity.";
    private static final List<AtlasTypesDef> atlasTypes = new ArrayList<>();
    private static final Map<String, String> atlasTypeClassMap = new HashMap<>();
    private static final Map<String, List<Pair<String, Pair<AtlasRelationshipEndDef, AtlasRelationshipEndDef>>>> atlasTypeRelationshipDefMap = new HashMap<>();

    static {
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_BOOLEAN, Boolean.class.getName());
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_BYTE, Byte.class.getName());
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_SHORT, Short.class.getName());
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_INT, Integer.class.getName());
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_LONG, Long.class.getName());
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_FLOAT, Float.class.getName());
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_DOUBLE, Double.class.getName());
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_BIGINTEGER, BigInteger.class.getName());
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_BIGDECIMAL, BigDecimal.class.getName());
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_STRING, String.class.getName());
        atlasTypeClassMap.put(AtlasBaseTypeDef.ATLAS_TYPE_DATE, Date.class.getName());

        try {
            load();
        } catch (IOException e) {
            throw new RuntimeException("load atlas type definition fail.", e);
        }
    }


    public static String getAtlasTypeClass(String typeName) {
        typeName = typeName.trim().toLowerCase();
        if (typeName.startsWith("array<")) {
            String subType = getAtlasTypeClass(typeName.substring("array<".length(), typeName.length() - 1));
            return "java.util.List<" + subType + ">";
        }
        if (typeName.startsWith("map<")) {
            String[] arr = typeName.substring("map<".length(), typeName.length() - 1).split(",");
            String subTypeLeft = getAtlasTypeClass(arr[0]);
            String subTypeRight = getAtlasTypeClass(arr[1]);
            return "java.util.Map<" + subTypeLeft + "," + subTypeRight + ">";
        }
        return atlasTypeClassMap.get(typeName);
    }

    public static List<AtlasTypesDef> getAtlasTypes() {
        return atlasTypes;
    }

    public static List<Pair<String, Pair<AtlasRelationshipEndDef, AtlasRelationshipEndDef>>> getAtlasRelationshipDef(String typeName) {
        return atlasTypeRelationshipDefMap.getOrDefault(typeName, new ArrayList<>());
    }

    public static String getClassName(AtlasBaseTypeDef baseTypeDef) {
        return StringHelper.toUpperCamel(baseTypeDef.getName());
    }

    public static String getPackageName(AtlasBaseTypeDef baseTypeDef) {
        String packageSuffix = baseTypeDef.getServiceType();
        if (StringUtils.isBlank(packageSuffix)) {
            packageSuffix = "others";
        }
        return PACKAGE_PREFIX + packageSuffix;
    }

    public static String getFullClassName(AtlasBaseTypeDef baseTypeDef) {
        return getPackageName(baseTypeDef) + "." + getClassName(baseTypeDef);
    }

    private static void load() throws IOException {
        String atlasHomeDir  = System.getProperty("atlas.home");
        String modelsDirName = (StringUtils.isEmpty(atlasHomeDir) ? "." : atlasHomeDir) + File.separator + "models";
        File file = new File(modelsDirName);
        Collection<File> files = FileUtils.listFiles(file, new String[]{"json"}, true);
        if (files == null || files.isEmpty()) {
            return;
        }
        for (File modelFile: files) {
            loadModelFile(modelFile);
        }
    }

    private static void loadModelFile(File modelFile) throws IOException {
        String s = FileUtils.readFileToString(modelFile, "UTF-8");
        AtlasTypesDef atlasTypesDef = AtlasType.fromJson(s, AtlasTypesDef.class);
        atlasTypes.add(atlasTypesDef);
        atlasTypesDef.getEnumDefs().forEach(atlasEnumDef -> {
            atlasTypeClassMap.put(atlasEnumDef.getName().toLowerCase(), getFullClassName(atlasEnumDef));
        });
        atlasTypesDef.getStructDefs().forEach(atlasEnumDef -> {
            atlasTypeClassMap.put(atlasEnumDef.getName().toLowerCase(), getFullClassName(atlasEnumDef));
        });
        atlasTypesDef.getEntityDefs().forEach(atlasEntityDef -> {
            atlasTypeClassMap.put(atlasEntityDef.getName().toLowerCase(), getFullClassName(atlasEntityDef));
        });
        atlasTypesDef.getRelationshipDefs().forEach(atlasRelationshipDef -> {
            AtlasRelationshipEndDef endDef1 = atlasRelationshipDef.getEndDef1();
            AtlasRelationshipEndDef endDef2 = atlasRelationshipDef.getEndDef2();
            if (endDef1.getIsLegacyAttribute()) {
                String key = endDef1.getType().toLowerCase();
                if (atlasTypeRelationshipDefMap.get(key) == null) {
                    atlasTypeRelationshipDefMap.put(key, new ArrayList<>());
                }
                atlasTypeRelationshipDefMap.get(key).add(Pair.of(atlasRelationshipDef.getName(), Pair.of(endDef1, endDef2)));
            }
            if (endDef2.getIsLegacyAttribute()) {
                String key = endDef2.getType().toLowerCase();
                if (atlasTypeRelationshipDefMap.get(key) == null) {
                    atlasTypeRelationshipDefMap.put(key, new ArrayList<>());
                }
                atlasTypeRelationshipDefMap.get(key).add(Pair.of(atlasRelationshipDef.getName(), Pair.of(endDef2, endDef1)));
            }
        });
    }

}
