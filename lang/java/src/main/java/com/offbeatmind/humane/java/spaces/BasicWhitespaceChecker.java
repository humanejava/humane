package com.offbeatmind.humane.java.spaces;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.ReceiverParameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr.Operator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeParameters;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.offbeatmind.humane.core.JavaFile;
import com.offbeatmind.humane.core.NodeSourceElement;
import com.offbeatmind.humane.core.SourceElement;
import com.offbeatmind.humane.core.TokenSourceElement;
import com.offbeatmind.humane.java.JavaFileProcessor;

public class BasicWhitespaceChecker extends JavaFileProcessor {

    private static final boolean ALLOW_UNSPACED_COMMAS_IN_SINGLE_LETTER_TYPE_ARGS = true;

    private static enum WhitespaceExpectation {
        NON_WHITESPACE_REQUIRED(false, true, true, true),
        ANYTHING(true, true, true, true),
        WHITESPACE_REQUIRED(true, false, false, false),
        WHITESPACE_OR_EXPRESSION_ENDER_REQUIRED(true, true, false, false),
        ARGUMENTS_REQUIRED(false, false, false, true);

        private final boolean whitespacePermitted;
        private final boolean enderPermitted;
        private final boolean nonWhitespacePermitted;
        private final boolean argumentsPermitted;

        private WhitespaceExpectation(
            boolean whitespacePermitted,
            boolean enderPermitted,
            boolean nonWhitespacePermitted,
            boolean argumentsPermitted
        ) {
            this.whitespacePermitted = whitespacePermitted;
            this.enderPermitted = enderPermitted;
            this.nonWhitespacePermitted = nonWhitespacePermitted;
            this.argumentsPermitted = argumentsPermitted;
        }

        public boolean isPermitted(SourceElement element) {
            if (element.isWhitespace()) return whitespacePermitted;

            if (element.isToken()) {
                switch (element.asTokenElement().getText()) {
                    case "(":
                        return argumentsPermitted || nonWhitespacePermitted;

                    case ")":
                    case "]":
                    case ",":
                    case ";":
                        return enderPermitted;

                    case ":":
                        if (!enderPermitted) return nonWhitespacePermitted;

                        Node parent = element.getParent().getNode();

                        if (parent instanceof SwitchEntry) {
                            SwitchEntry se = (SwitchEntry) parent;

                            for (Expression x : se.getLabels()) {
                                if (x.getRange().get().contains(element.getRange().get())) {
                                    return nonWhitespacePermitted;
                                }
                            }
                            for (Statement x : se.getStatements()) {
                                if (x.getRange().get().contains(element.getRange().get())) {
                                    return nonWhitespacePermitted;
                                }
                            }
                        }
                        return enderPermitted;

                    default:
                        return nonWhitespacePermitted;

                }
            }
            return nonWhitespacePermitted;
        }
    }

    private static final String OPTIONAL_EOL_AND_INDENTATION_REGEX = "(([\\r\\n\\t]*[\\r\\n][\\r\\n\\t]*))?";
    private static final String BETWEEN_DOTS_REGEX = "[^.\\r\\n\\t]+";
    
    private static final String DOT_WITH_WHITESPACE_REGEX =
        OPTIONAL_EOL_AND_INDENTATION_REGEX + "\\." + OPTIONAL_EOL_AND_INDENTATION_REGEX;

    private static final String IDENTIFIER_REGEX =
        "^" + BETWEEN_DOTS_REGEX + "(" + DOT_WITH_WHITESPACE_REGEX + BETWEEN_DOTS_REGEX + ")*$";

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile(IDENTIFIER_REGEX);

    private static final String UNSPACED_COMMA_REGEX = "^\\<[?A-Z],[?A-Z](,[?A-Z])*\\>$";
    private static final Pattern UNSPACED_COMMA_PATTERN = Pattern.compile(UNSPACED_COMMA_REGEX);

    // KEYWORD   REQUIRES BEFORE            REQUIRES AFTER         SPECIAL/EXCLUSIONS
    // "new"     WS or (                    WS
    // "this"    WS or . or (               WS|.|(|)|;|,           
    // "super"   WS                         WS or . or (
    // others    WS                         WS
    //
    //  ... new Foo(...
    //  ... (new Foo(...
    //
    //  ... this(...          (constructor reference)
    //  ... this.foo          (field or method reference)
    //  ... this<EOL>.foo     (field or method reference)
    //  Foo.this...           (typed reference)
    //  Foo.this.foo          (explicit reference)
    //  Foo.this.<EOL>foo     (explicit reference)
    //  ... (this)            (in an expression or parameter)
    //  ... , this,           (in a list)
    //  ... this;             (e.g. return this;)
    //
    //  ... super(...         (constructor reference)
    //  ... super.foo         (inherited member reference)
    //  ... super<EOL>.foo    (inherited member reference)

