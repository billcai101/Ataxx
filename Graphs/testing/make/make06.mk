foo.y: foo.c foo.h
        gcc -g -c foo.o foo.c

foo.c: foo.o
	yacc -o foo.c foo.y

foo.o: foo.z
    yacc -o foo.z foo.z




