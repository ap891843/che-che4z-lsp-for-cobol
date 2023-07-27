/*
 * Copyright (c) 2023 Broadcom.
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
package org.eclipse.lsp.cobol.usecases;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp.cobol.common.error.ErrorSource;
import org.eclipse.lsp.cobol.test.CobolText;
import org.eclipse.lsp.cobol.test.engine.UseCaseEngine;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

import static org.eclipse.lsp4j.DiagnosticSeverity.Error;

/**
 * Tests that the mapping for the embedded code is maintained.
 */
public class TestMappingMaintainedForEmbeddedCode {

  public static final String TEXT =
      "   \n"
          + "       IDENTIFICATION DIVISION.                                                 \n"
          + "       PROGRAM-ID.      TEST-123.                                            \n"
          + "       DATE-COMPILED.                                                           \n"
          + "                                                                                \n"
          + "       ENVIRONMENT DIVISION.                                                    \n"
          + "                                                                                \n"
          + "       DATA DIVISION.                                                           \n"
          + "       WORKING-STORAGE SECTION.                                                 \n"
          + "      *************************                                                 \n"
          + "                                                                                \n"
          + "       01  {$*THIS-PGM}                    PIC X(8) VALUE 'TEST'.             \n"
          + "           COPY {~COPYBOOK}.                                                       \n"
          + "       01  {$*TADA}.                                                                \n"
          + "           03 {$*PGM}                 PIC X(8) VALUE 'TEST'.            \n"
          + "                                                                                \n"
          + "      * Work areas                                                              \n"
          + "       01  {$*TT}.                                                               \n"
          + "           03 {$*TT-PARM}.                                                          \n"
          + "              05 {$*TEST-APPLID}           PIC X(8).                             \n"
          + "              05 FILLER                   PIC X(72).                    \n"
          + "                                                                                \n"
          + "       LINKAGE SECTION.                                                         \n"
          + "      /                                                                         \n"
          + "       PROCEDURE DIVISION.                                       \n"
          + "                                                                                \n"
          + "       {@*MAIN-CONTROL} SECTION.                                                    \n"
          + "                                                                                \n"
          + "           PERFORM WITH TEST AFTER UNTIL {$TT-PARM} = ALL X'DD'       \n"
          + "                                                                                \n"
          + "      *                                                                         \n"
          + "      *  some comments                                              \n"
          + "      *                                                                         \n"
          + "                 EXEC CICS LINK                                                 \n"
          + "                    PROGRAM({$PGM})                                        \n"
          + "                    {DUMMY-CMD|1}()                            \n"
          + "                    SYNCONRETURN                                                \n"
          + "                 END-EXEC                                                       \n"
          + "                                                                      \n"
          + "            END-PERFORM                                                         \n"
          + "                                                                          \n"
          + "           GOBACK                                                               \n"
          + "           .                                                                    \n"
          + "       END PROGRAM TEST-123.";

  private static final String COPYBOOK =
      "       01  {$*GRP-ITEM}.\n"
          + "           05 {$*ELEMENT1}     PIC X(50) VALUE\n"
          + "             'This is an elementary item 1'.\n"
          + "           05 {$*ELEMENT2}      PIC X(35) VALUE\n"
          + "             'This is an elementary item 2'.\n";

  @Test
  void assertCopybookWithQuotesProcessing() {
    UseCaseEngine.runTest(
        TEXT, ImmutableList.of(new CobolText("COPYBOOK", COPYBOOK)), ImmutableMap.of("1", new Diagnostic(
                    new Range(),
                    "Syntax error on 'DUMMY-CMD' expected {CHANNEL, COMMAREA, INPUTMSG, RESP, RESP2, SYNCONRETURN, SYSID, TRANSID}",
                    Error,
                    ErrorSource.PARSING.getText())));
  }
}