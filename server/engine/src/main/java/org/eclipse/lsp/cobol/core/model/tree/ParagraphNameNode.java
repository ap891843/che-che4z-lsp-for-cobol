/*
 * Copyright (c) 2022 Broadcom.
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
package org.eclipse.lsp.cobol.core.model.tree;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.eclipse.lsp.cobol.common.model.Context;
import org.eclipse.lsp.cobol.common.model.Locality;
import org.eclipse.lsp.cobol.common.model.tree.Node;
import org.eclipse.lsp.cobol.common.model.NodeType;
import org.eclipse.lsp.cobol.core.engine.symbols.CodeBlockReference;
import org.eclipse.lsp.cobol.core.engine.symbols.SymbolService;
import org.eclipse.lsp4j.Location;

import java.util.List;
import java.util.function.Function;

/** The class represents paragraphs or section name node in COBOL grammar. */
@Getter
public class ParagraphNameNode extends Node implements Context {
  private final String name;
  private final SymbolService symbolService;

  public ParagraphNameNode(Locality location, String paragraphName, SymbolService symbolService) {
    super(location, NodeType.PARAGRAPH_NAME_NODE);
    this.name = paragraphName.toUpperCase();
    this.symbolService = symbolService;
  }

  @Override
  public List<Location> getDefinitions() {
    return getLocations(CodeBlockReference::getDefinitions);
  }

  @Override
  public List<Location> getUsages() {
    return getLocations(CodeBlockReference::getUsage);
  }

  private List<Location> getLocations(
      Function<CodeBlockReference, List<Location>> retrieveLocations) {
    return getProgram()
        .map(symbolService::getParagraphMap)
        .map(it -> it.get(getName()))
        .map(retrieveLocations)
        .orElse(ImmutableList.of());
  }
}
