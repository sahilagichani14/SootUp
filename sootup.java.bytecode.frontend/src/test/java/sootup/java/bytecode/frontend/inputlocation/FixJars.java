package sootup.java.bytecode.frontend.inputlocation;

import categories.TestCategories;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.java.core.views.JavaView;

@Tag(TestCategories.JAVA_8_CATEGORY)
public class FixJars extends BaseFixJarsTest {

@Test
public void executewebjcordatestjar(){
	String jarDownloadUrl = "https://repo1.maven.org/maven2/org/web3j/corda/web3j-corda-test/0.2.8/web3j-corda-test-0.2.8.jar";
    String methodSignature = "<org.web3j.corda.assertion.ReflectKt: void hasParameters(assertk.Assert,java.lang.String[])>";
    JavaView javaView = supplyJavaView(jarDownloadUrl);
    assertMethodConversion(javaView,methodSignature);
    assertJar(javaView);
}

}