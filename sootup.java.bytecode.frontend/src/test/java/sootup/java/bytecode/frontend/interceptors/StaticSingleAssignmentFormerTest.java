package sootup.java.bytecode.frontend.interceptors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import categories.TestCategories;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.graph.MutableBlockStmtGraph;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.common.constant.IntConstant;
import sootup.core.jimple.common.ref.IdentityRef;
import sootup.core.jimple.common.stmt.*;
import sootup.core.model.Body;
import sootup.core.model.SourceType;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.ClassType;
import sootup.core.types.VoidType;
import sootup.core.util.ImmutableUtils;
import sootup.interceptors.StaticSingleAssignmentFormer;
import sootup.java.bytecode.frontend.inputlocation.PathBasedAnalysisInputLocation;
import sootup.java.core.JavaIdentifierFactory;
import sootup.java.core.language.JavaJimple;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

/** @author Zun Wang */
@Tag(TestCategories.JAVA_8_CATEGORY)
public class StaticSingleAssignmentFormerTest {

  // Preparation
  JavaIdentifierFactory factory = JavaIdentifierFactory.getInstance();
  StmtPositionInfo noStmtPositionInfo = StmtPositionInfo.getNoStmtPositionInfo();
  JavaJimple javaJimple = JavaJimple.getInstance();
  final String location =
      Paths.get(System.getProperty("user.dir")).getParent()
          + File.separator
          + "shared-test-resources/bugfixes/";

  JavaClassType intType = factory.getClassType("int");
  JavaClassType classType = factory.getClassType("Test");
  JavaClassType refType = factory.getClassType("ref");
  MethodSignature methodSignature =
      new MethodSignature(classType, "test", Collections.emptyList(), VoidType.getInstance());
  IdentityRef identityRef = JavaJimple.newThisRef(classType);
  ClassType exceptionType = factory.getClassType("Exception");
  IdentityRef caughtExceptionRef = javaJimple.newCaughtExceptionRef();

  // build locals
  Local l0 = JavaJimple.newLocal("l0", classType);
  Local l1 = JavaJimple.newLocal("l1", intType);
  Local l2 = JavaJimple.newLocal("l2", intType);
  Local l3 = JavaJimple.newLocal("l3", intType);
  Local stack4 = JavaJimple.newLocal("stack4", refType);

  JIdentityStmt startingStmt = JavaJimple.newIdentityStmt(l0, identityRef, noStmtPositionInfo);
  JAssignStmt assign1tol1 =
      JavaJimple.newAssignStmt(l1, IntConstant.getInstance(1), noStmtPositionInfo);
  JAssignStmt assign1tol2 =
      JavaJimple.newAssignStmt(l2, IntConstant.getInstance(2), noStmtPositionInfo);
  JAssignStmt assign0tol3 =
      JavaJimple.newAssignStmt(l3, IntConstant.getInstance(0), noStmtPositionInfo);
  BranchingStmt ifStmt =
      JavaJimple.newIfStmt(
          JavaJimple.newLtExpr(l3, IntConstant.getInstance(100)), noStmtPositionInfo);
  BranchingStmt ifStmt2 =
      JavaJimple.newIfStmt(
          JavaJimple.newLtExpr(l2, IntConstant.getInstance(20)), noStmtPositionInfo);
  JReturnStmt returnStmt = JavaJimple.newReturnStmt(l2, noStmtPositionInfo);
  JAssignStmt assignl1tol2 = JavaJimple.newAssignStmt(l2, l1, noStmtPositionInfo);
  JAssignStmt assignl3plus1tol3 =
      JavaJimple.newAssignStmt(
          l3, JavaJimple.newAddExpr(l3, IntConstant.getInstance(1)), noStmtPositionInfo);
  JAssignStmt assignl3tol2 = JavaJimple.newAssignStmt(l2, l3, noStmtPositionInfo);
  JAssignStmt assignl3plus2tol3 =
      JavaJimple.newAssignStmt(
          l3, JavaJimple.newAddExpr(l3, IntConstant.getInstance(2)), noStmtPositionInfo);
  JGotoStmt gotoStmt1 = JavaJimple.newGotoStmt(noStmtPositionInfo);
  JGotoStmt gotoStmt2 = JavaJimple.newGotoStmt(noStmtPositionInfo);
  JGotoStmt gotoStmt3 = JavaJimple.newGotoStmt(noStmtPositionInfo);

