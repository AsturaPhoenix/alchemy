# Alchemy
Alchemy encompasses a fully extensible programming language and development tools

##Motivations
* A programming language should be a tool rather than a limitation. Programmers should be able to write in whatever language is most expressive to their purpose rather than only the languages for which compatible libraries for their needs and compatible compilers for their target platforms exist.
* Different developers have different preferences and styles, which are often mathematically equivalent but can manifest in completely different method signatures and library designs. Adaptation of mathematically equivalent interfaces can and should be an automated process that does not incur a runtime penalty.
* Likewise, collaborating developers should be able to view and contribute to code with whatever language and conventions they are most familiar with; view and use of custom syntax extensions must be optional.
* Cross-platform development should require code modifications only to tailor the experience to the capabilities of each platform.
* Interop often requires interface binding libraries, which are today sometimes generated manually and almost never in a scalable, general way that applies previous binding translations. The process is not always straightforward but certainly feels mechanical, and keeping them in sync with backing libraries is challenging. At the very least, we should be able to automate the simple parts and track/stub the parts that are not straightforward.

##Project organization
Initial plan for project organization, with working names (suggestions welcome):

* **Alchemy:** umbrella project, to include a web and native IDE that supports semantic code views (rather than purely textual) and a package management system (to be compatible with existing package management systems in various languages).
* **PStone:** component libraries and CLI tools for transpilation driven by syntax extension and library adaptation.
* **[Substrate](Substrate.md):** extensible-syntax programming language and processing libraries. I'll start by modeling base capability natively and scripting ease-of-use features on top of those.

Initial implementation is in Java 8 (Eclipse IDE). Goal is to get the project to a point where it can port itself as desired.
