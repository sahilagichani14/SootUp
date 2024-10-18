package sootup.apk.frontend.instruction;

/*-
 * #%L
 * SootUp
 * %%
 * Copyright (C) 2022 - 2024 Kadiray Karakaya, Markus Schmidt, Jonas Klauke, Stefan Schott, Palaniappan Muthuraman, Marcus Hüwe and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import sootup.apk.frontend.main.DexBody;
import sootup.core.jimple.common.stmt.Stmt;

public abstract class JumpInstruction extends DexLibAbstractInstruction {
  /**
   * @param instruction the underlying dexlib instruction
   * @param codeAddress the bytecode address of this instruction
   */
  public JumpInstruction(Instruction instruction, int codeAddress) {
    super(instruction, codeAddress);
  }

  public DexLibAbstractInstruction targetInstruction;
  protected Stmt markerUnit;

  protected DexLibAbstractInstruction getTargetInstruction(DexBody body) {
    int offset = ((OffsetInstruction) instruction).getCodeOffset();
    int targetAddress = codeAddress + offset;
    targetInstruction = body.instructionAtAddress(targetAddress);
    return targetInstruction;
  }
}
