# Humane Java Code Style

## Style, Revisited

Some people prefer braces at the end of the line. Others place them at the beginning of a new line.
Some arguments for both relate to consistency vs readability impacted by vertical spacing, for example. 
However, existing standard guides (both types) fail to be consistent themselves and it all seems to start
with the problem of "line wrapping".

The first problem is defining what is a "line" and deciding when should any wrapping apply?
Most (if not all) formats out there suggest that, when a line is wrapped, the continuation should be 
indented, usually double the normal indentation, arguably to make it visually distinct from both
following statements or a normally indented block that may follow. For example:

<table>
<thead><tr valign="top">
  <th>'{' on the same line</th>
  <th>'{' on new line</th>
</tr></thead><tbody><tr valign="top"><td>
<pre lang="java">
        if (foo.equalsIgnoreCase("foo") &&
                bar.equalsIgnoreCase("bar")) {
            System.out.println("Both match!");
        }
</pre>
</td><td>
<pre lang="java">
        if (foo.equalsIgnoreCase("foo") &&
                bar.equalsIgnoreCase("bar"))
        {
            System.out.println("Both match!");
        }
</pre>
</td></tr></tbody></table>

Now, contrast this to a common practice for annotations, for example. Why is this done:

<table>
<thead><tr valign="top">
  <th>'{' on the same line</th>
  <th>'{' on new line</th>
</tr></thead><tbody><tr valign="top"><td>
<pre lang="java">
    @Deprecated
    @Override
    public void foo() {
        System.out.println("Foo!");
    }
</pre>
</td><td>
<pre lang="java">
    @Deprecated
    @Override
    public void foo()
    {
        System.out.println("Foo!");
    }
</pre>
</td></tr></tbody></table>

... and not apply the same line wrapping as `@Deprecated` and `@Override` belong to the same method signature, to produce one of the following **ugly** possibilities:

<table>
<thead><tr valign="top">
  <th>'{' on the same line</th>
  <th>'{' on new line</th>
</tr></thead><tbody>
<tr valign="top"><td>
<pre lang="java">
    @Deprecated
            @Override
            public void foo() {
        System.out.println("Foo!");
    }
</pre>
</td><td>
  <pre lang="java">
    @Deprecated
            @Override
            public void foo()
    {
        System.out.println("Foo!");
    }
</pre>
</td></tr>
<tr valign="top"><td>
<pre lang="java">
    @Deprecated
            @Override
            public void foo() {
                System.out.println("Foo!");
            }
</pre>
</td><td>
  <pre lang="java">
    @Deprecated
            @Override
            public void foo()
            {
                System.out.println("Foo!");
            }
</pre>
</td></tr>
<tr valign="top"><td>
</td><td>
<pre lang="java">
    @Deprecated
            @Override
            public void foo()
            {
        System.out.println("Foo!");
    }
</pre>
</td></tr>
</tbody></table>


From these examples we can see that:

  - Both '{' placement standards seem to have accepted exceptions to line wrapping. Why? Because the alternative looks very, very bad.
  - Common use of '{' on new line fails to apply the line wrapping indentation that seems like it "should" be applied, but would yield triple indentation of code inside the block (2x for wrapping + 1x inside the block, last example above).


How does one format expressions with many parentheses? Or argument lists? Is the following readable?
<pre lang="java">
   public static &lt;F extends Foo&lt;F, B&gt;,
           B extends Bar&lt;F, B&gt;&gt;
           IdentityHashMap&lt;
           ConcurrentHashMap&lt;String, F
           &gt;, B&gt; foo(TreeMap&lt;F, Integer
           &gt; x, HashMap&lt;Integer, B&gt; y) {
       ...
    }
</pre>

How about the following?

<table>
<thead><tr valign="top">
  <th>'{' on the same line</th>
  <th>'{' on new line</th>
</tr></thead><tbody>
<tr valign="top"><td>
<pre lang="java">
    public static &lt;
        F extends Foo&lt;F, B&gt;,
        B extends Bar&lt;F, B&gt;
    &gt; IdentityHashMap&lt;
        ConcurrentHashMap&lt;
            String, 
            F
        &gt;,
        B
    &gt; foo(
        TreeMap&lt;F, Integer&gt; x,
        HashMap&lt;Integer, B&gt; y
    ) {
       ...
    }