  FallsThroughStmt handlerStmt =
      JavaJimple.newIdentityStmt(stack4, caughtExceptionRef, noStmtPositionInfo);
  JAssignStmt l2eq2 = JavaJimple.newAssignStmt(l2, IntConstant.getInstance(2), noStmtPositionInfo);
  JGotoStmt gotoStmt = JavaJimple.newGotoStmt(noStmtPositionInfo);

  @Test
  public void testSSA() {
    StaticSingleAssignmentFormer ssa = new StaticSingleAssignmentFormer();
    Body.BodyBuilder builder = createBody();
    ssa.interceptBody(builder, new JavaView(Collections.emptyList()));
    String expectedBodyString =
        "{\n"
            + "    Test l0, l0#0;\n"
            + "    int l1, l1#1, l2, l2#10, l2#2, l2#4, l2#6, l2#8, l3, l3#11, l3#3, l3#5, l3#7, l3#9;\n"
            + "\n"
            + "\n"
            + "    l0#0 := @this: Test;\n"
            + "    l1#1 = 1;\n"
            + "    l2#2 = 2;\n"
            + "    l3#3 = 0;\n"
            + "\n"
            + "  label1:\n"
            + "    l2#4 = phi(l2#2, l2#10);\n"
            + "    l3#5 = phi(l3#3, l3#11);\n"
            + "\n"
            + "    if l3#5 < 100 goto label2;\n"
            + "\n"
            + "    return l2#4;\n"
            + "\n"
            + "  label2:\n"
            + "    if l2#4 < 20 goto label3;\n"
            + "    l2#8 = l3#5;\n"
            + "    l3#9 = l3#5 + 2;\n"
            + "\n"
            + "    goto label4;\n"
            + "\n"
            + "  label3:\n"
            + "    l2#6 = l1#1;\n"
            + "    l3#7 = l3#5 + 1;\n"
            + "\n"
            + "    goto label4;\n"
            + "\n"
            + "  label4:\n"
            + "    l2#10 = phi(l2#6, l2#8);\n"
            + "    l3#11 = phi(l3#7, l3#9);\n"
            + "\n"
            + "    goto label1;\n"
            + "}\n";

    assertEquals(expectedBodyString, builder.build().toString());
  }

  @Test
  public void testSSA2() {
    ClassType clazzType = factory.getClassType("TrapSSA");
    MethodSignature methodSignature =
        factory.getMethodSignature(
            clazzType, "main", "void", Collections.singletonList("java.lang.String[]"));
    final Path path = Paths.get(location + "TrapSSA.class");
    PathBasedAnalysisInputLocation inputLocation =
        new PathBasedAnalysisInputLocation.ClassFileBasedAnalysisInputLocation(
            path, "", SourceType.Application);
    PathBasedAnalysisInputLocation inputLocationWithSSA =
        new PathBasedAnalysisInputLocation.ClassFileBasedAnalysisInputLocation(
            path,
            "",
            SourceType.Application,
            Collections.singletonList(new StaticSingleAssignmentFormer()));
    JavaView view = new JavaView(inputLocationWithSSA);
    System.out.println(view.getMethod(methodSignature).get().getBody());
  }

  @Test
  public void testSSA3() {
    ClassType clazzType = factory.getClassType("ForLoopSSA");
    MethodSignature methodSignature =
        factory.getMethodSignature(
            clazzType, "main", "void", Collections.singletonList("java.lang.String[]"));
    final Path path = Paths.get(location + "ForLoopSSA.class");
    PathBasedAnalysisInputLocation inputLocation =
        new PathBasedAnalysisInputLocation.ClassFileBasedAnalysisInputLocation(
            path, "", SourceType.Application);
    PathBasedAnalysisInputLocation inputLocationWithSSA =
        new PathBasedAnalysisInputLocation.ClassFileBasedAnalysisInputLocation(
            path,
            "",
            SourceType.Application,
            Collections.singletonList(new StaticSingleAssignmentFormer()));
    JavaView view = new JavaView(inputLocationWithSSA);
    System.out.println(view.getMethod(methodSignature).get().getBody());
  }

