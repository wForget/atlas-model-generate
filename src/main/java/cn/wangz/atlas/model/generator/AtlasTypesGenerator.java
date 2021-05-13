package cn.wangz.atlas.model.generator;

import static com.github.javaparser.ast.Modifier.Keyword.*;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.atlas.model.typedef.AtlasTypesDef;

import cn.wangz.atlas.model.generator.model.BaseEntity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.utils.SourceRoot;

public class AtlasTypesGenerator {

    public void generate(AtlasTypesDef atlasTypesDef) {
        atlasTypesDef.getEntityDefs().forEach(atlasEntityDef -> {

        });
    }

//    private void generate(AtlasEntityDef atlasEntityDef) {

    public static void main(String[] args) {
        String clazzName = "Test";
        CompilationUnit compilationUnit = new CompilationUnit();
        ClassOrInterfaceDeclaration myClass = compilationUnit
                .addClass(clazzName)
                .addExtendedType(BaseEntity.class)
                .setPublic(true);
        myClass.addField(int.class, "A_CONSTANT", PUBLIC, STATIC);
        myClass.addField(String.class, "name", PRIVATE);
        String code = myClass.toString();

        File file = new File("F:\\git\\atlas-model-generator\\target\\test.java");
        compilationUnit.setStorage(file.toPath(), Charset.forName("UTF-8"));
        compilationUnit.getStorage().get().save();

    }

}
