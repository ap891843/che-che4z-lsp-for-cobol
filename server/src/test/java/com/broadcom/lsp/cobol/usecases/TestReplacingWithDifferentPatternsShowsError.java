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

package com.broadcom.lsp.cobol.usecases;

import com.broadcom.lsp.cobol.positive.CobolText;
import com.broadcom.lsp.cobol.service.delegates.validations.SourceInfoLevels;
import com.broadcom.lsp.cobol.usecases.engine.UseCaseEngine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.junit.jupiter.api.Test;

/**
 * This test checks grammar does not consume more tokens than necessary in REPLACING statement.
 * Here: REPLACING statement for REPL is incorrect due to different pattern types, so this statement
 * must not be parsed
 */
class TestReplacingWithDifferentPatternsShowsError {

  private static final String TEXT =
      "       IDENTIFICATION DIVISION.  \n"
          + "       PROGRAM-ID. TESTREPL.\n"
          + "       DATA DIVISION.\n"
          + "       WORKING-STORAGE SECTION.\n"
          + "       01  {$*PARENT} PIC 9.\n"
          + "       {COPY|1} REPL REPLACING ==TAG== BY DEF. \n"
          + "       PROCEDURE DIVISION.\n"
          + "       MAINLINE.\n"
          + "           PERFORM NAME3. \n"
          + "       COPY {~NEW2} REPLACING ==NAME2== BY ==NAME3== .\n"
          + "           GOBACK.";

  private static final String NEW2 =
      "         NAME2.\n" + "           DISPLAY \"ABC\".\n" + "           MOVE 0 TO PARENT. ";
  private static final String NEW2_NAME = "NEW2";

  private static final String MESSAGE =
      "Syntax error on 'COPY' expected {<EOF>, ID, IDENTIFICATION, LINKAGE, LOCAL-STORAGE, WORKING-STORAGE, PROCEDURE, "
          + "SCHEMA, END, FILE, MAP, SQL, 'EXEC SQL', EXEC, '01-49', '66', '77', '88'}";

  @Test
  void test() {
    UseCaseEngine.runTest(
        TEXT,
        ImmutableList.of(new CobolText(NEW2_NAME, NEW2)),
        ImmutableMap.of(
            "1",
            new Diagnostic(
                null, MESSAGE, DiagnosticSeverity.Error, SourceInfoLevels.ERROR.getText())));
  }
}