  @Disabled("ms: Which Trap body?")
  @Test
  public void testTrappedSSA() {
    StaticSingleAssignmentFormer ssa = new StaticSingleAssignmentFormer();
    Body.BodyBuilder builder = createTrapBody();
    ssa.interceptBody(builder, new JavaView(Collections.emptyList()));
    String expectedBodyString =
        "{\n"
            + "    Test l0, l0#0;\n"
            + "    int l1, l1#1, l2, l2#11, l2#13, l2#2, l2#4, l2#6, l2#8, l2#9, l3, l3#10, l3#12, l3#14, l3#3, l3#5;\n"
            + "    ref stack4, stack4#7;\n"
            + "\n"
            + "\n"
            + "    l0#0 := @this: Test;\n"
            + "    l1#1 = 1;\n"
            + "    l2#2 = 2;\n"
            + "    l3#3 = 0;\n"
            + "\n"
            + "  label1:\n"
            + "    l2#4 = phi(l2#2, l2#13);\n"
            + "    l3#5 = phi(l3#3, l3#14);\n"
            + "\n"
            + "    if l3#5 < 100 goto label2;\n"
            + "\n"
            + "    return l2#4;\n"
            + "\n"
            + "  label2:\n"
            + "    if l2#4 < 20 goto label3;\n"
            + "    l2#11 = l3#5;\n"
            + "    l3#12 = l3#5 + 2;\n"
            + "\n"
            + "    goto label6;\n"
            + "\n"
            + "  label3:\n"
            + "    l2#6 = l1#1;\n"
            + "\n"
            + "  label4:\n"
            + "    l2#9 = phi(l2#6, l2#8);\n"
            + "    l3#10 = l3#5 + 1;\n"
            + "\n"
            + "    goto label6;\n"
            + "\n"
            + "  label5:\n"
            + "    stack4#7 := @caughtexception;\n"
            + "    l2#8 = 2;\n"
            + "\n"
            + "    goto label4;\n"
            + "\n"
            + "  label6:\n"
            + "    l2#13 = phi(l2#9, l2#11);\n"
            + "    l3#14 = phi(l3#10, l3#12);\n"
            + "\n"
            + "    goto label1;\n"
            + "\n"
            + " catch Exception from label3 to label4 with label5;\n"
            + "}\n";

    assertEquals(expectedBodyString, builder.build().toString());
  }

  /**
   *
   *
   * <pre>
   *    l0 := @this Test
   *    l1 = 1
   *    l2 = 2
   *    l3 = 0
   * label1:
   *    if l3 < 100 goto label2
   *    return l2
   * label2:
   *    if l2 < 20 goto label3
   *    l2 = l3
   *    l3 = l3 + 2
   *    goto label4;
   * label3:
   *    l2 = l1
   *    l3 = l3 + 1
   *    goto label4
   * label3:
   *    goto label1
   * </pre>
   */
  private Body.BodyBuilder createBody() {
    MutableBlockStmtGraph graph = new MutableBlockStmtGraph();

    // Block 0
    graph.setStartingStmt(startingStmt);
    graph.putEdge(startingStmt, assign1tol1);
    graph.putEdge(assign1tol1, assign1tol2);
    graph.putEdge(assign1tol2, assign0tol3);

    // block 1
    graph.putEdge(assign0tol3, ifStmt);

    // block 2
    graph.putEdge(ifStmt, JIfStmt.TRUE_BRANCH_IDX, ifStmt2);

    // block 3
    graph.putEdge(ifStmt, JIfStmt.FALSE_BRANCH_IDX, returnStmt);

    // block 4
    graph.putEdge(ifStmt2, JIfStmt.TRUE_BRANCH_IDX, assignl1tol2);
    graph.putEdge(assignl1tol2, assignl3plus1tol3);
    graph.putEdge(assignl3plus1tol3, gotoStmt1);

    // block 5
    graph.putEdge(ifStmt2, JIfStmt.FALSE_BRANCH_IDX, assignl3tol2);
    graph.putEdge(assignl3tol2, assignl3plus2tol3);
    graph.putEdge(assignl3plus2tol3, gotoStmt2);

    // block 6
    graph.putEdge(gotoStmt1, JGotoStmt.BRANCH_IDX, gotoStmt);
    graph.putEdge(gotoStmt2, JGotoStmt.BRANCH_IDX, gotoStmt);
    graph.putEdge(gotoStmt, JGotoStmt.BRANCH_IDX, ifStmt);

    Body.BodyBuilder builder = Body.builder(graph);
    builder.setMethodSignature(methodSignature);

    // build set locals
    Set<Local> locals = ImmutableUtils.immutableSet(l0, l1, l2, l3);
    builder.setLocals(locals);

    return builder;
  }

