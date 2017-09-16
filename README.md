# GoLite Compiler
Compiler for a subset of Golang, built using SableCC. 

Translates Golang to Java. 

## Authors 
- Ralph Bou Samra 
- Jonathan Lucuix-André 
- Yan Qing Zhang

## Requirements
- **SableCC 3** 
  - [Download](http://www.sablecc.org/)
  - [Setup Tutorial](http://www.cs.mcgill.ca/~cs520/2009/howtosablecc.html) 
- **Java 8 or higher**

## How to Build and Run:

To build the compiler, `cd` to the cloned repository and run:

```
./build.sh
```

Then, run the compiler on a Golang program:

```
./run.sh /path/to/program.go
```

## Testing the compiler 

To test the compiler, the repository includes a series of Golang test programs. To run the compiler on these programs, execute the following:

```
./test.sh
```