    private SourceElement previousElement = null;
    private WhitespaceExpectation whitespaceExpectation = WhitespaceExpectation.NON_WHITESPACE_REQUIRED;
    private boolean oneSideWhitespaceRequirement = false;

    public BasicWhitespaceChecker(JavaFile javaFile) {
        super(javaFile);
    }

    @Override
    public void process(boolean fixErrors) {
        processElements(javaFile.getElements());
    }

    protected final void processElements(List<SourceElement> elements) {
        for (SourceElement element : elements) {
            if (element.isComment()) {
                processComment(element);
                oneSideWhitespaceRequirement = false;
            } else {
                if (element.isWhitespace()) {
                    processWhitespace(element);
                    oneSideWhitespaceRequirement = false;
                } else {
                    notWhitespace(element);

                    if (element.isToken()) {
                        processToken(element.asTokenElement());
                    } else if (element.isNode()) {
                        processNode(element.asNodeElement());
                        oneSideWhitespaceRequirement = false;
                    }
                }
            }
            previousElement = element;
        }
    }

    private void processWhitespace(SourceElement element) {
        if (!whitespaceExpectation.isPermitted(element)) {
            if (oneSideWhitespaceRequirement) {
                addViolation(new WhitespaceMustExistOnExactlyOneSideViolation(previousElement));
            } else {
                addViolation(new WhitespaceNotAllowedViolation(previousElement, element));
            }
        }
        whitespaceExpectation = WhitespaceExpectation.ANYTHING;
    }

    private void notWhitespace(SourceElement element) {
        if (!whitespaceExpectation.isPermitted(element)) {
            // Special case: {}
            if (
                element.isToken()
                    &&
                    previousElement.isToken() &&
                    (whitespaceExpectation == WhitespaceExpectation.WHITESPACE_OR_EXPRESSION_ENDER_REQUIRED) &&
                    element.asTokenElement().getText().contentEquals("}") &&
                    previousElement.asTokenElement().getText().contentEquals("{")
            ) {
                return;
            }
            if (oneSideWhitespaceRequirement) {
                addViolation(new WhitespaceMustExistOnExactlyOneSideViolation(previousElement));
            } else {
                addViolation(new MissingWhitespaceViolation(previousElement, element));
            }
        }
        whitespaceExpectation = WhitespaceExpectation.ANYTHING;
    }

    private void processComment(SourceElement element) {
        notWhitespace(element);
    }

    @SuppressWarnings("unchecked")
    private void processNode(NodeSourceElement<?> nodeElement) {
        Node node = nodeElement.getNode();

        if (node instanceof Name) {
            processName((NodeSourceElement<Name>) nodeElement);
        } else {
            processElements(nodeElement.getElements());
        }
    }

    private void processName(NodeSourceElement<Name> nodeElement) {
        // Do we want to allow whitespace around dots in the name for those that have to be wrapped?
        String identifier = nodeElement.asNodeElement().getNode().getIdentifier();

        if (!IDENTIFIER_PATTERN.matcher(identifier).matches()) {
            addViolation(new InappropriateIdentifierSpacingViolation(nodeElement));
        }
    }

    private void processToken(TokenSourceElement tokenElement) {
        if (tokenElement.isKeyword()) {
            processKeyword(tokenElement);
            oneSideWhitespaceRequirement = false;
        } else if (tokenElement.isOperator()) {
            processOperator(tokenElement);
        } else if (tokenElement.isSeparator()) {
            processSeparator(tokenElement);
        } else if (tokenElement.isIdentifier()) {
            processIdentifier(tokenElement);
        } else {
            processOrdinaryToken(tokenElement);
            oneSideWhitespaceRequirement = false;
        }
    }

