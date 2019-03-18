package com.offbeatmind.humane.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.Position;
import com.github.javaparser.Problem;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.UnknownType;

public class JavaFile {
    
    private final SourceTree sourceTree;
    private final List<String> packagePath;
    private final String expectedPackage;
    private final File sourceFile;
    private final String source;
    private final EndOfLineStyle eol;
    private final String[] lines;
    private final String unitName;
    private final CompilationUnit compilationUnit;
    
    private final TreeMap<Position, Violation> violations = new TreeMap<>();
    
    public JavaFile(SourceTree sourceTree, List<String> packagePath, File sourceFile) throws IOException {
        this(sourceTree, packagePath, sourceFile, getDefaultParserConfiguration());
    }
    
    private JavaFile(SourceTree sourceTree, List<String> packagePath, File sourceFile, ParserConfiguration config) throws IOException {
        this.sourceTree = sourceTree;
        this.packagePath = Collections.unmodifiableList(packagePath);
        StringBuilder pp = new StringBuilder();
        for (String p: packagePath) {
            if (pp.length() > 0) pp.append('.');
            pp.append(p);
        }
        this.expectedPackage = (pp.length() > 0) ? pp.toString() : null;
        this.sourceFile = sourceFile;
        String fileName = sourceFile.getName();
        this.unitName = fileName.substring(0, fileName.length() - ".java".length());
        
        source = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);
        
        
        eol = EndOfLineStyle.recognize(source);
        if (eol == null) {
            // All in one line.
            lines = new String[] { source };
        } else {
            lines = source.split(eol.getSequencePattern());
        }
        
        //System.out.println("EOL:   " + eol);
        //System.out.println("Lines: " + lines.length);
        //System.out.println(source);
        //System.out.flush();
        
        JavaParser parser = new JavaParser(config);
        ParseResult<CompilationUnit> result = parser.parse(source);
        if (!result.isSuccessful()) {
            for (Problem p: result.getProblems()) {
                System.err.println(p.getLocation() + ": " + p.getVerboseMessage());
            }
            throw new RuntimeException("Could not parse: " + sourceFile.getPath() + ": " + result.toString());
        }
        compilationUnit = result.getResult().get();
        System.out.println("Parsed:  " + sourceFile.getPath());
        
        organizeElements();
    }
    
    private static ParserConfiguration getDefaultParserConfiguration() {
        ParserConfiguration config = new ParserConfiguration();
        config.setAttributeComments(false);
        config.setDoNotAssignCommentsPrecedingEmptyLines(true);
        config.setIgnoreAnnotationsWhenAttributingComments(true);
        config.setTabSize(1); // We'll have to handle this and we don't want tabs at all
        config.setLexicalPreservationEnabled(true);
        config.setLanguageLevel(LanguageLevel.JAVA_8);
        config.setPreprocessUnicodeEscapes(false);
        return config;
    }
    
    public SourceTree getSourceTree() {
        return sourceTree;
    }
    
    public List<String> getPackagePath() {
        return packagePath;
    }
    
    public String getExpectedPackage() {
        return expectedPackage;
    }
    
    public String getUnitName() {
        return unitName;
    }

    public File getSourceFile() {
        return sourceFile;
    }
    
    public String getSource() {
        return source;
    }
    
    public EndOfLineStyle getEndOfLineStyle() {
        return eol;
    }
    
    public String getLine(int lineNumber) {
        return lines[lineNumber - 1];
    }
    
    public int getLineCount() {
        return lines.length;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }
    
    public List<SourceElement> getElements() {
        return NodeElement.of(compilationUnit).getElements();
    }

    private void organizeElements() {
        for (JavaToken token : compilationUnit.getTokenRange().get()) {
            Range tokenRange = token.getRange().orElseThrow(() -> new RuntimeException("Token without range: " + token));
            Node owner = findNodeForToken(compilationUnit, tokenRange);
            if (owner == null) {
                throw new RuntimeException("Token without node owning it: " + token);
            }
            NodeElement<?> ownerElement = NodeElement.of(owner);
            ownerElement.addElement(new TokenElement(token, ownerElement));
        }
    }
    
    public void addViolation(Violation violation) {
        violations.put(violation.getPosition(), violation);
    }
    

    private static Node findNodeForToken(Node node, Range tokenRange) {
        if (isPhantomNode(node)) {
            return null;
        }
        if (node.getRange().get().contains(tokenRange)) {
            for (Node child : node.getChildNodes()) {
                Node found = findNodeForToken(child, tokenRange);
                if (found != null) {
                    return found;
                }
            }
            return node;
        } else {
            return null;
        }
    }
    
    /**
     * Warning: does NOT consider if parents are phantom! 
     * They must be filtered on their own.
     */
    private static boolean isPhantomNode(Node node) {
        if (node instanceof UnknownType) return true;
        final Optional<Node> parent = node.getParentNode();
        if (parent.isPresent()) {
            if (!parent.get().getRange().get().contains(node.getRange().get())) return true;
        }
        return false;
    }
    
    public <T extends Node> void walkNodes(Class<T> nodeType, Consumer<T> consumer) {
        getCompilationUnit().walk(nodeType, consumer);
    }
    
    public <T extends Node> void walkElements(Class<T> nodeType, Consumer<NodeSourceElement<T>> consumer) {
        walkNodes(nodeType, new Consumer<T>() {

            @Override
            public void accept(T node) {
                consumer.accept(NodeElement.of(node));
            }
        });
    }

    public void printViolations() {
        for (Violation v: violations.values()) {
            SourceElement e = v.getViolatingElement();
            if (e.isNode()) {
                System.err.println(e.getName() + " @ " + v.getRange() +  ": " + v.getMessage());
            } else {
                System.err.println(e.getName() + " @ " + v.getPosition() + ": " + v.getMessage());
            }
            
        }
        
    }
}
