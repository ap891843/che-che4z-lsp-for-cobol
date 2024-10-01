/*
 * Copyright (c) 2024 Broadcom.
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
import org.eclipse.lsp.cobol.test.engine.UseCaseEngine;
import org.junit.jupiter.api.Test;

/**
 * Tests CICS MOVE CONTAINER (EXCI) statement. Ref:
 * https://www.ibm.com/docs/en/cics-ts/6.1?topic=interface-exec-cics-link-command-exci
 */
public class TestCicsExciMoveContainer {
  public static final String TEXT =
      "       IDENTIFICATION DIVISION.\n"
          + "       PROGRAM-ID. GetNextContainerEXCI.\n"
          + "       DATA DIVISION.\n"
          + "       WORKING-STORAGE SECTION.\n"
          + "       01  {$*CONTAINER-VALUE}       PIC X(50).\n"
          + "\n"
          + "       PROCEDURE DIVISION.\n"
          + "           EXEC CICS \n"
          + "           MOVE CONTAINER('containeR1')\n"
          + "           AS({$CONTAINER-VALUE})\n"
          + "           CHANNEL({$CONTAINER-VALUE})\n"
          + "           TOCHANNEL({$CONTAINER-VALUE})\n"
          + "           RETCODE({$RETURN-CODE})\n"
          + "           END-EXEC.";

  @Test
  void test() {
    UseCaseEngine.runTest(TEXT, ImmutableList.of(), ImmutableMap.of());
  }
}
