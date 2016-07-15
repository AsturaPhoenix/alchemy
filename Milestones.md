#Alchemy as a Platform for Idiomatic Transpilation of Programming Languages and Cross-Platform Programming

##User Stories
###V23 JNI 
[Vanadium](https://vanadium.github.io) is a P2P RPC library written in Go but with applications in Android, principally Java (and iOS, principally Swift)). Interop is achieved through [cgo](https://golang.org/cmd/cgo/) and a JNI wrapper.

##Relationship to Prior Art
###[DMS Software Reengineering Toolkit](https://en.wikipedia.org/wiki/DMS_Software_Reengineering_Toolkit)
DMS is a toolchain generator to assist with management and porting of large legacy codebases. We want to expose the same functionality as an integrated environment for first-class development rather than porting or post-coding analysis.

As DMS focuses on generating tools to accomplish specific interop tasks, it does not lend itself to operating simultaneously on multiple languages, and I don't think it exposes language extension capability (though it does include DSL tools).

DMS analysis tools include a lot of features that we might want to add to any tooling environment.
* Flow analysis
* Range analysis
* Points-to analysis
* Smart diffs (Levenstein diff between ASTs)
* Syntactic clone detection

See also: [Google Tech Talk](https://www.youtube.com/watch?v=C-_dw9iEzhA)  
See also: [Semantic Designs website](http://www.semanticdesigns.com/index.html)

###[Use The Source](http://usethesource.io/)
Use The Source is a set of projects with very similar goals to Alchemy. Alchemy intends to go a bit further by introducing more powerful tooling, more powerful language extensibility, and departing further from textual source code, but perhaps Use The Source components can be reused for Alchemy.

###[PetitParser](http://scg.unibe.ch/research/helvetia/petitparser)
PetitParser is a dynamic parser that could potentially implement the parser for Substrate. It is a little less powerful than might be desired.
