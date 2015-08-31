# jar-patch-maker

Tool for computing differences between two sets of jar files and creating a patch jar
file containing only the differences.

Example directory old/ contains jar files from release 1 of your application, and directory
new/ contains jar files from release 2 of your application.

build with
> sbt universal:packageZipTarball

The cd to the location of your choice and
> tar -xzvf jarpatch source directory/target/universal/jar-patch-*.tgz

Then

>jar-patch-1.0/bin/jar-patch -o old/*.jar -n new/*.jar -p patch.jar

The file patch.jar will contain only class and resource files that changed.



  
