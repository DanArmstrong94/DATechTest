# DATechTest

To run this application you will need the following software:

 - Java
 - Maven
 - Git
 
 The steps below will detail the production of the .jar file, and allow you to run test files from the SingleTest/ folder.
 
 1. Clone the repository using "git clone"
 2. Open the folder in the command promt and perform "mvn package" - this will produce a .jar file in the current working directory.
    2a. You should notice that all the JUnit tests pass here.
 3. Running the .jar file will run all .txt files in the SingleTest folder (and reject non-txt files) providing some word count analytics in a clear and readable format in-line,
    or you can run the jar by double-clicking and see the output for each analysed file in separated txt files in the base directory
