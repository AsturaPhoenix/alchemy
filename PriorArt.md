# Relationship to Prior Art
## [DMS Software Reengineering Toolkit](https://en.wikipedia.org/wiki/DMS_Software_Reengineering_Toolkit)
DMS is a toolchain generator to assist with management and porting of large legacy codebases. We want to expose the same functionality as an integrated environment for first-class development rather than porting or post-coding analysis.

As DMS focuses on generating tools to accomplish specific interop tasks, it does not lend itself to operating simultaneously on multiple languages, and I don't think it exposes language extension capability (though it does include DSL tools).

DMS analysis tools include a lot of features that we might want to add to any tooling environment.
* Flow analysis
* Range analysis
* Points-to analysis
* Smart diffs (Levenstein diff between ASTs)
* Syntactic clone detection

**See also:** [Google Tech Talk](https://www.youtube.com/watch?v=C-_dw9iEzhA)  
**See also:** [Semantic Designs website](http://www.semanticdesigns.com/index.html)

## [PetitParser](http://scg.unibe.ch/research/helvetia/petitparser)
PetitParser is a dynamic parser that could potentially implement the parser for Substrate. However, out of the box it cannot handle context-sensitive grammars or arbitrary rewrite rules. Context-sensitive grammars have been handled in PetitParser by modifying its input stream type (its equivalent of a lexer). Arbitrary rewrite rules would need more work, and might degenerate to the point where using PetitParser no longer offers significant advantages.

## [Use The Source](http://usethesource.io/)
Use The Source is a set of projects with very similar goals to Alchemy. Alchemy intends to go a bit further by introducing more powerful tooling, more powerful language extensibility, and departing further from textual source code, but perhaps Use The Source components can be reused for Alchemy.
