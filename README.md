# GoLite Compiler
Compiler which translates Golang to Java, built using SableCC. 

## Authors 
- Ralph Bou Samra 
- Jonathan Lucuix-André 
- Yan Qing Zhang

## Requirements
- **Java 8 or higher**
- **SableCC 3** [(Download)](http://www.sablecc.org/)
  - [Setup Tutorial](http://www.cs.mcgill.ca/~cs520/2009/howtosablecc.html) 

## How to Biuld and Run:

`./build.sh`

## To run dumpsymtab:

`java golite.Main -dumpsymtab <filename>`

## To run pptype:

`java golite.Main -pptype <filename>`

## Important note:

Due to time constraints, this submission does not support array indexing and struct selecting. However, we have implemented the type checking for those despite the grammar not fully supporting their corresponding rules.
