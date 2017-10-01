# GoLite Compiler <img src="https://user-images.githubusercontent.com/10332234/30514818-68895c98-9aea-11e7-8a6f-122bab4691e5.png" width="4.5%" height="4.5%">

Compiler for a subset of Golang. Translates Go programs to Java. 

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

To test the compiler, a series of Golang test programs are included. To run the compiler on these programs, execute the following:

```
./test.sh
```

## Authors 
- Ralph Bou Samra 
- Jonathan Lucuix-André 
- Yan Qing Zhang
