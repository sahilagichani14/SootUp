package sootup.java.bytecode.frontend.inputlocation;

import categories.TestCategories;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.java.core.views.JavaView;

@Tag(TestCategories.JAVA_8_CATEGORY)
public class FixJars extends BaseFixJarsTest {

@Test
public void executeorgqijcoretestsupportjar(){
	String jarDownloadUrl = "https://repo1.maven.org/maven2/org/qi4j/core/org.qi4j.core.testsupport/2.1/org.qi4j.core.testsupport-2.1.jar";
    String methodSignature = "<org.qi4j.test.util.Assume: void assumeConnectivity(java.lang.String,int)>";
    JavaView javaView = supplyJavaView(jarDownloadUrl);
    assertMethodConversion(javaView,methodSignature);
    assertJar(javaView);
}

}