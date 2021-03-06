package cn.wangz.atlas.model.generator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.model.typedef.AtlasEnumDef;
import org.apache.atlas.model.typedef.AtlasRelationshipEndDef;
import org.apache.atlas.model.typedef.AtlasStructDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.utils.SourceRoot;

import cn.wangz.atlas.model.BaseEntity;
import cn.wangz.atlas.model.annotation.AtlasAttribute;
import cn.wangz.atlas.model.annotation.AtlasRelationshipAttribute;
import cn.wangz.atlas.model.generator.utils.StringHelper;

public class AtlasTypesDefGenerator {

    private AtlasTypesDef atlasTypesDef;
    private SourceRoot sourceRoot;


    public AtlasTypesDefGenerator(AtlasTypesDef atlasTypesDef, SourceRoot sourceRoot) {
        this.atlasTypesDef = atlasTypesDef;
        this.sourceRoot = sourceRoot;
    }

    public void generate() {
        // generate enum type
        atlasTypesDef.getEnumDefs()
                .forEach(atlasEnumDef -> {
                    generateEnumDef(sourceRoot, atlasEnumDef);
                });

        // generate enum type
        atlasTypesDef.getStructDefs()
                .forEach(atlasStructDef -> {
                    generateStructDef(sourceRoot, atlasStructDef);
                });

        // generate entity type
        atlasTypesDef.getEntityDefs()
                .forEach(atlasEntityDef -> {
                    generateEntityDef(sourceRoot, atlasEntityDef);
                });

        // save all class file
        sourceRoot.saveAll();
    }

