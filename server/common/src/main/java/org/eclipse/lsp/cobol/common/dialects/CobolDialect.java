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
package org.eclipse.lsp.cobol.common.dialects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.eclipse.lsp.cobol.common.ResultWithErrors;
import org.eclipse.lsp.cobol.common.error.SyntaxError;
import org.eclipse.lsp.cobol.common.processor.ProcessorDescription;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A COBOL dialect */
public interface CobolDialect {
  String FILLER = "\u200B";
  /**
   * Gets the name of the dialect
   * @return the name of the dialect
   */
  String getName();

  /**
   * Processing the text
   *
   * @param context is a DialectProcessingContext class with all needed data for dialect processing
   * @return the dialect processing result
   */
  default ResultWithErrors<DialectOutcome> processText(DialectProcessingContext context) {
    return new ResultWithErrors<>(new DialectOutcome(context),
            ImmutableList.of());
  }

  /**
   * Get document extension data from dialect. This data should be added to an argument.
   *
   * @param context data related to dialect processing
   * @return a list of syntax errors
   */
  default List<SyntaxError> extend(DialectProcessingContext context) {
    return ImmutableList.of();
  }

  /**
   * Return a list of processor descriptors.
   * @return a list of processor descriptors for the dialect
   */
  default List<ProcessorDescription> getProcessors() {
    return Collections.emptyList();
  }

  /**
   * Define a order for dialect execution
   * @return set of dialect processors, that should follow this one
   */
  default Set<String> runBefore() {
    return ImmutableSet.of();
  }

  /**
   * Returns dialect keywords map where key is a keyword and a value is a description
   * @return key/value map with keywords and descriptions
   */
  default Map<String, String> getKeywords() {
    return ImmutableMap.of();
  }
}
