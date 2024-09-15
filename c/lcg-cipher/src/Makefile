# Compiler
CC = gcc

# Compiler flags
CFLAGS = -Wall -ansi -pedantic

# Source files
SRC = cipher.c lcg.c

# Object files
OBJ = cipher.o lcg.o

# Executable name
EXEC = cipher

# Default rule to compile the program
all: $(EXEC)

# Linking the object files to create the executable
$(EXEC): $(OBJ)
	$(CC) $(OBJ) -o $(EXEC)

# Rule for creating object files from .c source files
%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

# Clean up generated files
clean:
	rm -f $(OBJ) $(EXEC)