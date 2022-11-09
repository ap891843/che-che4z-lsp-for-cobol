/*
 * Copyright (c) 2020 Broadcom.
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

package org.eclipse.lsp.cobol.service.delegates.completions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import org.eclipse.lsp.cobol.common.model.CopyNode;
import org.eclipse.lsp.cobol.common.model.Locality;
import org.eclipse.lsp.cobol.common.model.ProgramNode;
import org.eclipse.lsp.cobol.core.engine.symbols.SymbolsRepository;
import org.eclipse.lsp.cobol.core.engine.symbols.SymbolService;
import org.eclipse.lsp.cobol.core.model.tree.*;
import org.eclipse.lsp.cobol.core.model.tree.variables.MnemonicNameNode;
import org.eclipse.lsp.cobol.core.model.tree.variables.VariableNode;
import org.eclipse.lsp.cobol.service.CobolDocumentModel;
import org.eclipse.lsp.cobol.service.delegates.validations.AnalysisResult;

/** This class stores a model to assert the completion providers */
class MockCompletionModel {
  static final AnalysisResult RESULT =
      AnalysisResult.builder()
          .rootNode(new RootNode(Locality.builder().build(), ImmutableMultimap.of()))
          .build();
  static final CobolDocumentModel MODEL = new CobolDocumentModel("some text", RESULT);
  static final SymbolService SYMBOL_SERVICE = new SymbolService();
  static final SymbolsRepository SYMBOL_STORAGE = new SymbolsRepository();

  static {
    ProgramNode programNode = new ProgramNode(Locality.builder().build());
    RESULT.getRootNode().addChild(programNode);
    ImmutableList.of("constD1", "ConstD2")
        .forEach(
            name -> {
              VariableNode variable = new MnemonicNameNode(Locality.builder().build(), "sys", name);
              SYMBOL_SERVICE.addVariableDefinition(programNode, variable);
            });

    ImmutableList.of("parD1", "ParD2", "Not-parD")
        .forEach(
            name -> {
              ParagraphNameNode nameNode =
                  new ParagraphNameNode(Locality.builder().build(), name, SYMBOL_STORAGE);
              SYMBOL_SERVICE.registerParagraphNameNode(programNode, nameNode);
            });
    ImmutableList.of("secD1", "SecD2", "Not-secD")
        .forEach(
            name -> {
              SectionNameNode nameNode =
                  new SectionNameNode(
                      Locality.builder().build(), name);
              SYMBOL_SERVICE.registerSectionNameNode(programNode, nameNode);
            });

    RootNode rootNode = new RootNode(Locality.builder().build(), ImmutableMultimap.of());
    RESULT.getRootNode().addChild(rootNode);
    ImmutableList.of("cpyU1", "CpyU2", "Not-cpyU")
        .forEach(
            name -> {
              CopyNode nameNode = new CopyNode(Locality.builder().build(), name);
              rootNode.addChild(nameNode);
            });
    SYMBOL_STORAGE.updateSymbols(SYMBOL_SERVICE.getProgramSymbols());
  }
}
