compile: clean sablecc
	javac `find minilang/ -name *.java`
sablecc: minilang.sablecc
	sablecc minilang.sablecc
clean:
	rm -rf `find . -name *.class` minilang/parser minilang/lexer \
	minilang/node minilang/analysis *.pretty.min *.symbol.txt *.c
