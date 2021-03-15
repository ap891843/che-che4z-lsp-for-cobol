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

import com.google.inject.Singleton;
import lombok.NonNull;
import org.eclipse.lsp.cobol.core.model.variables.Variable;
import org.eclipse.lsp.cobol.service.CobolDocumentModel;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.eclipse.lsp.cobol.service.delegates.completions.CompletionOrder.VARIABLES;

/**
 * This completion provider returns all the defined variables as completion suggestions and their
 * definition as documentation
 */
@Singleton
public class VariableCompletion implements Completion {

  @Override
  public @NonNull Collection<CompletionItem> getCompletionItems(
      @NonNull String token, @Nullable CobolDocumentModel document) {
    if (document == null) return emptyList();
    return document.getAnalysisResult().getVariables().stream()
        .filter(matchNames(token))
        .map(this::toVariableCompletionItem)
        .collect(toList());
  }

  private Predicate<Variable> matchNames(@NonNull String token) {
    return it -> it.getName().regionMatches(true, 0, token, 0, token.length());
  }

  private CompletionItem toVariableCompletionItem(Variable it) {
    String name = it.getName();
    CompletionItem item = new CompletionItem(name);
    item.setLabel(name);
    item.setInsertText(name);
    item.setSortText(VARIABLES.prefix + name);
    item.setDocumentation(DocumentationUtils.collectDescription(it));
    item.setKind(CompletionItemKind.Variable);
    return item;
  }
}
