## Atlas Model Generator(Incomplete)

### Introduce

Apache Atlas 项目通过 Json 格式定义 model 元数据模型。第三方系统对接 Atlas 时，通过创建 AtlasEntity 对象定义元数据信息，构造对象时通过 Map 对象设置属性。

Atlas Model Generator 项目，根据 models 定义的 json 文件，生成 Java Bean 对象，使得创建元数据信息更加方便和规范。

### Use Steps

1. 编译 generator

```
cd $PROJECT_HOME

mvn clean -DskipTests install
```

2. 执行 generator 生成代码

```
mvn exec:exec
```

3. atlas-models 模块

生成的 Java Bean 代码在 atlas-models/src/main/java 中，可将 atlas-models 模块进行打包然后提供给用户使用。

 