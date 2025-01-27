package sootup.java.bytecode.frontend.inputlocation;

import categories.TestCategories;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.java.core.views.JavaView;

@Tag(TestCategories.JAVA_8_CATEGORY)
public class FixJars extends BaseFixJarsTest {

@Test
public void executeopenamcassandraembeddedjar(){
	String jarDownloadUrl = "https://repo1.maven.org/maven2/org/openidentityplatform/openam/openam-cassandra-embedded/15.1.3/openam-cassandra-embedded-15.1.3.jar";
    String methodSignature = "<org.openidentityplatform.openam.cassandra.embedded.Server: void run()>";
    JavaView javaView = supplyJavaView(jarDownloadUrl);
    assertMethodConversion(javaView,methodSignature);
    assertJar(javaView);
}

}