package sootup.java.bytecode.frontend.inputlocation;

import categories.TestCategories;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.java.core.views.JavaView;

@Tag(TestCategories.JAVA_8_CATEGORY)
public class FixJars extends BaseFixJarsTest {

@Test
public void executeGDXseerjar(){
	String jarDownloadUrl = "https://repo1.maven.org/maven2/io/github/niraj-rayalla/GDXseer/1.0.3/GDXseer-1.0.3.jar";
    String methodSignature = "<io.github.niraj_rayalla.gdxseer.loader.EffekseerParticleAssetLoader$Companion: io.github.niraj_rayalla.gdxseer.loader.EffekseerParticleSubAssetLoader$Companion$Result getCachedAssetInLoaderOrAssetManager(com.badlogic.gdx.assets.AssetManager,java.lang.String)>";
    JavaView javaView = supplyJavaView(jarDownloadUrl);
    assertMethodConversion(javaView,methodSignature);
    assertJar(javaView);
}

@Test
public void executehttpkconnectamazoninstancemetadatafakejar(){
	String jarDownloadUrl = "https://repo1.maven.org/maven2/org/http4k/http4k-connect-amazon-instancemetadata-fake/5.25.0.0/http4k-connect-amazon-instancemetadata-fake-5.25.0.0.jar";
    String methodSignature = "<org.http4k.connect.amazon.instancemetadata.InstanceMetadata: org.http4k.connect.amazon.instancemetadata.model.Ec2Credentials getCredentials(org.http4k.connect.amazon.core.model.Ec2ProfileName,java.time.ZonedDateTime)>";
    JavaView javaView = supplyJavaView(jarDownloadUrl);
    assertMethodConversion(javaView,methodSignature);
    assertJar(javaView);
}

@Test
public void executeidsdkimpljar(){
	String jarDownloadUrl = "https://repo1.maven.org/maven2/cn/teleinfo/id-sdk-impl/0.0.1/id-sdk-impl-0.0.1.jar";
    String methodSignature = "<cn.teleinfo.idhub.sdk.utils.KeyConverter: java.security.PrivateKey privateKeyFromBytes(byte[],boolean,java.lang.String)>";
    JavaView javaView = supplyJavaView(jarDownloadUrl);
    assertMethodConversion(javaView,methodSignature);
    assertJar(javaView);
}

}