# StableCCGoLite
Compiler built in SableCC for a subset of the Go language

### Ralph
- Scanner
- Expressions

### Martin 260520930
#### Declarations:
- Package declaration
- Top-level declarations
- Variable declaration
- Type declaration
- Function declaration

### Jonathan
- Comments
- Statements

*Notes:* Our scanner and parser could not enforce that a break and continue statement 
could only appear within a loop. This constraint will be handled using a weeding pass 
in the next milestone.

Design Decisions:

Choice of Tool:
Prior to starting the first milestone, each team member was assigned to explore a set of tools that might be suitable to write the compiler in. Ralph looked at flex/bison in C, Jonathan looked at SableCC and Java and Martin looked at menhir and OCaml. After several meetings, it was decided that SableCC would be the most ideal tool for the group. Reasons given were: 
1) Everyone on the team was very comfortable in Java
2) CST generation was very simple
3) It would be useful to understand the tool as it would appear later on in the midterm/final

Declaration Design Decisions:
Declarations were interesting in that there were 4 clear high level declarations: variable, type, function, and package. However as variable and type functions could be nested within the function declarations, it was decided that a two tier system was necessary. Variable and type functions were grouped as declarations and subcategoried under top level declarations, which included the superclass declarations as well as func_decl and package_decl. Another thing to note was that the base types did not actually represent reserved keywords, therefore they could be used as general identifiers. A production was created then to encapsulate these two types of identifiers (base types and general identifiers). Likewise, due to the fact that types could be structs, slices, arrays, or a struct/base type, it was necessary to create a general type production that encapsulated a type that could contain multiple combinations of these types (i.e [5][] name_of_struct). 
