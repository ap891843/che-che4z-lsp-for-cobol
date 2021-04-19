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
package org.eclipse.lsp.cobol.core.model.tree;

import lombok.Value;
import org.eclipse.lsp.cobol.core.CobolParser;
import org.eclipse.lsp.cobol.core.model.Locality;

/**
 * The class represents variable usage in COBOL program.
 * This must be extended with a link to variable definition.
 */
@Value
public class VariableUsageNode extends Node {
  String dataName;
  Type variableUsageType;
  CobolParser.QualifiedDataNameFormat1Context dataNameFormat1Context;
  CobolParser.ConditionNameReferenceContext nameReferenceContext;

  public VariableUsageNode(String dataName,
                           Locality locality,
                           CobolParser.QualifiedDataNameFormat1Context dataNameFormat1Context) {
    super(locality, NodeType.VARIABLE_USAGE);
    this.dataName = dataName;
    this.dataNameFormat1Context = dataNameFormat1Context;
    variableUsageType = Type.DATA_NAME;
    nameReferenceContext = null;
  }

  public VariableUsageNode(String dataName, Locality locality) {
    super(locality, NodeType.VARIABLE_USAGE);
    this.dataName = dataName;
    variableUsageType = Type.TABLE_CALL;
    dataNameFormat1Context = null;
    nameReferenceContext = null;
  }

  public VariableUsageNode(String dataName,
                           Locality locality,
                           CobolParser.ConditionNameReferenceContext nameReferenceContext) {
    super(locality, NodeType.VARIABLE_USAGE);
    this.dataName = dataName;
    this.nameReferenceContext = nameReferenceContext;
    variableUsageType = Type.CONDITION_CALL;
    dataNameFormat1Context = null;
  }

  @Override
  public void process() {
    getNearestParentByType(NodeType.PROGRAM)
        .map(ProgramNode.class::cast)
        .map(ProgramNode::getVariableUsageDelegate)
        .ifPresent(variableUsageDelegate -> {
          switch (variableUsageType) {
            case DATA_NAME:
              variableUsageDelegate.handleDataName(dataName, getLocality(), dataNameFormat1Context);
              break;
            case TABLE_CALL:
              variableUsageDelegate.handleTableCall(dataName, getLocality());
              break;
            case CONDITION_CALL:
              variableUsageDelegate.handleConditionCall(dataName, getLocality(), nameReferenceContext);
              break;
            default:
              // No other variable usage types exist, this is unreachable, but just in case.
              break;
          }
        });
  }

  /**
   * Represents different types of variable usages.
   */
  private enum Type {
    DATA_NAME,
    TABLE_CALL,
    CONDITION_CALL,
  }
}
