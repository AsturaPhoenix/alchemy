# Alchemy Milestones

Also known as philosophers' stones.

This document outlines a high-level plan for leveraging alchemy as a platform for idiomatic transpilation of programming languages and cross-platform programming. Along the way, the Alchemy project will produce incremental milestones that addresses various problems pertinent to other problem spaces.

## User stories

See also: [internal stories](https://goto.google.com/alchemy-stories) (limited access)

### V23 JNI
[Vanadium](https://vanadium.github.io) is a P2P RPC library written in Go but with applications in Android, principally Java (and iOS, principally Swift)). Interop is achieved through [cgo](https://golang.org/cmd/cgo/) and a JNI wrapper.

## Feature milestones
### Substrate parser
### Mechanical transpilation
### Language specification by example

Languages are classically specified by defining BNF grammars which can then be fed to parser generators. While this is often a straightforward process, it can be tedious to specify the entire grammar.

Alchemy will introduce a tool (stand-alone and as an IDE plug-in) that, given a text input of an example usage of a language (generalizable to any input format) and a basis language similar to the language being used, can allow a developer to highlight pertinent sections of the example input and create/modify the corresponding parse rules to fully specify the language.