    private void processIdentifier(TokenSourceElement tokenElement) {
        final Node parent = tokenElement.getParent().getNode();
//        System.out.println("#####: " + tokenElement.getToken().getCategory() + ": " + tokenElement.getText() +
//                " in " + parent.getParentNode().get().getClass().getSimpleName() +
//                "/" + parent.getClass().getSimpleName());

        if (parent instanceof SimpleName) {
            final Node grandparent = parent.getParentNode().get();

            if (grandparent instanceof MethodDeclaration) {
                final MethodDeclaration decl = (MethodDeclaration) grandparent;
                // TODO ensure...
                whitespaceExpectation = WhitespaceExpectation.ARGUMENTS_REQUIRED;
            } else if (grandparent instanceof MethodCallExpr) {
                final MethodCallExpr call = (MethodCallExpr) grandparent;
                // TODO ensure...
                whitespaceExpectation = WhitespaceExpectation.ARGUMENTS_REQUIRED;

            } else if (grandparent instanceof ObjectCreationExpr) {
                final ObjectCreationExpr expr = (ObjectCreationExpr) grandparent;
                // TODO ensure...
                whitespaceExpectation = WhitespaceExpectation.ARGUMENTS_REQUIRED;

            }
        }

    }

    private void processSeparator(TokenSourceElement tokenElement) {
        // Commas and semicolons normally require a space afterwards
        // Commas potentially have special cases, such as in generic type args.

        final String text = tokenElement.getText();

        switch (text) {
            case ",":
                previousMustNotBeWhitespace(tokenElement);

                if (isUnspacedCommaAllowed(tokenElement)) {
                    whitespaceExpectation = WhitespaceExpectation.ANYTHING;
                } else {
                    whitespaceExpectation = WhitespaceExpectation.WHITESPACE_REQUIRED;
                }
                break;

            case ";":
                previousMustNotBeWhitespace(tokenElement);
                whitespaceExpectation = WhitespaceExpectation.WHITESPACE_REQUIRED;
                break;

            case "{":
                this.previousMustBeWhitespaceOr(tokenElement, "[", "(");
                whitespaceExpectation = WhitespaceExpectation.WHITESPACE_OR_EXPRESSION_ENDER_REQUIRED;
                break;

            case "}":
                previousMustBeWhitespaceOr(tokenElement, "}", "{");
                whitespaceExpectation = WhitespaceExpectation.WHITESPACE_OR_EXPRESSION_ENDER_REQUIRED;

            default:
                // other braces, dot, @
                whitespaceExpectation = WhitespaceExpectation.ANYTHING;
                break;
        }
    }

    private boolean isUnspacedCommaAllowed(TokenSourceElement tokenElement) {
        if (!ALLOW_UNSPACED_COMMAS_IN_SINGLE_LETTER_TYPE_ARGS) return false;

        //System.out.println("$$$$$ Comma found: " + tokenElement.getRange().get().begin);

        final NodeSourceElement<?> parentElement = tokenElement.getParent();
        final Node parentNode = parentElement.getNode();

        if (parentNode instanceof NodeWithTypeArguments) {
            final NodeWithTypeArguments<?> node = (NodeWithTypeArguments<?>) parentNode;
            final NodeList<Type> typeArgs = node.getTypeArguments().orElse(null);

            if ((typeArgs != null) && isUnspacedCommaAllowedInTypes(typeArgs, tokenElement)) {
                return true;
            }

        }

        if (parentNode instanceof NodeWithTypeParameters) {
            final NodeWithTypeParameters<?> node = (NodeWithTypeParameters<?>) parentNode;

            if (isUnspacedCommaAllowedInTypeParams(node.getTypeParameters(), tokenElement)) {
                return true;
            }
        }

        if (parentNode instanceof ClassOrInterfaceDeclaration) {
            final ClassOrInterfaceDeclaration node = (ClassOrInterfaceDeclaration) parentNode;

            if (isUnspacedCommaAllowedInTypes(node.getExtendedTypes(), tokenElement)) {
                return true;
            }

            if (isUnspacedCommaAllowedInTypes(node.getImplementedTypes(), tokenElement)) {
                return true;
            }

            return false;
        } else if (parentNode instanceof EnumDeclaration) {
            final EnumDeclaration node = (EnumDeclaration) parentNode;

            if (isUnspacedCommaAllowedInTypes(node.getImplementedTypes(), tokenElement)) {
                return true;
            }

            return false;
        } else if (parentNode instanceof FieldDeclaration) {
            final FieldDeclaration node = (FieldDeclaration) parentNode;

            if (isUnspacedCommaAllowedInType(node.getElementType(), tokenElement)) {
                return true;
            }
            return false;
        } else if (parentNode instanceof CallableDeclaration) {
            final CallableDeclaration<?> node = (CallableDeclaration<?>) parentNode;

            if (isUnspacedCommaAllowedInParameters(node.getParameters(), tokenElement)) {
                return true;
            }

            if (parentNode instanceof MethodDeclaration) {
                final MethodDeclaration method = (MethodDeclaration) parentNode;

                if (isUnspacedCommaAllowedInType(method.getType(), tokenElement)) {
                    return true;
                }

                Optional<ReceiverParameter> orp = method.getReceiverParameter();

                if (orp.isPresent()) {
                    if (isUnspacedCommaAllowedInType(orp.get().getType(), tokenElement)) {
                        return true;
                    }
                }
            }

            return false;
        } else if (parentNode instanceof MethodCallExpr) {
            final MethodCallExpr call = (MethodCallExpr) parentNode;

            Optional<NodeList<Type>> ota = call.getTypeArguments();

            if (ota.isPresent()) {
                if (isUnspacedCommaAllowedInTypes(ota.get(), tokenElement)) {
                    return true;
                }
            }

            return false;
        } else if (parentNode instanceof Type) {
            final Type type = (Type) parentNode;

            if (isUnspacedCommaAllowedInType(type, tokenElement)) {
                return true;
            }

            return false;
        }
        //System.out.println("@@@@@ Couldn't figure out comma: " + tokenElement.getParent().getNode().getClass().getSimpleName() + ": " + tokenElement.getParent().getNode());

        return false;
    }

