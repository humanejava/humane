# Humane Code Style

[![Maven Central](https://img.shields.io/maven-central/v/com.offbeatmind.humane/humane.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.offbeatmind.humanejava%22%20AND%20a%3A%humanejava-core%22)
[![Build Status](https://travis-ci.org/humanejava/humane.svg?branch=master)](https://travis-ci.org/humanejava/humane)
[![Coverage Status](https://coveralls.io/repos/humanejava/humanejava/badge.svg?branch=master&service=github)](https://coveralls.io/github/humanejava/humanejava?branch=master)
[![Join the chat at Gitter](https://badges.gitter.im/humane-code/community.svg)](https://gitter.im/humane-code/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![License LGPL-3/Apache-2.0](https://img.shields.io/badge/license-LGPL--3%2FApache--2.0-blue.svg)](LICENSE)

This project brings rational and humane coding style (formatting) aid and enforcement tools, 
starting with Java and, with welcome help of others, other languages.

## Background and Rationale

A number of coding style standards and tools already exist, for quite a long time.
Yet, even though the adoption is, in fact, significant, in most situations it brings
an unhealthy dose of dissatisfaction or isn't adopted at all. There are numerous situations
where existing tools to either too far or too little to be of common use, such as:

  - the tools blindly follow a single recipe and forget that code style is for humans, not machines
  - the available style checking tools cannot match the freedom/flexibility of the accepted standard
  - the formatting tools ruin existing perfectly valid code as they force a single format
  - there is no distinction between checking and formatting tools at all and the format is valid if reformatting brings no changes
  - (some) existing guides fail to address frequent situations well such as long "line" wrapping or intentional extra indentation of code for reasons other than Java (e.g. embedded SQL statements).
  - etc.
  
This project aims to solve that by accepting the following:

  1. Code formatting is important for humans, not machines.
  2. Humans have better understanding of what the code represents beyond what it does.
  3. Humans can make better decisions as to what is better for other humans (when properly guided).
  4. A style guide must leave some freedom for humans to format the code best for humans.
  5. Code style checker should keep that freedom.
  6. An automatic formatter should only change the parts of the code that do not match the style guide and can, at that time, apply any single possible format. Humans can then improve upon it.
   
... and by revisiting some hard cases where existing formats seem to give up.

## Details

For details see the language-specific README:

 - [Java](lang/java/README.md)  

## License

Humane Java is available either under the terms of the LGPL License or the Apache License. You as the user are entitled to choose the terms under which adopt Humane Java.

For details about the LGPL License please refer to [LICENSE.LGPL](https://github.com/humanejava/humanejava/blob/master/LICENSE.LGPL).

For details about the Apache License please refer to [LICENSE.APACHE](https://github.com/humanejava/humanejava/blob/master/LICENSE.APACHE).