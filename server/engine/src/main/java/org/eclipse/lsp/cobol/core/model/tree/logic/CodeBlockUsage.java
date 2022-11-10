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
package org.eclipse.lsp.cobol.core.model.tree.logic;

import org.eclipse.lsp.cobol.common.error.SyntaxError;
import org.eclipse.lsp.cobol.common.model.tree.ProgramNode;
import org.eclipse.lsp.cobol.common.processor.ProcessingContext;
import org.eclipse.lsp.cobol.common.processor.Processor;
import org.eclipse.lsp.cobol.core.engine.symbols.SymbolService;
import org.eclipse.lsp.cobol.core.model.tree.CodeBlockUsageNode;

import java.util.Optional;

/** CodeBlockUsageNode processor */
public class CodeBlockUsage implements Processor<CodeBlockUsageNode> {
  private final SymbolService symbolService;

  public CodeBlockUsage(SymbolService symbolService) {
    this.symbolService = symbolService;
  }

  @Override
  public void accept(CodeBlockUsageNode node, ProcessingContext ctx) {
    Optional<ProgramNode> programOpt = node.getProgram();
    if (!programOpt.isPresent()) {
      // TODO: error?
      return;
    }
    ProgramNode program = programOpt.get();
    Optional<SyntaxError> syntaxError = symbolService.registerCodeBlockUsage(program, node);
    syntaxError.ifPresent(error -> ctx.getErrors().add(error));
  }
}
