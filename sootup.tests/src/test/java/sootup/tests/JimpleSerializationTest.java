package sootup.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Java8")
public class JimpleSerializationTest {

    @Test
    public void testTrapSerialization() {
        AnalysisInputLocation inputLocation = new JavaClassPathAnalysisInputLocation("src/test/resources/bugs/1119_trap-serialization");
        JavaView view = new JavaView(inputLocation);

        Optional<JavaSootMethod> methodOpt = view.getMethod(view.getIdentifierFactory().parseMethodSignature(
                "<com.linecorp.centraldogma.server.internal.storage.repository.git.GitRepository: java.util.Map blockingFind(com.linecorp.centraldogma.common.Revision,java.lang.String,java.util.Map)>"
        ));
        assertTrue(methodOpt.isPresent());
        JavaSootMethod method = methodOpt.get();
        method.getBody().toString();
    }
}