    private boolean isUnspacedCommaAllowedInParameters(
        NodeList<Parameter> parameters, TokenSourceElement tokenElement
    ) {
        for (Parameter parameter : parameters) {
            if (isUnspacedCommaAllowedInType(parameter.getType(), tokenElement)) return true;
        }
        return false;
    }

    private boolean isUnspacedCommaAllowedInType(Type type, TokenSourceElement tokenElement) {
        if (type.getRange().get().contains(tokenElement.getRange().get())) {
            // Unfortunately, there seems to be no way to further drill this down
            // so now we must operate on strings.
            //
            // Fortunately, this case requires that everything is in a single line.
            // If not, we MUST return false.

            final Position position = tokenElement.getRange().get().begin;
            final int lineNumber = position.line;
            final int columnNumber = position.column;
            final String line = javaFile.getLine(lineNumber);

            // There must be a '<' before and a '>' after. 
            // If either is missing, this is not the case we're after.
            final int beginIndex = line.lastIndexOf('<', columnNumber - 2);
            if (beginIndex < 0) return false;

            final int endIndex = line.indexOf('>', columnNumber);
            if (endIndex < 0) return false;

            final String range = line.substring(beginIndex, endIndex + 1);

            return UNSPACED_COMMA_PATTERN.matcher(range).matches();
        }
        return false;
    }

    private boolean isUnspacedCommaAllowedInTypes(NodeList<? extends Type> typeArgs, TokenSourceElement tokenElement) {
        for (Type type : typeArgs) {
            if (isUnspacedCommaAllowedInType(type, tokenElement)) return true;
        }
        return false;
    }

    private boolean isUnspacedCommaAllowedInTypeParams(
        NodeList<TypeParameter> typeParameters, TokenSourceElement tokenElement
    ) {
        for (TypeParameter typeParameter : typeParameters) {
            if (isUnspacedCommaAllowedInTypes(typeParameter.getTypeBound(), tokenElement)) {
                return true;
            }
        }
        return false;
    }

