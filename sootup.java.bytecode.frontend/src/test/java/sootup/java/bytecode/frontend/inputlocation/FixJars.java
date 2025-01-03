package sootup.java.bytecode.frontend.inputlocation;

import categories.TestCategories;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.Body;
import sootup.core.model.SourceType;
import sootup.interceptors.NopEliminator;
import sootup.java.core.views.JavaView;
import sootup.jimple.frontend.JimpleAnalysisInputLocation;
import sootup.jimple.frontend.JimpleView;

@Tag(TestCategories.JAVA_8_CATEGORY)
public class FixJars extends BaseFixJarsTest {

  @Test
  public void executeexamcorejar() {
    AnalysisInputLocation inputLocation =
        new JimpleAnalysisInputLocation(
            Paths.get("./src/test/resources/"),
            SourceType.Application,
            Collections.singletonList(new NopEliminator()));
    JimpleView jimpleView = new JimpleView(inputLocation);
    jimpleView
        .getClasses()
        .forEach(
            sootClass -> {
              Body body =
                  sootClass.getMethodsByName("nopEliminatorBug").stream()
                      .findFirst()
                      .get()
                      .getBody();
            });
  }

  @Test
  public void executeaudiofileRCjar() {
    String jarDownloadUrl =
        "https://repo1.maven.org/maven2/de/sciss/audiofile_3.0.0-RC2/2.3.3/audiofile_3.0.0-RC2-2.3.3.jar";
    String methodSignature =
        "<de.sciss.audiofile.AudioFile$AsyncBasic: scala.concurrent.Future close()>";
    JavaView javaView = supplyJavaView(jarDownloadUrl);
    assertMethodConversion(javaView, methodSignature);
    assertJar(javaView);
  }
}
