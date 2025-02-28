package sootup.java.frontend.minimaltestsuite.java6;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.java.frontend.minimaltestsuite.MinimalSourceTestSuiteBase;

/** @author Kaustubh Kelkar */
@Tag("Java8")
public class ThrowExceptionMethodTest extends MinimalSourceTestSuiteBase {

  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "divideByZero", "void", Collections.emptyList());
  }

  public MethodSignature getThrowCustomExceptionSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "throwCustomException", "void", Collections.emptyList());
  }

  /**
   *
   *
   * <pre>
   *     void divideByZero() throws ArithmeticException{
   * 			int i=8/0;
   *        }
   * </pre>
   */
  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of("r0 := @this: ThrowExceptionMethod", "i0 = 8 / 0", "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public MethodSignature getMethodSignature1() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "throwCustomException", "void", Collections.emptyList());
  }

  /**
   *
   *
   * <pre>
   *     void throwCustomException() throws CustomException {
   * 		throw new CustomException("Custom Exception");
   *        }
   * </pre>
   */
  public List<String> expectedBodyStmts1() {
    return Stream.of(
            "r0 := @this: ThrowExceptionMethod",
            "r1 = new CustomException",
            "specialinvoke r1.<CustomException: void <init>(java.lang.String)>(\"Custom Exception\")",
            "throw r1")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Disabled
  @Test
  public void testArithException() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
    assertTrue(
        method.getExceptionSignatures().stream()
            .anyMatch(classType -> classType.getClassName().equals("ArithmeticException")));
  }

  @Test
  @Disabled
  public void testCustomException() {

    SootMethod method = loadMethod(getMethodSignature1());
    assertJimpleStmts(method, expectedBodyStmts1());
    assertTrue(
        method.getExceptionSignatures().stream()
            .anyMatch(classType -> classType.getClassName().equals("CustomException")));
  }
}
