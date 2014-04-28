CS 571
TCP assignment
Han Chen
4/25/2014

**************************************

The structure of database:

//by entering ".schema" in sqlite3 terminal:

CREATE TABLE chatlog(author text, content text, date text);
CREATE TABLE user(username text, password text);
CREATE TABLE subscribe(reader text, author text);

***************************************************
The message format:

OperationCmd+"|"+parameter+"|"+paramerter+"|"+........

Example:

psOutput.println(MsgCodes.INSERT+"|"+username+"|"+password);