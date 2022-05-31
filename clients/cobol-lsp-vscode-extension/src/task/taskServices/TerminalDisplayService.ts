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
 *   Broadcom, Inc. - initial API and implementation
 */

import { ZoweExplorerApi } from "@zowe/zowe-explorer-api";
import * as vscode from "vscode";
import { CRLF } from "../terminal/CustomBuildTaskTerminal";

export class TerminalDisplayService {

    constructor(private writeEmitter: vscode.EventEmitter<string>) {
    }

    private readonly STEP_SUMMARY_REGEX = /(IEF472I|IEF142I) \S{1,8} ((\S+ -)|(\S+ \S+)) (((COMPLETION CODE)|-) )?(-|STEP WAS EXECUTED) (.*|-) (.*$)/;

    private readonly CCODE_REGEX = /SYSTEM=(\S+) USER=(\S+)/;

    public displayJobSummary(jobSummary: { stepname: string; procstep: string; msg: string; retCode: string; reason: string; }[]) {
        this.writeEmitter.fire(`${CRLF}\x1b[35mStep         StepName     ProcStep     Status       CCode        AbendRsn${CRLF}\x1b[34m${jobSummary
            .map((file, index) => {
                return `${this.getPaddedString(`${index + 1}`)}${this.getPaddedString(file.stepname)}${this.getPaddedString(file.procstep)}${this.getPaddedString(file.msg)}${this.getPaddedString(file.retCode)}${this.getPaddedString(file.reason)}`;
            })
            .join(`${CRLF}`)} ${CRLF}`);
    }

    public async displaySpoolFiles(spoolFiles, jesApi: ZoweExplorerApi.IJes, jobContext: Map<string, { jobname: string, jobid: string, spoolid: number }>) {
        this.writeEmitter.fire(`${CRLF}\x1b[35mSpool-id     DDname       Stepname     Procstep${CRLF}\x1b[34m${spoolFiles
            .map(file => {
                jobContext.set(file.ddname, { jobid: file.jobid, jobname: file.jobname, spoolid: file.id });
                return `${this.getPaddedString(file.id.toString())}${this.getPaddedString(file.ddname)}${this.getPaddedString(file.stepname)}${this.getPaddedString(file.procstep ? file.procstep : "")}`;
            })
            .join(`${CRLF}`)} ${CRLF}`);
    }

    public async getMessageFromJesMsg(spoolFiles: any, jesApi: ZoweExplorerApi.IJes) {
        const sysPrintContent = await jesApi.getSpoolContentById(spoolFiles.jobname, spoolFiles.jobid, spoolFiles.id);
        return sysPrintContent.split("\n")
            .filter(str => str.match(this.STEP_SUMMARY_REGEX))
            .map(str => {
                const matches = str.match(this.STEP_SUMMARY_REGEX);
                let stepname = matches[2].split(" ")[0];
                let procstep = "";
                let msg: string;
                let retCode: string;
                let reason = "";
                if (!matches[2].includes("-")) {
                    stepname = matches[2].split(" ")[1];
                    procstep = matches[2].split(" ")[0];
                }

                if (matches[1] === "IEF472I") {
                    msg = "ABENDED";
                } else {
                    msg = "EXECUTED";
                }

                if (matches[9].includes("COND CODE")) {
                    retCode = matches[10];
                } else {
                    const retmatches = matches[9].match(this.CCODE_REGEX);
                    retCode = retmatches[1] !== "000" ? `S${retmatches[1]}` : `U${retmatches[2]}`;
                }

                if (retCode !== "0000") {
                    reason = matches[10];
                }

                return { stepname, procstep, msg, retCode, reason };
            });

    }


    private getPaddedString(str: string, maxLength: number = 13) {
        if (str.length >= maxLength) { return str; }
        return str + " ".repeat(maxLength - str.length);
    }
}
