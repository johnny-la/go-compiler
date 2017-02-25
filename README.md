# StableCCGoLite
Compiler built in SableCC for a subset of the Go language

### Jonathan
- Comments
- Statements

*Notes:* Our scanner and parser could not enforce that a break and continue statement 
could only appear within a loop. This constraint will be handled using a weeding pass 
in the next milestone.

### Ralph
- Scanner
- Expressions

### Martin
#### Declarations:
- Package declaration
- Top-level declarations
- Variable declaration
- Type declaration
- Function declaration
