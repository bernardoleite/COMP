# Compilers

### PROJECT TITLE: "Compiler of the yal0.4 language to Java Bytecodes" ###

### GROUP: 26 ###

* NAME1: Ângelo Moura, NR1: 201303828, GRADE1: 17, CONTRIBUTION1: 25% 
* NAME2: Bernardo Leite, NR2: 201404464, GRADE2: 17, CONTRIBUTION2: 25%
* NAME3: Francisco Silva, NR3: 201502860, GRADE3: 17, CONTRIBUTION3: 25%
* NAME4: Luís Miguel Saraiva, NR4: 201404302, GRADE4: 17, CONTRIBUTION4: 25%

### SUMMARY: ###

* Compilers Final Project. Compiler language to Java Bytecodes.

### EXECUTE: ###

--> Using .jar

* Go to Folder "trabalho";
* Execute command "java -jar Project.jar testsuite/MyFirstYalExamples/NameOfFile.yal"
or
"java -jar Project.jar examples/NameOfFile.yal" (relative to the 5 examples selected)
* Open folder "output" to see generated code.

--> Using Makefile

* Go to Folder "trabalho";
* Execute command "make";
* Execute command "java YAL2JVM testsuite/MyFirstYalExamples/NameOfFile.yal" or
"java YAL2JVM examples/NameOfFile.yal" (relative to the 5 examples selected).
* Open folder "output" to see generated code.

### DEALING WITH SYNTACTIC ERRORS: ###

* The program is capable of detecting up to 10 errors and identifies the exact location.

### SEMANTIC ANALYSIS: ###

* A scalar can not be an array;
* It is not possible to compare an array with a scalar;
* The variable that holds the return of a function must be of the same type as the return;
* When calling a function that returns a value, the variable that stores its value must be of the expected type.

### INTERMEDIATE REPRESENTATIONS (IRs): ###

* Stack-Based.

### CODE GENERATION: ###

* Code generation is made by storing each line of Java Bytecodes language on a data strucute and then written to a file.j .

### OVERVIEW: ###

* The approach was made incrementally. As the work progressed we added the code to generate on the stack representation.

### TASK DISTRIBUTION: ###

#### Ângelo Moura: ####

- Develop parser for yal taking as starting point yal grammar furnished;
- Solve conflicts in grammar;
- Error treatment and recovery mechanisms;
- Specification on the file jjt to generate, syntax tree;
- Generate JVM code accpeted by jasmin - functions;
- Generate JVM code accpeted by jasmin - arithmetic expressions;
- Generate JVM code accpeted by jasmin - conditional instructions;
- Generate JVM code accpeted by jasmin - deal with arrays;

#### Bernardo Leite: ####

- Develop parser for yal taking as starting point yal grammar furnished;
- Solve conflicts in grammar;
- Error treatment and recovery mechanisms;
- Specification on the file jjt to generate, syntax tree;
- Generate JVM code accpeted by jasmin - functions;
- Generate JVM code accpeted by jasmin - arithmetic expressions;
- Generate JVM code accpeted by jasmin - loops;
- Generate JVM code accpeted by jasmin - deal with arrays;

#### Francisco Silva: ####

- Develop parser for yal taking as starting point yal grammar furnished;
- Solve conflicts in grammar;
- Error treatment and recovery mechanisms;
- Specification on the file jjt to generate, syntax tree;
- Generate JVM code accpeted by jasmin - functions;
- Generate JVM code accpeted by jasmin - arithmetic expressions;
- Generate JVM code accpeted by jasmin - conditional instructions;
- Generate JVM code accpeted by jasmin - deal with arrays;

#### Luís Miguel Saraiva: ####

- Develop parser for yal taking as starting point yal grammar furnished;
- Solve conflicts in grammar;
- Error treatment and recovery mechanisms;
- Specification on the file jjt to generate, syntax tree;
- Include the necessary symbol tables;
- Semantic Analysis and error treatment;
- Generate JVM code accpeted by jasmin - loops;
- Generate JVM code accpeted by jasmin - deal with arrays;

### PROS: ###

* Good error control;
* Any possible detail in the semantics it is easy to correct and adapt;
* We have an Intermediate Representation of easy interpretation;
* We have an intermediate Representation of easy adjustment and addition of new cases.

### CONS: ###

* Lack fo Examples to test Semantic.