    private CompilationUnit generateEntityDef(SourceRoot sourceRoot, AtlasEntityDef atlasEntityDef) {
        String clazzName = AtlasTypeCache.getClassName(atlasEntityDef);
        String packageName = AtlasTypeCache.getPackageName(atlasEntityDef);

        CompilationUnit compilationUnit = new CompilationUnit();
        sourceRoot.add(packageName, clazzName + ".java", compilationUnit);
        compilationUnit.setPackageDeclaration(packageName);

        ClassOrInterfaceDeclaration atlasTypeClazz = compilationUnit.addClass(clazzName)
                .setPublic(true);

        atlasTypeClazz.addConstructor(Modifier.Keyword.PUBLIC)
                .getBody().addStatement("this.setTypeName(\"" + atlasEntityDef.getName() + "\");");

        // add supper type
        if (atlasEntityDef.getSuperTypes() != null && !atlasEntityDef.getSuperTypes().isEmpty()) {
            if (atlasEntityDef.getSuperTypes().size() > 1) {
                // more super types
                List<String> superTypes = new ArrayList<>(atlasEntityDef.getSuperTypes());
                for (int i = 0; i < superTypes.size(); i++) {
                    String superType = superTypes.get(i);
                    String typeClass = AtlasTypeCache.getAtlasTypeClass(superType);
                    if (i == 0) {
                        atlasTypeClazz.addExtendedType(typeClass);
                        continue;
                    }
                    int index = typeClass.lastIndexOf(".");
                    CompilationUnit supperCu =
                            sourceRoot.parse(typeClass.substring(0, index), typeClass.substring(index + 1) + ".java");
                    Optional<ClassOrInterfaceDeclaration> supperCd =
                            supperCu.getClassByName(typeClass.substring(index + 1));
                    supperCd.get().getFields().forEach(fieldDeclaration -> {
                        if (fieldDeclaration.isAnnotationPresent(AtlasAttribute.class)
                                || fieldDeclaration.isAnnotationPresent(AtlasRelationshipAttribute.class) ) {
                            atlasTypeClazz.addPrivateField(fieldDeclaration.getElementType(),
                                    fieldDeclaration.getVariable(0).getName().getIdentifier())
                                    .setAnnotations(fieldDeclaration.getAnnotations());
                        }
                    });
                }
            } else {
                atlasEntityDef.getSuperTypes().forEach(superType -> {
                    String superTypeClass = AtlasTypeCache.getAtlasTypeClass(superType);
                    atlasTypeClazz.addExtendedType(superTypeClass);
                });
            }
        } else {
            atlasTypeClazz.addExtendedType(BaseEntity.class);
        }

        List<FieldDeclaration> attrFieldList = new ArrayList<>();

        // relationshipDef attribute
        attrFieldList.addAll(
                AtlasTypeCache.getAtlasRelationshipDef(atlasEntityDef.getName().toLowerCase())
                .stream()
                .map(relationshipEndDefPair -> {
                    String relationshipName = relationshipEndDefPair.getLeft();
                    AtlasRelationshipEndDef left = relationshipEndDefPair.getRight().getLeft();
                    AtlasRelationshipEndDef right = relationshipEndDefPair.getRight().getRight();
                    String name = left.getName();
                    String type = AtlasTypeCache.getAtlasTypeClass(right.getType());
                    switch (left.getCardinality()) {
                    case SINGLE:
                        break;
                    case LIST:
                        type = "java.util.List<" + type + ">";
                        break;
                    case SET:
                        type = "java.util.Set<" + type + ">";
                        break;
                    }
                    return addAttrField(atlasTypeClazz, name, type, true, relationshipName);
                }).collect(Collectors.toList()));

        // relation attribute
        if (atlasEntityDef.getRelationshipAttributeDefs() != null) {
            attrFieldList.addAll(atlasEntityDef.getRelationshipAttributeDefs().stream()
                    .map(atlasAttributeDef ->
                        addAttrField(atlasTypeClazz, atlasAttributeDef, true, atlasAttributeDef.getRelationshipTypeName())
                    )
                    .collect(Collectors.toList()));
        }

        // attribute
        if (atlasEntityDef.getAttributeDefs() != null) {
            attrFieldList.addAll(atlasEntityDef.getAttributeDefs().stream()
                    .map(atlasAttributeDef -> addAttrField(atlasTypeClazz, atlasAttributeDef))
                    .collect(Collectors.toList()));
        }

        // create get and set method
        attrFieldList.forEach(fieldDeclaration -> {
            try {
                if (fieldDeclaration == null) {
                    return;
                }
                fieldDeclaration.createGetter();
                fieldDeclaration.createSetter();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });

        return compilationUnit;
    }

    private CompilationUnit generateStructDef(SourceRoot sourceRoot, AtlasStructDef atlasStructDef) {
        String clazzName = AtlasTypeCache.getClassName(atlasStructDef);
        String packageName = AtlasTypeCache.getPackageName(atlasStructDef);

        CompilationUnit compilationUnit = new CompilationUnit();
        sourceRoot.add(packageName, clazzName + ".java", compilationUnit);
        compilationUnit.setPackageDeclaration(packageName);

        ClassOrInterfaceDeclaration atlasTypeClazz = compilationUnit.addClass(clazzName)
                .addExtendedType(BaseEntity.class)
                .setPublic(true);

        // attribute
        if (atlasStructDef.getAttributeDefs() != null) {
            atlasStructDef.getAttributeDefs().forEach(atlasAttributeDef -> {
                addAttrField(atlasTypeClazz, atlasAttributeDef);
            });
        }

        return compilationUnit;
    }

    private CompilationUnit generateEnumDef(SourceRoot sourceRoot, AtlasEnumDef atlasEnumDef) {
        String clazzName = AtlasTypeCache.getClassName(atlasEnumDef);
        String packageName = AtlasTypeCache.getPackageName(atlasEnumDef);

        CompilationUnit compilationUnit = new CompilationUnit();
        sourceRoot.add(packageName, clazzName + ".java", compilationUnit);
        compilationUnit.setPackageDeclaration(packageName);
        EnumDeclaration enumDeclaration = compilationUnit.addEnum(clazzName).setPublic(true);
        enumDeclaration.addField(String.class, "name").createGetter();
        ConstructorDeclaration constructor = enumDeclaration.addConstructor();
        constructor.addParameter(String.class, "name");
        constructor.getBody().addStatement("this.name = name;");
        atlasEnumDef.getElementDefs().stream()
                .sorted(Comparator.comparingInt(v -> v.getOrdinal()))
                .map(v -> {
                    String value = "\"" + v.getValue() + "\"";
                    String name = StringHelper.fixIdentifier(v.getValue());
                    EnumConstantDeclaration enumConstantDeclaration = new EnumConstantDeclaration(name);
                    enumConstantDeclaration.addArgument(value);
                    return enumConstantDeclaration;
                })
                .forEach(e -> enumDeclaration.addEntry(e));

        return compilationUnit;
    }

    private FieldDeclaration addAttrField(ClassOrInterfaceDeclaration atlasTypeClazz,
            AtlasStructDef.AtlasAttributeDef atlasAttributeDef) {
        return addAttrField(atlasTypeClazz, atlasAttributeDef, false, null);
    }

    private FieldDeclaration addAttrField(ClassOrInterfaceDeclaration atlasTypeClazz,
            AtlasStructDef.AtlasAttributeDef atlasAttributeDef, boolean isRelationship, String relationshipName) {
        String attrName = atlasAttributeDef.getName();
        String attrTypeName = atlasAttributeDef.getTypeName();
        String attrClassName = AtlasTypeCache.getAtlasTypeClass(attrTypeName);

        return addAttrField(atlasTypeClazz, attrName, attrClassName, isRelationship, relationshipName);
    }

    private FieldDeclaration addAttrField(ClassOrInterfaceDeclaration atlasTypeClazz,
            String attrName, String attrClassName, boolean isRelationship, String relationshipName) {
        String newAttrName = StringHelper.toLowerCamel(attrName);
        if (StringHelper.isKeyword(attrName)) {
            newAttrName = attrName + "_attr";
        }

        // if field exists, remove it
        if (atlasTypeClazz.getFieldByName(newAttrName).isPresent()) {
            return null;
        }

        FieldDeclaration fieldDeclaration = atlasTypeClazz.addPrivateField(attrClassName, newAttrName);
        NormalAnnotationExpr normalAnnotationExpr;
        if (isRelationship) {
            normalAnnotationExpr = fieldDeclaration.addAndGetAnnotation(AtlasRelationshipAttribute.class);
            if (relationshipName != null) {
                normalAnnotationExpr.addPair("relationShipName", "\"" + relationshipName + "\"");
            }
        } else {
            normalAnnotationExpr = fieldDeclaration.addAndGetAnnotation(AtlasAttribute.class);
        }
        if (!attrName.equals(newAttrName)) {
            normalAnnotationExpr.addPair("name", "\"" + attrName + "\"");
        }
        return fieldDeclaration;
    }

}