    private void processOperator(TokenSourceElement tokenElement) {
        // Binary operators require spacing on both sides.
        // Unary operators must be preceded by whitespace and not followed by one.
        // Increment/decrement operators must have space on one side only.

        final Node parent = tokenElement.getParent().getNode();

        if (parent instanceof UnaryExpr) {
            final UnaryExpr ux = (UnaryExpr) parent;
            final Operator operator = ux.getOperator();
            final Expression expression = ux.getExpression();

            if (
                operator.asString().contentEquals(tokenElement.getText())
                    &&
                    !expression.getRange().get().contains(tokenElement.getRange().get())
            ) {
                if (operator.isPrefix()) {
                    // Prefix unary operators that must be preceded and followed by a space
                    previousMustBeWhitespaceOr(tokenElement, "[", "(");
                    whitespaceExpectation = WhitespaceExpectation.NON_WHITESPACE_REQUIRED;
                    oneSideWhitespaceRequirement = false;
                    return;
                } else if (operator.isPostfix()) {
                    previousMustNotBeWhitespace(tokenElement);
                    whitespaceExpectation = WhitespaceExpectation.WHITESPACE_OR_EXPRESSION_ENDER_REQUIRED;
                    oneSideWhitespaceRequirement = false;
                    return;
                }
            }
        }

        switch (tokenElement.getText()) {
            case "!":
            case "~":
                // Unary operators that must not be followed by a space
                whitespaceExpectation = WhitespaceExpectation.NON_WHITESPACE_REQUIRED;
                oneSideWhitespaceRequirement = false;
                break;

            case "--":
            case "++":
                // Increment/decrement operators that must have space on ONE side
                if (previousWasWhitespace()) {
                    whitespaceExpectation = WhitespaceExpectation.NON_WHITESPACE_REQUIRED;
                } else {
                    whitespaceExpectation = WhitespaceExpectation.WHITESPACE_REQUIRED;
                }
                oneSideWhitespaceRequirement = true;
                break;

            case "::":
                this.previousMustNotBeWhitespace(tokenElement);
                whitespaceExpectation = WhitespaceExpectation.NON_WHITESPACE_REQUIRED;
                break;

            case "<":
            case ">":
            case "?":
                // These two may actually be used as for type parameters, not operators
                // Must account for that.
                boolean isSeparator;

                if (parent instanceof VariableDeclarationExpr) {
                    VariableDeclarationExpr vdx = (VariableDeclarationExpr) parent;

                    if (vdx.getCommonType().getRange().get().contains(tokenElement.getRange().get())) {
                        isSeparator = true;
                    } else {
                        isSeparator = false;
                    }
                } else if (parent instanceof Expression) {
                    isSeparator = false;
                } else {
                    isSeparator = true;
                }
                if (isSeparator) {
                    processSeparator(tokenElement);
                    break;
                }
                // Otherwise fall through...

            default:
                whitespaceExpectation = WhitespaceExpectation.WHITESPACE_REQUIRED;
                oneSideWhitespaceRequirement = false;
                break;
        }
    }

    private void processKeyword(TokenSourceElement tokenElement) {
        final String keyword = tokenElement.getText();

        switch (keyword) {
            case "this":
            case "super":
            case "new":
            case "boolean":
            case "char":
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
            case "true":  // just in case, if this gets parsed as a keyword
            case "false": // just in case, if this gets parsed as a keyword
            case "null":  // just in case, if this gets parsed as a keyword
                whitespaceExpectation = WhitespaceExpectation.ANYTHING;
                break;

            case "class":
                // Used both in class declarations and references such as Foo.class
                if (tokenElement.getParent().getNode() instanceof ClassOrInterfaceDeclaration) {
                    this.previousMustBeWhitespace(tokenElement);
                    whitespaceExpectation = WhitespaceExpectation.WHITESPACE_REQUIRED;
                } else {
                    // We must have a dot as a previous token
                    if (previousElement.isToken()) {
                        if (previousElement.asTokenElement().getText().contentEquals(".")) {
                            whitespaceExpectation = WhitespaceExpectation.ANYTHING;
                        } else {
                            // TODO Not-a-dot here is unexpected
                            whitespaceExpectation = WhitespaceExpectation.ANYTHING;
                        }
                    }
                }
                break;

            default:
                previousMustBeWhitespaceOr(tokenElement, "[", "(");
                whitespaceExpectation = WhitespaceExpectation.WHITESPACE_OR_EXPRESSION_ENDER_REQUIRED;
                break;
        }
    }

    private void previousMustBeWhitespace(SourceElement element) {
        if (!previousWasWhitespace()) {
            addViolation(new MissingWhitespaceViolation(previousElement, element));
        }
    }

    private void previousMustBeWhitespaceOr(SourceElement element, String... alternatives) {
        if (previousWasWhitespace()) return;

        if (previousElement.isToken()) {
            final String text = previousElement.asTokenElement().getText();

            for (String alternative : alternatives) {
                if (text.contentEquals(alternative)) return;
            }
        }
        addViolation(new MissingWhitespaceViolation(previousElement, element));
    }

    private void previousMustNotBeWhitespace(SourceElement element) {
        if (previousWasWhitespace()) {
            addViolation(new WhitespaceNotAllowedViolation(previousElement, element));
        }
    }

    private boolean previousWasWhitespace() {
        return (previousElement == null) || previousElement.isWhitespace();
    }

    private void processOrdinaryToken(TokenSourceElement tokenElement) {
        // Nothing (yet) to assume or do that isn't addressed in the caller methods.
        whitespaceExpectation = WhitespaceExpectation.ANYTHING;
    }
}
