foo: foo.o
    gcc -o foo foo.o

foo.o: foo.y
    gcc -o foo.o foo.y

foo.y: foo.c
    gcc -o foo.y foo.c

foo.c: foo.h
    gcc -o foo.c foo.h

foo.h: foo.d
    gcc -o foo.h foo.d
