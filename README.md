FXyz
====

JavaFX 3D Visualization and Component Library

This is the original F(X)yz project, the JavaFX 3D library that provides additional primitives, composite objects, controls and data visualizations that the base JavaFX 8 3D packages do not have.
New development as of 2016 was moved to a dedicated project: (https://github.com/FXyz/FXyz), which is a gradle project and no longer uses Ant. Please adjust your projects accordingly.

Thanks for using FXyz in the before times!!
- Birdasaur


Building FXyz
====================

You will need [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
and [ANT](http://ant.apache.org/) installed to build the project. 

Add the included jars as local dependencies to the project:

 - JCSG.jar
 - poly2tri.jar

Running FXyz
===================

After you've built the project you can run this with a simple java command.

```bash
java -jar dist/FXyzLib.jar
```

You can select between the multiple tests available under org.fxyz.tests:

```bash
java -cp .;dist/FXyzLib.jar org.fxyz.tests.Text3DTest
```

Sampler
===================

For a visual application to run all the tests and their multiple options, you can 
try our [Sampler](https://github.com/FXyz/FXyz).

License
===================

The project is licensed under GPL 3. See [LICENSE](https://github.com/Birdasaur/FXyz/blob/master/LICENSE)
file for the full license.

