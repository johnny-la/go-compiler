main: clean sablecc compile
	
sablecc: golite.sablecc
	sablecc golite.sablecc
compile: 
	javac `find golite/ -name *.java`
pp:
	vim golite/PrettyPrinter.java
lexer:
	vim golite/GoliteLexer.java
weed:
	vim golite/Weeder.java
Main:
	vim golite/Main.java
edit:
	vim golite.sablecc
clean:
	rm -rf `find . -name *.class` golite/parser golite/lexer \
	golite/node golite/analysis *.pretty.min *.symbol.txt *.c
