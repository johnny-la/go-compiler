# StableCCGoLite
Compiler built in SableCC for a subset of the Go language


Below is a list of each team member's contribution to the project:

### Ralph
- Scanner
- Expressions

### Martin 260520930
#### Declarations:
- Package declaration
- Variable declaration
- Type declaration
- Function declaration

### Jonathan
- Comments
- Statements

Design Decisions:

Choice of Tool:

Prior to starting the first milestone, each team member was assigned to explore a set of tools that might be suitable to write the compiler in. Ralph looked at flex/bison in C, Jonathan looked at SableCC and Java and Martin looked at menhir and OCaml. After several meetings, it was decided that SableCC would be the most ideal tool for the group. Reasons given were: 
1) Everyone on the team was very comfortable in Java
2) CST generation was very simple
3) It would be useful to understand the tool as it would appear later on in the midterm/final

Declaration Design Decisions:

Declarations were interesting in that there were 4 clear high level declarations: variable, type, function, and package. However as variable and type functions could be nested within the function declarations, it was decided that a two tier system was necessary. Variable and type functions were grouped by a production called declarations and subcategoried under top_level_declarations, which included the grouped declarations as well as func_decl and package_decl. Another thing to note was that the base types did not actually represent reserved keywords, therefore they could be used as general identifiers. A production was created then to encapsulate these two types of identifiers (base types and general identifiers). Likewise, due to the fact that types could be structs, slices, arrays, or a struct/base type, it was necessary to create a general type production that encapsulated a type that could contain multiple combinations of these types (i.e [5][] name_of_struct). 

Weeding:

We decided that ultimately it would be too difficult to implement the weeding conditions through the grammar rules and decided to implement a java class instead to do the weeding. The reason is it is too difficult simply due to the way we have encapsulated the break/continue/default keywords to fit within the general statement production. To write specific cases for each of these keywords would add too much overhead and it was ultimately decided to be easier to simply do another pass to catch errors surrounding those keywords. The class Weeder.java reads through the generates AST and ensures that any break or continue statements found outside the context of a loop (and switch for break) throw an exception. It also catches when multiple default statements are found within the context of a switch statement. 