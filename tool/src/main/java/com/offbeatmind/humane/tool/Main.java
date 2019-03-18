package com.offbeatmind.humane.tool;

import java.io.File;
import java.io.IOException;

import com.offbeatmind.humane.core.Language;
import com.offbeatmind.humane.core.SourceTree;
import com.offbeatmind.humane.java.JavaLanguage;

public class Main {
    
    private static Language currentLanguage = JavaLanguage.INSTANCE;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            help();
        } else {
            for (String arg: args) {
                processArg(arg);
            }
        }
    }

    private static void processArg(String arg) throws IOException {
        if (arg.startsWith("--")) {
            processLongSwitch(arg);
        } else if (arg.startsWith("-")) {
            processShortSwitch(arg);
        } else {
            processPath(arg);
        }
    }

    private static void processLongSwitch(String arg) {
        // TODO Auto-generated method stub
        
    }

    private static Object switchValue(String arg) {
        return arg.substring(arg.indexOf('=') + 1);
    }
    
    private static void processShortSwitch(String arg) {
        if (arg.contentEquals("-?") || arg.contentEquals("-h") || arg.contentEquals("--help")) {
            help();
        } else if (arg.contentEquals("-j") || arg.contentEquals("--java")) {
            setJava();
        } else if (arg.contentEquals("-v") || arg.contentEquals("--validate-only")) {
            setFix(false);
        } else if (arg.contentEquals("-f") || arg.contentEquals("--fix")) {
            setFix(true);
        } else if (arg.startsWith("-c=") || arg.startsWith("--config=")) {
            loadConfig(switchValue(arg));
        } else if (arg.startsWith("-hr=") || arg.startsWith("--html-report=")) {
            setHtmlReport(switchValue(arg));
        } else if (arg.startsWith("-xr=") || arg.startsWith("--xml-report=")) {
            setXmlReport(switchValue(arg));
        } else if (arg.startsWith("-tr=") || arg.startsWith("--text-report=")) {
            setTextReport(switchValue(arg));
        } else {
            System.err.println("Unrecognized switch: " + arg);
            System.err.flush();
            System.exit(1);
        }        
    }

    private static void setFix(boolean b) {
        // TODO Auto-generated method stub
        
    }

    private static void setJava() {
        currentLanguage = JavaLanguage.INSTANCE;
        
    }

    private static void loadConfig(Object switchValue) {
        // TODO Auto-generated method stub
        
    }

    private static void setHtmlReport(Object switchValue) {
        // TODO Auto-generated method stub
        
    }

    private static void setXmlReport(Object switchValue) {
        // TODO Auto-generated method stub
        
    }

    private static void setTextReport(Object switchValue) {
        // TODO Auto-generated method stub
        
    }

    private static void processPath(String arg) throws IOException {
        SourceTree sourceTree = new SourceTree(new File(arg));
        currentLanguage.check(sourceTree);
    }

    private static void help() {
        System.out.println("Arguments are as follow and can occur any number of times, in any order");
        System.out.println("(order is important):");
        System.out.println();
        System.out.println("-?, -h and/or --help");
        System.out.println("   Prints this message.");
        System.out.println();
        System.out.println("-j and/or --java");
        System.out.println("    Specifies that the language for subsequent paths is Java (default).");
        System.out.println();
        System.out.println("-c=<path> and/or --config=<path>");
        System.out.println("    Specifies the configuration file to load.");
        System.out.println();
        System.out.println("-v or --validate-only");
        System.out.println("    Enables validation-only mode, disables fixing (see next).");
        System.out.println();
        System.out.println("-f or --fix");
        System.out.println("    Enables fixing of formatting/style errors.");
        System.out.println();
        System.out.println("-hr=<path> and/or --html-report=<path>");
        System.out.println("    Specifies the file to write the HTML report into.");
        System.out.println();
        System.out.println("-xr=<path> and/or --xml-report=<path>");
        System.out.println("    Specifies the file to write the XML report into.");
        System.out.println();
        System.out.println("-tr=<path> and/or --text-report=<path>");
        System.out.println("    Specifies the file to write the text report into.");
        System.out.println();
        System.out.println("<source-path>");
        System.out.println("    Specifies a Java source tree to analyze.");
        return;
    }
}
