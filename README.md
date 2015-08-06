# jar-patch-maker

Tool for computing differences between two sets of jar files and creating a patch jar
file containing only the differences.

Example directory old/ contains jar files from release 1 of your application, and directory
new/ contains jar files from release 2 of your application.

tar -xzvf ../jarpatch/target/universal/jar-patch-1.0.tgz

jar-patch-1.0/bin/jar-patch -o old/*.jar -n new/*.jar -p patch.jar


  