  /**
   *
   *
   * <pre>
   *    l0 := @this Test
   *    l1 = 1
   *    l2 = 2
   *    l3 = 0
   * label1:
   *    if l3 < 100 goto label2
   *    return l2
   * label2:
   *    if l2 < 20 goto label3
   *    l2 = l3
   *    l3 = l3 + 2
   *    goto label6
   * label3:
   *    l2 = l1
   * label4:
   *    l3 = l3 + 1
   *    goto label6
   * label5:
   *    stack4 := @caughtexception
   *    l2 = 2
   *    goto label4
   * label6:
   *    goto label1
   *
   * catch Exception from label3 to label5 with label5
   * </pre>
   */
  private Body.BodyBuilder createTrapBody() {
    MutableBlockStmtGraph graph = new MutableBlockStmtGraph();

    // Block 0
    graph.setStartingStmt(startingStmt);
    graph.putEdge(startingStmt, assign1tol1);
    graph.putEdge(assign1tol1, assign1tol2);
    graph.putEdge(assign1tol2, assign0tol3);

    // block 1
    graph.putEdge(assign0tol3, ifStmt);

    // block 2
    graph.putEdge(ifStmt, JIfStmt.TRUE_BRANCH_IDX, ifStmt2);

    // block 3
    graph.putEdge(ifStmt, JIfStmt.FALSE_BRANCH_IDX, returnStmt);

    // block 4
    graph.putEdge(ifStmt2, JIfStmt.TRUE_BRANCH_IDX, assignl1tol2);

    // block 5 exceptional block
    graph.addNode(assignl1tol2, Collections.singletonMap(exceptionType, handlerStmt));
    graph.putEdge(handlerStmt, l2eq2);
    graph.putEdge(l2eq2, gotoStmt3);

    // block 6
    graph.putEdge(gotoStmt3, JGotoStmt.BRANCH_IDX, assignl3plus1tol3);
    graph.putEdge(assignl1tol2, assignl3plus1tol3);
    graph.putEdge(assignl3plus1tol3, gotoStmt1);

    // block 7
    graph.putEdge(ifStmt2, JIfStmt.FALSE_BRANCH_IDX, assignl3tol2);
    graph.putEdge(assignl3tol2, assignl3plus2tol3);
    graph.putEdge(assignl3plus2tol3, gotoStmt2);

    // block 8
    graph.putEdge(gotoStmt1, JGotoStmt.BRANCH_IDX, gotoStmt);
    graph.putEdge(gotoStmt2, JGotoStmt.BRANCH_IDX, gotoStmt);
    graph.putEdge(gotoStmt, JGotoStmt.BRANCH_IDX, ifStmt);

    Body.BodyBuilder builder = Body.builder(graph);
    builder.setMethodSignature(methodSignature);

    // build set locals
    Set<Local> locals = ImmutableUtils.immutableSet(l0, l1, l2, l3, stack4);
    builder.setLocals(locals);

    return builder;
  }
}
