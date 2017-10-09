# GoLite Compiler

![pirate gopher](https://user-images.githubusercontent.com/10332234/31352779-0bed0b44-acfe-11e7-83b4-a2cddcf54e2c.png)

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

Then, compile a Golang program as follows:

<pre>
./run.sh /path/to/<b>&lt;program-name&gt;</b>.go
</pre>

A corresponding Java program will be generated here:

<pre>
/path/to/GoLite<b>&lt;program-name&gt;</b>.java
</pre>

## Testing the compiler 

To test the compiler, a series of Golang test programs are included. To run the test suite, execute the following command:

```
./test.sh
```

## Important Notes
- This compiler only supports a subset of Golang's entire feature set. The list of supported features are detailed in the following documents:
  - [Syntax Specifications](http://www.cs.mcgill.ca/~cs520/2017/assignments/m1_syntax.pdf)
  - [Typechecking Specifications](http://www.cs.mcgill.ca/~cs520/2017/assignments/m2_typechecker.pdf)
- To compile a custom Go program, make sure that the program ends with an extra newline 

## Authors 
- Jonathan Lucuix-André 
- Ralph Bou Samra 
- Yan Qing Zhang
