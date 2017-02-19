compile: clean sablecc
	javac `find golite/ -name *.java`
sablecc: golite.sablecc
	sablecc golite.sablecc
clean:
	rm -rf `find . -name *.class` golite/parser golite/lexer \
	golite/node golite/analysis *.pretty.min *.symbol.txt *.c
