 ===================
  CGVis Version 0.1
 ===================

 1. SYSTEM REQUIREMENTS

 To run CGVis you need Java Runtime Environment (J2RE or J2SE) Version 5.0
 or higher.

 The latest version for Windows, Linux and Solaris can be downloaded from
 http://www.java.com/getjava/

 Mac OS X versions are available at
 http://www.apple.com/macosx/features/java/

 2. BUILDING CGVis

 To build CGVis you need Apache Ant which is freely available at
 http://ant.apache.org

 Simply run Ant in the CGVis root directory and it will compile the project
 and write the binaries to ${CGVIS_ROOT}/build.

 3. RUNNING CGVis

 3.1 Running on Windows

 To launch CGVis on Windows use the batch file CGVis.bat. There is another
 batch file called CGVis-opengl.bat that turns on the Sun's Java OpenGL
 pipeline acceleration. Note using the OpenGL pipeline acceleration is
 experimental and can sometimes cause hanging.
  
 3.2 Running on Mac OS X

 To run CGVis start Finder, open the CGVis directory and then double click
 on CGVis.jar. It should work if Java is configured properly on your
 computer. Otherwise you can try the generic UNIX running method described
 in the next section. 

 3.3 Running on UNIX

 To start CGVis open the terminal, change the directory to where CGVis was
 installed to and then run the ./CGVis.sh from the terminal.

 4. Troubleshooting
 
 If CGVis fails to run ensure that the proper version of the Java Runtime
 executable is in your PATH environment variable. To do that you type 

 java -version

 in the console. It should then print the version of the JRE which is set
 in your system PATH. The version must be 1.5.0 or higher, if an older one
 is reported, modify your PATH variable so that it contains path to the
 newer Java version.


 --------------------------------------------------------------------------
 Ilya Boyandin <Ilya.Boyandin@gmail.com> <http://www.boyandi.net>
 Erik Koerner

 Department of Information Design
 FH JOANNEUM - Graz University of Applied Sciences

 Institute for Genomics and Bioinformatics
 Graz University of Technology

 March 2006
