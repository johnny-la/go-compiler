# GoLite Compiler

<img src="https://user-images.githubusercontent.com/10332234/31352779-0bed0b44-acfe-11e7-83b4-a2cddcf54e2c.png" width="20%" height="20%" hspace="15">

Compiler built for a subset of the Go programming language. Translates Go programs to Java. 

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
# See "Important Notes" section below for a list of supported language features
./run.sh /path/to/<b>&lt;program-name&gt;</b>.go
</pre>

A corresponding Java program will be generated here:

<pre>
/path/to/GoLite<b>&lt;program-name&gt;</b>.java
</pre>

## Testing the compiler 

To test the compiler, a series of Golang test programs are included in the **programs/** directory. To run the test suite, execute the following command:

```
./test.sh
```

## Important Notes

<img src="https://user-images.githubusercontent.com/10332234/31354385-3d07f2ce-ad04-11e7-902d-ed1534c4a684.png" width="15%" hspace="15"/>

- This compiler only supports a subset of Golang's entire feature set. The list of supported features are detailed in the following documents:
  - [Syntax Specifications](http://www.cs.mcgill.ca/~cs520/2017/assignments/m1_syntax.pdf)
  - [Typechecking Specifications](http://www.cs.mcgill.ca/~cs520/2017/assignments/m2_typechecker.pdf)
- To compile a custom Go program, the program must end with an extra newline for parsing purposes

## Authors 
- Jonathan Lucuix-André 
- Ralph Bou Samra 
- Yan Qing Zhang
