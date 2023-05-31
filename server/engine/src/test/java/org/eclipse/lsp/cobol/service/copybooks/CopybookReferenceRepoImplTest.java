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
package org.eclipse.lsp.cobol.service.copybooks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Set;

import org.eclipse.lsp.cobol.common.copybook.CopybookModel;
import org.eclipse.lsp.cobol.common.copybook.CopybookName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

/** Test @{@link CopybookReferenceRepo} */
class CopybookReferenceRepoImplTest {

  public static final String DOCUMENT_URI = "file:///c:/workspace/document.cbl";
  private static final String CPY_URI = "file:///c:/workspace/.c4z/.copybooks/PARENT.CPY";
  public static final String COPYBOOK_CONTENT = "sample copybook text";

  @Test
  void clearReferences() {
    CopybookReferenceRepo repo = storeReferences();
    assertEquals(1, setUpRepo().size());
    repo.clearReferences();
    assertEquals(0, repo.getCopybookUsageReference(CPY_URI).size());
  }

  @Test
  void getCopybookUsageReference() {
    Set<CopybookModel> copybookUsageReference = setUpRepo();
    assertEquals(1, copybookUsageReference.size());
    CopybookModel referencedCopybookModels = copybookUsageReference.toArray(new CopybookModel[0])[0];
    assertEquals(COPYBOOK_CONTENT, referencedCopybookModels.getContent());
    assertEquals(DOCUMENT_URI, referencedCopybookModels.getUri());
  }

  @Test
  @EnabledOnOs({OS.WINDOWS})
  void getCopybookUsageReference_whenUriIsCaseInsensitive() {
    CopybookReferenceRepo repo = storeReferences();
    Set<CopybookModel> copybookUsageReference = repo.getCopybookUsageReference("file:///C:/workspace/.c4z/.copybooks/PARENT.CPY");
    CopybookModel referencedCopybookModels = copybookUsageReference.toArray(new CopybookModel[0])[0];
    assertEquals(1, copybookUsageReference.size());
    assertEquals(DOCUMENT_URI, referencedCopybookModels.getUri());
    assertEquals(COPYBOOK_CONTENT, referencedCopybookModels.getContent());
  }

  private Set<CopybookModel> setUpRepo() {
    CopybookReferenceRepo repo = storeReferences();
    return repo.getCopybookUsageReference(CPY_URI);
  }

  private CopybookReferenceRepo storeReferences() {
    CopybookReferenceRepo repo = new CopybookReferenceRepoImpl();
    CopybookName copybookName = new CopybookName("COPYBOOK");
    CopybookModel copybookModel = new CopybookModel(copybookName.toCopybookId(DOCUMENT_URI), copybookName, CPY_URI, COPYBOOK_CONTENT);
    repo.storeCopybookUsageReference(copybookName, DOCUMENT_URI, copybookModel);
    return repo;
  }
}
