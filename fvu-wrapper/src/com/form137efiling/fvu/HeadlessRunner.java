package com.form137efiling.fvu;

import com.tin.etbaf.form24G.fvu.TBAFFormatValidator;
import com.tin.etbaf.form24G.fvu.TBAFInterface;
import com.tin.etbaf.form24G.util.Hash;
import com.tin.etbaf.form24G.util.TBAFFileGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Headless equivalent of the 24GFVU.jar Swing GUI's "Validate" button.
 *
 * The vendor jar (com.tin.etbaf.form24G.*) has no CLI/validate mode of its
 * own — it's a Swing application. Its validation and file-generation logic
 * (TBAFFormatValidator, Hash, TBAFFileGenerator) are plain classes with no
 * GUI dependency, so this class drives them directly in the same order the
 * GUI's button handler does, without ever creating a JFrame.
 *
 * Usage: java -cp "24GFVU.jar:<deps>:." com.form137efiling.fvu.HeadlessRunner
 *          <inputTxtPath> <outputDir> <baseName>
 *
 * Prints exactly one line of JSON to stdout describing the outcome.
 */
public class HeadlessRunner {

    public static void main(String[] args) {
        if (args.length != 3) {
            printResult(false, "usage", "Expected 3 arguments: <inputTxtPath> <outputDir> <baseName>", null, null, null, null);
            System.exit(1);
            return;
        }

        String inputPath = args[0];
        String outputDir = args[1];
        String baseName = args[2];

        try {
            run(inputPath, outputDir, baseName);
        } catch (Exception e) {
            printResult(false, "exception", String.valueOf(e), null, null, null, null);
            System.exit(1);
        }
    }

    private static void run(String inputPath, String outputDir, String baseName) throws Exception {
        File inputFile = new File(inputPath);
        if (!inputFile.exists() || inputFile.length() == 0) {
            printResult(false, "input", "Input file not found or empty: " + inputPath, null, null, null, null);
            System.exit(1);
            return;
        }

        // Standalone FVU (utilityLevel = 0), matching TBAFFVU.java's own
        // constant for this jar's non-SAM/SCM mode.
        final int utilityLevel = 0;
        final int fhFieldCount = 8;
        final int paperReturnFlag = 0;

        String errorFileName = new File(outputDir, baseName + ".err").getPath();
        String hashFileName = new File(outputDir, baseName + ".fvu").getPath();
        String statisticFileName = new File(outputDir, baseName + ".html").getPath();
        String transferVoucherFileName = new File(outputDir, baseName + "24G.html").getPath();
        String htmlErrorFileName = new File(outputDir, baseName + "err.html").getPath();

        TBAFFormatValidator validator = new TBAFFormatValidator();
        validator.readFile(inputPath, errorFileName, utilityLevel);

        if (validator.errStrBuff.errorBufferString.length() == 0) {
            Hash hash = new Hash();
            int hashError = hash.startProcessing(inputPath, hashFileName, utilityLevel, fhFieldCount, paperReturnFlag);

            if (hashError == 0) {
                TBAFFileGenerator generator = new TBAFFileGenerator();
                String fileHash = extractFileLevelHash(hashFileName);
                generator.generateStatisticFile(validator, statisticFileName, baseName + ".txt", TBAFInterface.TBAFFVUVersion, fileHash);
                generator.generateTbafForm(inputPath, "^", transferVoucherFileName);

                // generateStatisticFile renders the statistics report to a
                // PDF (via pd4ml) and deletes the intermediate .html it was
                // handed — the real on-disk artifact is "<base>.pdf", not
                // the .html path passed in.
                String statisticPdfPath = new File(outputDir, baseName + ".pdf").getPath();
                printResult(true, null, null, hashFileName, statisticPdfPath, transferVoucherFileName, null);
            } else {
                TBAFFileGenerator generator = new TBAFFileGenerator();
                String message = hashErrorMessage(hashError);
                StringBuffer html = generator.generateHtmlErrorFile(
                        "File Header Record^1^-^^" + message + "\n", true, true, baseName + ".txt");
                generator.writeToFile(htmlErrorFileName, html.toString(), 0, false);

                printResult(false, "hash", message, null, null, null, htmlErrorFileName);
            }
        } else {
            TBAFFileGenerator generator = new TBAFFileGenerator();
            boolean fileAlreadyOpened = validator.errStrBuff.fileOpened;
            StringBuffer html = generator.generateHtmlErrorFile(
                    validator.errStrBuff.errorBufferString.toString(),
                    !fileAlreadyOpened,
                    true,
                    baseName + ".txt");
            generator.writeToFile(htmlErrorFileName, html.toString(), 0, fileAlreadyOpened);

            printResult(false, "validation", "Format validation errors found.", null, null, null, htmlErrorFileName);
        }
    }

    /**
     * Mirrors the GUI's own lookup of the FH record's "FVU File Level Hash"
     * field (position 10, 0-indexed) out of the freshly hashed .fvu file,
     * needed as an input to generateStatisticFile.
     */
    private static String extractFileLevelHash(String hashFileName) throws Exception {
        File fvuFile = new File(hashFileName);
        if (!fvuFile.exists()) return null;

        String fileHash = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(fvuFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = (line + "-").split("\\^");
                if (fields.length > 10 && fields[1].equals(TBAFInterface.TBAF_FH_REC)) {
                    fileHash = fields[10];
                }
            }
        }
        return fileHash;
    }

    private static String hashErrorMessage(int hashError) {
        switch (hashError) {
            case 3: return TBAFInterface.TBAF_FV_1014;
            case 4: return TBAFInterface.TBAF_FV_1017;
            case 5: return TBAFInterface.TBAF_FV_1018;
            case 9: return TBAFInterface.TBAF_FV_1019;
            case 10: return TBAFInterface.TBAF_FV_1020;
            case 11: return TBAFInterface.TBAF_FV_1021;
            default: return TBAFInterface.TBAF_FV_1015;
        }
    }

    private static void printResult(
            boolean success,
            String stage,
            String message,
            String fvuFilePath,
            String statisticFilePath,
            String receiptFilePath,
            String errHtmlPath) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":").append(success);
        json.append(",\"stage\":").append(jsonString(stage));
        json.append(",\"message\":").append(jsonString(message));
        json.append(",\"fvuFilePath\":").append(jsonString(fvuFilePath));
        json.append(",\"statisticFilePath\":").append(jsonString(statisticFilePath));
        json.append(",\"receiptFilePath\":").append(jsonString(receiptFilePath));
        json.append(",\"errHtmlPath\":").append(jsonString(errHtmlPath));
        json.append("}");
        System.out.println(json);
    }

    private static String jsonString(String value) {
        if (value == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
