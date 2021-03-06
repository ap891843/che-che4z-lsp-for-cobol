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

/**
 * This value class represents an Index item. It is defined using INDEXED BY statement in a {@link
 * TableDataNameNode} declaration.
 */
@Getter
@ToString(callSuper = true)
public class IndexItemNode extends VariableNode {
  protected IndexItemNode(Locality location, String name, boolean global) {
    super(location, name, VariableType.INDEX_ITEM, global);
  }
}
