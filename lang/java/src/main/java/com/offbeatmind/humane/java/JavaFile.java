package com.offbeatmind.humane.java;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.Problem;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.UnknownType;
import com.offbeatmind.humane.core.SourceFile;

/**
 * Represent a single parsed Java source file.
 * 
 * @author humanejava
 *
 */
public class JavaFile extends SourceFile {
    
    /**
     * The source tree this file was found in.
     */
    private final JavaSourceTree sourceTree;
    
    /**
     * Directories recursed through from the source tree root to this file.
     * 
     * @see #expectedPackage
     */
    private final List<String> packagePath;
    
    /**
     * Expected package name in the file.
     * 
     * @see #packagePath
     */
    private final String expectedPackage;
    
    /**
     * Name of the compilation unit (file name without the extension).
     */
    private final String unitName;
    
    /**
     * Parsed compilation unit.
     */
    private final CompilationUnit compilationUnit;

    /**
     * Parses the file and constructs this object.
     * 
     * @param sourceTree
     *     The source tree this file was found in.
     *     
     * @param packagePath
     *     Directories recursed through from the source tree root to this file.
     *     
     * @param sourceFile
     *     The file system file to read and parse.
     *     
     * @throws IOException
     */
    public JavaFile(JavaSourceTree sourceTree, List<String> packagePath, File sourceFile) throws IOException {
        this(sourceTree, packagePath, sourceFile, getDefaultParserConfiguration());
    }

    /**
     * Parses the file and constructs this object.
     * 
     * @param sourceTree
     *     The source tree this file was found in.
     *     
     * @param packagePath
     *     Directories recursed through from the source tree root to this file.
     *     
     * @param sourceFile
     *     The file system file to read and parse.
     *     
     * @param config
     *     Configuration to use when processing this file.
     *     
     * @throws IOException
     */
    private JavaFile(JavaSourceTree sourceTree, List<String> packagePath, File sourceFile, ParserConfiguration config)
    throws IOException {
        super(sourceFile);
        
        this.sourceTree = sourceTree;

        this.packagePath = Collections.unmodifiableList(packagePath);
        StringBuilder pp = new StringBuilder();

        for (String p : packagePath) {
            if (pp.length() > 0) pp.append('.');
            pp.append(p);
        }
        this.expectedPackage = (pp.length() > 0) ? pp.toString() : null;
        String fileName = sourceFile.getName();
        this.unitName = fileName.substring(0, fileName.length() - ".java".length());

        JavaParser parser = new JavaParser(config);
        ParseResult<CompilationUnit> result = parser.parse(getSourceText());

        if (!result.isSuccessful()) {
            for (Problem p : result.getProblems()) {
                System.err.println(p.getLocation() + ": " + p.getVerboseMessage());
            }
            throw new RuntimeException("Could not parse: " + sourceFile.getPath() + ": " + result.toString());
        }
        compilationUnit = result.getResult().get();
        System.out.println("Parsed:  " + sourceFile.getPath());

        organizeElements();
    }
    
    /**
     * Returns the source tree this file was found in.
     * @return
     */
    public JavaSourceTree getSourceTree() {
        return sourceTree;
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

    /**
     * Returns the directories recursed through from the source tree root to this file.
     * Note: the returned list is unmodifiable.
     */
    public List<String> getPackagePath() {
        return packagePath;
    }

    /**
     * Returns the package name expected to be used in the file.
     */
    public String getExpectedPackage() {
        return expectedPackage;
    }

    /**
     * Returns the name of the unit (name of the file without the extension).
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * Returns the parsed compilation unit.
     */
    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    /**
     * Returns all the top-level source elements in this file.
     */
    public List<SourceElement> getElements() {
        return NodeElement.of(compilationUnit).getElements();
    }

    /**
     * Initializes - constructs and orders the elements of code in order they appear in the source.
     */
    private void organizeElements() {
        for (JavaToken token : compilationUnit.getTokenRange().get()) {
            Range tokenRange = token.getRange().orElseThrow(
                () -> new RuntimeException("Token without range: " + token)
            );

            Node owner = findNodeForRange(compilationUnit, tokenRange);

            if (owner == null) {
                throw new RuntimeException("Token without node owning it: " + token);
            }
            NodeElement<?> ownerElement = NodeElement.of(owner);
            ownerElement.addElement(new TokenElement(token, ownerElement));
        }
    }

    /**
     * Finds which most-specific JavaParser sub-node of specified {@code rootNode} 
     * best contains the given {@code rangeToFind}. 
     *  
     * @param rootNode
     *     Node to start digging down (recursing) from.
     *     
     * @param rangeToFind
     *     Source code range to find.
     *     
     * @return
     *     Node that most specifically contains the range or {@code null} if none found.
     */
    private static Node findNodeForRange(Node rootNode, Range rangeToFind) {
        if (isPhantomNode(rootNode)) {
            return null;
        }
        if (rootNode.getRange().get().contains(rangeToFind)) {
            for (Node child : rootNode.getChildNodes()) {
                Node found = findNodeForRange(child, rangeToFind);

                if (found != null) {
                    return found;
                }
            }
            return rootNode;
        } else {
            return null;
        }
    }

    /**
     * Returns {@code true} if the given node is a phantom one and not to be
     * included in processing.
     * 
     * Warning: does NOT consider if parents are phantom!
     * They must be filtered on their own. Assumes that the caller will simply
     * not recurse down into the phantom nodes in the first place.
     * 
     * @see JavaFile#findNodeForRange(Node, Range)
     */
    private static boolean isPhantomNode(Node node) {
        if (node instanceof UnknownType) return true;
        final Optional<Node> parent = node.getParentNode();

        if (parent.isPresent()) {
            if (!parent.get().getRange().get().contains(node.getRange().get())) return true;
        }
        return false;
    }

    /**
     * Calls the consumer for each occurrence of a node of a specified {@code nodeType} in the file.
     * 
     * @param nodeType
     *     Type of the node to find and make the call for.
     *     
     * @param consumer
     *     Consumer to call the {@link Consumer#accept(Object)} on for each matching node.
     *     
     * @see #walkElements(Class, Consumer)
     */
    public <T extends Node> void walkNodes(Class<T> nodeType, Consumer<T> consumer) {
        getCompilationUnit().walk(nodeType, consumer);
    }

    /**
     * Calls the consumer for each occurrence of an element containing a 
     * node of a specified {@code nodeType} in the file.
     * 
     * @param nodeType
     *     Type of the node to find and make the call for.
     *     
     * @param consumer
     *     Consumer to call the {@link Consumer#accept(Object)} on for each matching node.
     *     
     * @see #walkNodes(Class, Consumer)
     */
    public <T extends Node> void walkElements(Class<T> nodeType, Consumer<NodeSourceElement<T>> consumer) {
        walkNodes(nodeType, new Consumer<T>() {

            @Override
            public void accept(T node) {
                consumer.accept(NodeElement.of(node));
            }
        });
    }
}
