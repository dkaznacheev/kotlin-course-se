grammar Exp;

file: body = block EOF;

block: (statement)*;

statement: function | variable | expression | t_while | t_if | assignment | t_return;

function: 'fun' name = identifier '(' params = parameter_names ')' body = '{' block '}';

variable: 'var' name = identifier ('=' value = expression)?;

parameter_names: ((identifier ',')* identifier)?;

t_while: 'while' '(' condition = expression ')' while_block = '{' block '}';

t_if: 'if' '(' condition = expression ')' if_block = '{' block '}' ('else' else_block = '{' block '}')?;

assignment: name = identifier '=' value = expression;

t_return: 'return' value = expression;

expression:
      function_call
    | <assoc = left> left = expression op = (MUL | DIV | MOD) right = expression
    | <assoc = left> left = expression op = (ADD | SUB) right = expression
    | left = expression op = (LE | LT | GE | GT) right = expression
    | left = expression op = (EQ | NE) right = expression
    | left = expression op = AND right = expression
    | left = expression op = OR right = expression
    | identifier
    | literal
    | '(' expression ')';

function_call: name = identifier '(' args = arguments ')';

arguments: ((expression ',')* expression)?;

identifier: ID_REGEX;

literal: LIT_REGEX;

LIT_REGEX: '0' | [1-9][0-9]*;

ID_REGEX: [a-zA-Z][a-zA-Z0-9_]*;

MUL: '*';
DIV: '/';
MOD: '%';
ADD: '+';
SUB: '-';
LE: '<=';
LT: '<';
GE: '>=';
GT: '>';
EQ: '==';
NE: '!=';
AND: '&&';
OR: '||';

TO_SKIP: (' ' | '\t' | '\r' | '\n' | '//' (.)*? '\n') -> skip;