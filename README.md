# BASIC Interpreter

AN interpreter for the BASIC language using Java. The compiler utilizes lexing, Abstract Syntax Tree (AST) parsing, and interpretation techniques.

## Summary

The BASIC compiler follows a multi-stage process to transform source code into executable code. The first stage is lexing, where the source code is divided into tokens. These tokens are then used to build an Abstract Syntax Tree (AST) during the parsing stage. The AST represents the structure of the program and is used for further analysis and interpretation. The final stage is the interpretation stage, which involves traversing the AST and executing the corresponding actions for each node. 

## Disclourse

This project uses a unique dialect of BASIC, the specific configurations and nuances of this dialect are found in the document attached in this repository called "BASIC Specification".

## Getting Started

To get started with the BASIC compiler, follow these steps:

1. Clone this repository to your local machine.
2. Open the project in your Java IDE.
3. Build the project to compile the source code.
4. Run the compiler with the path to your BASIC source code file as an argument.
5. The compiler will interpret and execute the BASIC program.

For more detailed instructions and examples, please refer to the documentation provided in the repository.


## License

This project is licensed under the MIT License. For more information, please refer to the LICENSE file.
