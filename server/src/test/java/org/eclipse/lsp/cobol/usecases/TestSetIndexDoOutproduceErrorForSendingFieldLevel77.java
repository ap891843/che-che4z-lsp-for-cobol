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
package org.eclipse.lsp.cobol.usecases;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp.cobol.usecases.engine.UseCaseEngine;
import org.junit.jupiter.api.Test;

/** This test checks that level 77 variable can be used for setting field. */
public class TestSetIndexDoOutproduceErrorForSendingFieldLevel77 {
  private static final String TEXT =
      "000100 IDENTIFICATION DIVISION.                                         NC1404.2\n"
          + "000200 PROGRAM-ID.                                                      NC1404.2\n"
          + "000300     NC140A.                                                      NC1404.2\n"
          + "003700 DATA DIVISION.                                                   NC1404.2\n"
          + "004200 WORKING-STORAGE SECTION.                                         NC1404.2\n"
          + "004300 01  {$*GRP-TABLE1}.                                                  NC1404.2\n"
          + "004400     02  {$*ELEM1}  PIC S999    OCCURS 100 TIMES                      NC1404.2\n"
          + "004500              INDEXED BY {$*INDEX1}.                                  NC1404.2\n"
          + "006000 77  {$*CS-3}     PICTURE S999    COMPUTATIONAL   VALUE ZERO.         NC1404.2\n"
          + "021200 PROCEDURE DIVISION.                                              NC1404.2\n"
          + "048400 {#*SET-TEST-003-01}.                                                 NC1404.2\n"
          + "048700     SET {$INDEX1} UP BY {$CS-3}.                                       NC1404.2";

  @Test
  void test() {
    UseCaseEngine.runTest(TEXT, ImmutableList.of(), ImmutableMap.of());
  }
}