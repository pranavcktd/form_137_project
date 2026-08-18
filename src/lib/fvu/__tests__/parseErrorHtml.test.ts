import { describe, expect, it } from "vitest";
import { mkdtempSync, writeFileSync } from "fs";
import { tmpdir } from "os";
import path from "path";
import { parseErrorHtml } from "../parseErrorHtml";

// Captured verbatim from a real FVU run (24GFVU.jar) against a generated
// statement missing the "State name" field for a State Government AO.
const REAL_ERR_HTML =
  "<HTML><BODY><H3><CENTER> Form 24G / Form 137 - ERROR FILE </CENTER></H3><TABLE BORDER=1 CELLSPACING=0 CELLPADDING=0 BORDERCOLOR=000000 style='border-collapse:collapse; border:none;mso-border-alt:solid windowtext .5pt;mso-padding-alt:0in 5.4pt 0in 5.4pt'><TR><TD WIDTH=70 VALIGN=top style='text-align:right' ><B>   Line No </B></CENTER></TD><TD WIDTH=85 VALIGN=top><B>  Record Type </B></CENTER></TD><TD WIDTH=85 VALIGN=top><B>  Field Name & No.* </B></CENTER></TD><TD WIDTH=85 VALIGN=top><B>  Transaction Detail No </B></CENTER></TD><TD WIDTH=150 VALIGN=top><B>  Error Code </B></CENTER></TD><TD WIDTH=585 VALIGN=top><B>  Error Description </B></CENTER></TD></TR><TR><TD ALIGN=RIGHT WIDTH=70> 2</TD><TD ALIGN=LEFT WIDTH=85> Batch Record </TD><TD ALIGN=LEFT WIDTH=50> State Name(47) </TD><TD ALIGN=LEFT WIDTH=85> - </TD><TD ALIGN=LEFT WIDTH=130> F137/F24G-FV-2134 </TD><TD ALIGN=LEFT WIDTH=585>  Numeric code of state should be provided for state government, refer Annexure 1.  </TD></TR></TABLE></BODY></HTML>";

describe("parseErrorHtml", () => {
  it("parses the FVU's error table into structured field errors", async () => {
    const dir = mkdtempSync(path.join(tmpdir(), "fvu-err-"));
    const filePath = path.join(dir, "err.html");
    writeFileSync(filePath, REAL_ERR_HTML, "utf8");

    const errors = await parseErrorHtml(filePath);

    expect(errors).toEqual([
      {
        lineNo: "2",
        recordType: "Batch Record",
        fieldName: "State Name",
        fieldIndex: 47,
        transactionDetailNo: "-",
        errorCode: "F137/F24G-FV-2134",
        errorDescription: "Numeric code of state should be provided for state government, refer Annexure 1.",
      },
    ]);
  });

  it("returns an empty array when the file doesn't exist", async () => {
    const errors = await parseErrorHtml("/nonexistent/path/err.html");
    expect(errors).toEqual([]);
  });
});
