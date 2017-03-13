# StableCCGoLite - Milestone 2
Compiler built in SableCC for a subset of the Go language


Authors: Ralph Bou Samra 260503614, Jonathan Lucuix-André 260632816, Yan Qing Zhang 260520930

# To build:

`./build.sh`

# To run dumpsymtab:

`java golite.Main -dumpsymtab <filename>`

# To run pptype:

`java golite.Main -pptype <filename>`

## Important note:

Due to time constraints, this submission does not support array indexing and struct selecting. However, we have implemented the type checking for those despite the grammar not fully supporting their corresponding rules.
