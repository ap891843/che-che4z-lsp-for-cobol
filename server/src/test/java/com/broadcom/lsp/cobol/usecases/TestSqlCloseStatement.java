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

import com.broadcom.lsp.cobol.usecases.engine.UseCaseEngine;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/** This test checks if sql CLOSE statement works correctly. */
class TestSqlCloseStatement {
  private static final String TEXT =
      "       IDENTIFICATION DIVISION.\n"
          + "       PROGRAM-ID. HELLO-SQL.\n"
          + "       DATA DIVISION.\n"
          + "       WORKING-STORAGE SECTION.\n"
          + "       EXEC SQL\n"
          + "         DECLARE C1 CURSOR FOR\n"
          + "         SELECT DEPTNO, DEPTNAME, MGRNO\n"
          + "         FROM DSN8C10.DEPT\n"
          + "         WHERE ADMRDEPT = 'A00';\n"
          + "         OPEN C1;\n"
          + "         FETCH C1 INTO :DNUM, :DNAME, :MNUM;\n"
          + "         \n"
          + "         CLOSE C1;\n"
          + "       END-EXEC.";

  @Test
  void test() {
    UseCaseEngine.runTest(TEXT, List.of(), Map.of());
  }
}
