/*
 * Copyright (c) 2021 Broadcom.
 * The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Broadcom, Inc. - initial API and implementation
 *
 */
package org.eclipse.lsp.cobol.core.model.tree.variables;

import lombok.Getter;
import lombok.ToString;
import org.eclipse.lsp.cobol.core.model.Locality;
import org.eclipse.lsp.cobol.core.model.variables.UsageFormat;

/**
 * This value class represents an element item COBOL variable. It has a PIC clause representing its
 * type, and an optional VALUE clause that stores an explicitly defined value; both as Strings.
 * Element items cannot have nested variables, but may be a top element with level 01. Allowed
 * levels for this element are 01-49.
 */
@Getter
@ToString(callSuper = true)
public class ElementaryItemNode extends VariableWithLevelNode {
  private String picClause;
  private String value;
  private UsageFormat usageFormat;
  private boolean redefines;

  public ElementaryItemNode(Locality location, int level, String name, boolean global, String picClause, String value,
                            UsageFormat usageFormat, boolean redefines) {
    super(location, level, name, VariableType.ELEMENTARY_ITEM, global);
    this.picClause = picClause;
    this.value = value;
    this.usageFormat = usageFormat;
    this.redefines = redefines;
  }
}