</pre>
</td><td>
<pre lang="java">
    public static
    &lt;
        F extends Foo&lt;F, B&gt;,
        B extends Bar&lt;F, B&gt;
    &gt;
    IdentityHashMap
    &lt;
        ConcurrentHashMap
        &lt;
            String, 
            F
        &gt;,
        B
    &gt;
    foo(
        TreeMap&lt;F, Integer&gt; x,
        HashMap&lt;Integer, B&gt; y
    )
    {
       ...
    }
</pre>
</td></tr>
</tbody></table>

Now, here's the "kicker"... if we accepted that line wrapping needs no indentation of its own anywhere 
(and not just for annotations, for example) we'd actually cause multiple effects:

  1. Both styles of '{' positioning become equivalent, as '{' in new line is simply line wrap.
  2. We'd lose less horizontal space due to line wrapping, arguably when it is most critical and ugly.
  3. We'd have to find other means to apply visual separation from the next "line".
  
While (1) and (2) actually solve our problems and reduce quarrels, (3) shines a spotlight on a problem
frequently left unaddressed in coding standards. However, that problem has been solved for centuries 
but in a different context. It should be enough to say that we could stop calling these "lines" and try
to wrap them and instead think of them as "paragraphs" that may not fit in one line. There are two
common practices how paragraphs of normal text are visually separated:

  1. Indenting the first line (only). This would not only be too much of a departure for code as it is opposite from normal practices but also does not help with short "paragraphs" that do fit in single lines.
  2. Vertically spacing out paragraphs, possibly by leaving empty lines in between.
  
(2) can work for us and it can easily be applied and verified/enforced around any multiline "paragraph". We can also recognize that not only '{...}' braces imply indentation but also others, such as '[...]', '(...)' and '<...>'.
Each one of these defines its scope and "deserves" indentation.

We end up with following simple principal differences from (previously established) other standards:

  1. Line wrapping requires no indentation at all. Optionally, single (1x, not 2x) indentation may be requested for all lines after the first that do not begin or end blocks or scopes (various braces).
  2. Multiline paragraphs require blank either blank lines or lone block/scope start/end markers (optional if wrapping indentation requirement is enabled).
  3. Additional indentation is always allowed and/but is inherited by child blocks.
  4. All kinds of blocks/scopes ('{...}', '[...]', '(...)' and '<...>') require indentation when multiline.
  5. A single line can only end blocks started on the same line and at most one other line.
  6. Horizontal code alignment is welcome when it improves readability. Yes, as Google says, it has a "blast radius" when (future) changes are made but "so what?" if it improves readability and, more often than not, that blast radius is actually useful to detect historical changes related to the same code (and otherwise easily ignored). Occasional merge conflicts will be just that - occasional and the benefit of readability will easily outweigh this.

## Side-by-side comparison

Note that in "Humane" style the "quarrel" between two styles of placement of "{" is essentially non-existent as both are equally valid (new-line style is a line-wrapped end-of-line) unless explicitly forced either way.
<table>
<thead><tr valign="top">
  <th>Style</th>
  <th>'{' on the same line</th>
  <th>'{' on new line</th>
</tr></thead><tbody><tr valign="top">
<th>Legacy</th><td>
<pre lang="java">
@Deprecated
@Override
public void foo() {
    if (foo.equalsIgnoreCase("foo") &&
            bar.equalsIgnoreCase("bar")) {
        System.out.println("Match!");
    }
}
</pre>
</td><td>
<pre lang="java">
@Deprecated
@Override
public void foo()
{
    if (foo.equalsIgnoreCase("foo") &&
            bar.equalsIgnoreCase("bar"))
    {
        System.out.println("Match!");
    }
}
</pre>
</td>
</tr><tr valign="top"><th>Humane</th><td>
<pre lang="java">
@Deprecated
@Override
public void foo() {
    if (
        foo.equalsIgnoreCase("foo") &&
        bar.equalsIgnoreCase("bar")
    ) {
        System.out.println("Match!");
    }
}
</pre></td><td>
<pre lang="java">
@Deprecated
@Override
public void foo()
{
    if (
        foo.equalsIgnoreCase("foo") &&
        bar.equalsIgnoreCase("bar")
    )
    {
        System.out.println("Match!");
    }
}
</pre>
</td>
</tr></tbody></table>
