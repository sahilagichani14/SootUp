package sootup.java.bytecode.frontend.inputlocation;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import sootup.core.model.SourceType;
import sootup.core.transform.BodyInterceptor;
import sootup.core.types.ClassType;
import sootup.core.views.View;
import sootup.java.bytecode.frontend.conversion.AsmJavaClassProvider;
import sootup.java.core.JavaSootClassSource;
import sootup.java.core.types.JavaClassType;

class DirectoryBasedAnalysisInputLocation extends PathBasedAnalysisInputLocation {

  protected DirectoryBasedAnalysisInputLocation(
      @Nonnull Path path,
      @Nonnull SourceType srcType,
      @Nonnull List<BodyInterceptor> bodyInterceptors) {
    this(path, srcType, bodyInterceptors, Collections.emptyList());
  }

  protected DirectoryBasedAnalysisInputLocation(
      @Nonnull Path path,
      @Nonnull SourceType srcType,
      @Nonnull List<BodyInterceptor> bodyInterceptors,
      @Nonnull Collection<Path> ignoredPaths) {
    super(path, srcType, bodyInterceptors, ignoredPaths);
  }

  @Override
  @Nonnull
  public Stream<JavaSootClassSource> getClassSources(@Nonnull View view) {
    // FIXME: 1) store the classprovider reference as a field; 2) and above too; and 3) move view
    // which is only used in SootNode to be just there?
    return walkDirectory(path, view.getIdentifierFactory(), new AsmJavaClassProvider(view));
  }

  @Override
  @Nonnull
  public Optional<JavaSootClassSource> getClassSource(@Nonnull ClassType type, @Nonnull View view) {
    return getClassSourceInternal((JavaClassType) type, path, new AsmJavaClassProvider(view));
  }
}
