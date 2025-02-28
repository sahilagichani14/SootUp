package sootup.codepropertygraph.propertygraph.nodes;

/*-
* #%L
* Soot - a J*va Optimization Framework
* %%
Copyright (C) 2024 Michael Youkeim, Stefan Schott and others
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

import sootup.core.jimple.basic.Immediate;
import sootup.core.jimple.basic.Value;

public class ImmediateGraphNode extends PropertyGraphNode implements ValueGraphNode {
  private final Immediate immediate;

  public ImmediateGraphNode(Immediate immediate) {
    this.immediate = immediate;
  }

  public Immediate getImmediate() {
    return immediate;
  }

  @Override
  public Value getValue() {
    return immediate;
  }

  @Override
  public boolean equals(Object o) {
    return this == o;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }

  @Override
  public String toString() {
    return immediate.toString();
  }
}
