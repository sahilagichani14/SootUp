package sootup.tests;

import com.squareup.javapoet.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.SourceType;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.PrimitiveType;
import sootup.core.types.VoidType;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;
import sootup.jimple.frontend.JimpleAnalysisInputLocation;
import sootup.jimple.frontend.JimpleStringAnalysisInputLocation;
import sootup.jimple.frontend.JimpleView;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Java8")
public class JimpleSerializationTest {

    @Test
    public void testTrapSerialization() {
        AnalysisInputLocation inputLocation = new JavaClassPathAnalysisInputLocation("src/test/resources/bugs/1119_trap-serialization", SourceType.Application, Collections.emptyList());
        JavaView view = new JavaView(inputLocation);

        Optional<JavaSootMethod> methodOpt = view.getMethod(view.getIdentifierFactory().parseMethodSignature(
                "<com.linecorp.centraldogma.server.internal.storage.repository.git.GitRepository: java.util.Map blockingFind(com.linecorp.centraldogma.common.Revision,java.lang.String,java.util.Map)>"
        ));
        assertTrue(methodOpt.isPresent());
        JavaSootMethod method = methodOpt.get();
        method.getBody().toString();
    }

    @Test
    public void testBasicTrapSerialization() {
        AnalysisInputLocation inputLocation = new JavaClassPathAnalysisInputLocation("src/test/resources/bugs/1119_trap-serialization", SourceType.Application, Collections.emptyList());
        JavaView javaView = new JavaView(inputLocation);
        Optional<JavaSootMethod> nestedTrap = javaView.getMethod(javaView.getIdentifierFactory().parseMethodSignature(
                "<com.linecorp.centraldogma.server.internal.storage.repository.git.TrapSerialization: java.lang.Integer processWithExplicitCasting(java.lang.String,java.lang.String)>"
        ));

        assertTrue(nestedTrap.isPresent());
        JavaSootMethod nestedTrapMethod = nestedTrap.get();
        System.out.println(nestedTrapMethod.getBody());
    }

    @Test
    public void testJimpleTrapSerialization() {
        String jimpleString = "class DummyClass extends java.lang.Object {\n" +
                "\tint testTrapSerialization() {\n" +
                "\t\tsootUp.RQ1.jb_a.JB_A this;\n" +
                "\t\tunknown $stack4, $stack5, a, b, e;\n" +
                "\n" +
                "\n" +
                "\t\tthis := @this: sootUp.RQ1.jb_a.JB_A;\n" +
                "\t\ta = 0;\n" +
                "\t\tb = 10;\n" +
                "\n" +
                "\t  label1:\n" +
                "\t\tb = b / a;\n" +
                "\t\t$stack4 = b;\n" +
                "\t\treturn a;\n" +
                "\n" +
                "\t  label2:\n" +
                "\t\treturn $stack4;\n" +
                "\n" +
                "\t  label3:\n" +
                "\t\t$stack5 := @caughtexception;\n" +
                "\t\te = $stack5;\n" +
                "\n" +
                "\t\treturn b;\n" +
                "\n" +
                "\t catch java.lang.ArithmeticException from label1 to label2 with label3;\n" +
                "\t catch java.lang.NullPointerException from label1 to label2 with label3;\n" +
                "\t}\n" +
                "}";

        JimpleStringAnalysisInputLocation analysisInputLocation = new JimpleStringAnalysisInputLocation(jimpleString, SourceType.Application, Collections.emptyList());
        JimpleView view = new JimpleView(analysisInputLocation);
        assertTrue(view.getClass(analysisInputLocation.getClassType()).isPresent());
        MethodSignature methodSig =
                view.getIdentifierFactory()
                        .getMethodSignature(
                                analysisInputLocation.getClassType(),
                                "testTrapSerialization",
                                PrimitiveType.IntType.getInstance(),
                                Collections.emptyList());
        assertTrue(view.getMethod(methodSig).isPresent());
    }


    @Test
    public void addNopInEndOfTryCatchFinally() throws IOException {
        // Define the method body
        CodeBlock methodBody = CodeBlock.builder()
                .addStatement("$T<String, $T<?>> result = new $T<>()", Map.class, Map.Entry.class, HashMap.class)
                .beginControlFlow("try")
                .addStatement("result.put($S, new $T.SimpleEntry<>($S, $S))", "try", AbstractMap.class, "Key1", "Value1")
                .addStatement("return result")
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("result.put($S, new $T.SimpleEntry<>($S, $S))", "catch", AbstractMap.class, "Key2", "Value2")
                .addStatement("return result")
                .nextControlFlow("finally")
                .addStatement("result.put($S, new $T.SimpleEntry<>($S, $S))", "finally", AbstractMap.class, "Key3", "Value3")
                .endControlFlow()
                .addStatement("return result")
                .addStatement("return result")
                .build();

        // Create the method
        MethodSpec tryCatchFinallyMethod = MethodSpec.methodBuilder("tryCatchFinallyMethod")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ParameterizedTypeName.get(Map.class, String.class))
                .addCode(methodBody)
                .build();

        // Create the class
        TypeSpec tryCatchFinallyClass = TypeSpec.classBuilder("TryCatchFinallyExample")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(tryCatchFinallyMethod)
                .build();

        // Write to a Java file
        JavaFile javaFile = JavaFile.builder("com.example", tryCatchFinallyClass)
                .build();

        // Write the generated Java file to the file system
        javaFile.writeTo(Paths.get("./src/test/resources/bugs/1119_trap-serialization/com/linecorp/centraldogma/server/internal/storage/repository/git/"));
    }

    @Test
    public void addNopInEndOfNestedTrap() throws IOException {
        // Define the method body with nested try-catch blocks
        CodeBlock methodBody = CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("System.out.println($S)", "Outer try block")
                .beginControlFlow("try")
                .addStatement("System.out.println($S)", "Inner try block")
                .addStatement("throw new RuntimeException($S)", "Inner exception")
                .nextControlFlow("catch (Exception e)")
                .addStatement("System.out.println($S + e.getMessage())", "Caught inner exception: ")
                .endControlFlow()
                .nextControlFlow("catch (Exception e)")
                .addStatement("System.out.println($S + e.getMessage())", "Caught outer exception: ")
                .endControlFlow()
                .build();

        // Create the method
        MethodSpec nestedTryCatchMethod = MethodSpec.methodBuilder("nestedTryCatch")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addCode(methodBody)
                .build();

        // Create the class
        TypeSpec nestedTryCatchClass = TypeSpec.classBuilder("NestedTryCatchExample")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(nestedTryCatchMethod)
                .build();

        // Write to a Java file
        JavaFile javaFile = JavaFile.builder("com.example", nestedTryCatchClass)
                .build();

        // Write the generated Java file to the file system
        javaFile.writeTo(Paths.get("./src/test/resources/bugs/1119_trap-serialization/com/linecorp/centraldogma/server/internal/storage/repository/git/"));
    }

}
