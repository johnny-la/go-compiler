# GoLite Compiler
Compiler built in SableCC which translates Golang to Java 

## Authors 
- Ralph Bou Samra 
- Jonathan Lucuix-André 
- Yan Qing Zhang 260520930

# To build:

`./build.sh`

# To run dumpsymtab:

`java golite.Main -dumpsymtab <filename>`

# To run pptype:

`java golite.Main -pptype <filename>`

## Important note:

Due to time constraints, this submission does not support array indexing and struct selecting. However, we have implemented the type checking for those despite the grammar not fully supporting their corresponding rules.
