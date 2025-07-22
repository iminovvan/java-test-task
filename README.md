# java-test-task
This is a command-line Java utility that reads one or more `.txt` files, classifies each line as an integer, float, or string, and writes them into separate output files.
It can also print short/full statistics.
## Instructions to run the program 
Tools: 
* Java 21
* Apache Maven 3.9.11

1) Create .jar file 'target/java-test-task-1.0-SNAPSHOT.jar' by running:
  mvn clean package

2) Run the program using the command similar to the example below.
   Make sure the input files are created in advance and located in the program file.
   java -jar target/java-test-task-1.0-SNAPSHOT.jar -s -f -a -p sample- -o output in1.txt in2.txt

 ## Command-line options
`-s` - print short statistics (number of elements of each data type)
`-f` - print full statistics (min, max, sum, avg, string lengths)
`-a` - append to output files instead of overwriting
`-p <prefix>` - optional prefix for output file names
`-o <directory>` - optional directory for output files
