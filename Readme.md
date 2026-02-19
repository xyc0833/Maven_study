## maven

本地仓库的路径：

/Users/xuyaochen/.m2

Maven依赖作用域
除了三个基本的属性用于定位坐标外，依赖还可以添加以下属性：

type：依赖的类型，对于项目坐标定义的packaging。大部分情况下，该元素不必声明，其默认值为jar
scope：依赖的范围（作用域，着重讲解）
optional：标记依赖是否可选
exclusions：用来排除传递性依赖（一个项目有可能依赖于其他项目，就像我们的项目，如果别人要用我们的项目作为依赖，那么就需要一起下载我们项目的依赖，如Lombok）
我们着重来讲解一下scope属性，它决定了依赖的作用域范围：

compile ：为默认的依赖有效范围。如果在定义依赖关系的时候，没有明确指定依赖有效范围的话，则默认采用该依赖有效范围。此种依赖，在编译、运行、测试时均有效。
provided ：在编译、测试时有效，但是在运行时无效，也就是说，项目在运行时，不需要此依赖，比如我们上面的Lombok，我们只需要在编译阶段使用它，编译完成后，实际上已经转换为对应的代码了，因此Lombok不需要在项目运行时也存在。
runtime ：在运行、测试时有效，但是在编译代码时无效。比如我们如果需要自己写一个JDBC实现，那么肯定要用到JDK为我们指定的接口，但是实际上在运行时是不用自带JDK的依赖，因此只保留我们自己写的内容即可。
test ：只在测试时有效，例如：JUnit，我们一般只会在测试阶段使用JUnit，而实际项目运行时，我们就用不到测试了


system：作用域和provided是一样的，但是它不是从远程仓库获取，而是直接导入本地Jar包：

<dependency>
     <groupId>javax.jntm</groupId>
     <artifactId>lbwnb</artifactId>
     <version>2.0</version>
     <scope>system</scope>
     <systemPath>C://学习资料/4K高清无码/test.jar</systemPath>
</dependency>

## <packaging>pom</packaging>这一行能否解释一下作用

你想了解 `<packaging>pom</packaging>` 这行配置的具体作用，这是 Maven 多模块项目中最核心的配置之一，我会用通俗易懂的方式讲清楚它的含义、作用场景和核心功能，让你明白为什么父项目必须用这个配置。

### 核心定义
`<packaging>` 是 Maven 中指定**项目打包类型**的核心配置，`pom` 是其中一种特殊类型，专门用于**多模块项目的父项目/聚合项目**，它的核心作用是：**该项目不生成可执行构件（如 jar/war），仅作为“配置容器”管理子模块、统一依赖/插件版本**。

### 一、`<packaging>pom</packaging>` 的核心作用
#### 1. 标识项目角色：父/聚合项目
Maven 会根据 `packaging` 的值识别项目类型：
- `jar`：普通 Java 项目，打包生成 `.jar` 文件（如你的 Child1 子模块）；
- `war`：Web 项目，打包生成 `.war` 文件；
- `pom`：**聚合/父项目**，不编译代码、不生成构件，仅负责：
    - 聚合多个子模块（通过 `<modules>` 节点指定）；
    - 统一管理子模块的依赖版本、插件版本、编译配置（如 JDK 版本）；
    - 传递通用配置给子模块（子模块通过 `<parent>` 继承）。

#### 2. 改变 Maven 生命周期行为
Maven 不同打包类型对应不同的生命周期：
- `jar/war` 类型：执行 `mvn package` 时会执行「编译 → 测试 → 打包生成构件」等完整步骤；
- `pom` 类型：执行 `mvn package` 时，**仅验证配置、触发子模块的构建**，自身不会编译代码或生成 jar/war，这也是你父项目没有 jar 文件的根本原因。

#### 3. 支持依赖/插件的集中管理
父项目用 `pom` 类型时，可通过以下节点实现“统一管理”，子模块无需重复配置：
- `<dependencyManagement>`：统一管理所有子模块的依赖版本（子模块引用时无需写版本号）；
- `<pluginManagement>`：统一管理所有子模块的插件版本（如你父项目中的 `maven-surefire-plugin`）；
- `<properties>`：统一定义全局变量（如 JDK 版本、Lombok 版本）。

### 二、实战对比：pom vs jar 类型
#### 1. 父项目（pom 类型）典型配置
```xml
<project>
  <groupId>org.example</groupId>
  <artifactId>Maven_study</artifactId>
  <version>1.0-SNAPSHOT</version>
  <!-- 核心：标识为父/聚合项目 -->
  <packaging>pom</packaging>

  <!-- 聚合子模块：执行mvn命令时会自动构建这些子模块 -->
  <modules>
    <module>Child1</module>
    <!-- 可添加更多子模块，如Child2、Child3 -->
  </modules>

  <!-- 统一管理子模块依赖版本 -->
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
      </dependency>
    </dependencies>
  </dependencyManagement>
</project>
```

#### 2. 子模块（jar 类型）典型配置
```xml
<project>
  <parent>
    <groupId>org.example</groupId>
    <artifactId>Maven_study</artifactId>
    <version>1.0-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
  </parent>

  <artifactId>Child1</artifactId>
  <!-- 默认packaging是jar，可省略 -->
  <packaging>jar</packaging>

  <!-- 引用父项目管理的依赖，无需写版本号 -->
  <dependencies>
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
    </dependency>
  </dependencies>
</project>
```

### 三、关键注意事项
1. **父项目必须用 pom 类型**：如果父项目用 `jar` 类型，Maven 会尝试编译父项目的代码（若有）并生成 jar，但失去了“统一管理子模块”的核心价值，不符合多模块项目的最佳实践；
2. **pom 项目也会生成构件，但不是 jar**：执行 `mvn install` 时，pom 项目会把 `pom.xml` 安装到本地仓库（`~/.m2/repository/org/example/Maven_study/1.0-SNAPSHOT/Maven_study-1.0-SNAPSHOT.pom`），供子模块继承使用；
3. **可嵌套使用**：大型项目中，可有多级 pom 项目（如顶级父项目 → 业务模块父项目 → 具体子模块），每层都用 `pom` 类型做分层管理。

### 总结
1. `<packaging>pom</packaging>` 标识项目为**父/聚合项目**，不生成 jar/war，仅做配置管理；
2. 核心功能：聚合子模块、统一管理依赖/插件版本、传递通用配置；
3. 多模块项目中，父项目用 `pom`，子模块用 `jar/war` 是 Maven 的标准最佳实践。

不一定非要带依赖才能运行，但如果你的代码用到了外部依赖（比如 Lombok、日志库等），就必须用包含依赖的 jar 才能正常运行。