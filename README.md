# About
-----
This java library can be used to dynamically load and run a jar into an already running jvm. It uses a java agent to gain access to a target program
and then adds your jar to the program's class loader, loads your main class and invokes your main method on a new Thread. This makes it much more versatile
than using a plain java agent - you have much more control over how you want your program to execute and, most importantly, what class loader it should run on.
A java agent itself will be loaded on the app class loader, which is rarely the one used by the target program. You also do not have to code an agent for each and
every of your debug tools etc. - any jar can be loaded! You also save the hassle of writing your own agent loader! Overall, this program is a better 
alternative to simply using a java agent because it enhances and improves on its functionality.

This tool exists simply as a proof of concept. I do not endorse or condone any malicious activity, cheating etc. Use this software responsibly.
Note that this tool will only work when the target vm allows dynamic agent attaching. This option is enabled by default.

# How to use
-----
To use the library's functionality, pass a ```VirtualMachineDescriptor``` and a ```Configuration``` object to the
```Injector.run()``` method. To get a list of all available VirtualMachineDescriptors use ```VirtualMachine.list()```.

The ```Configuration``` class uses a builder pattern to set all its attributes. Not all attributes have to be set depending
on the value of other attributes. However, an attribute being not set or invalidly set when it is required will lead to crashes.
A list of attributes, what they do and when they are required is as follows:
* jarPath: The path to the jar to be loaded. Should be a fully qualified path. Must always be set.
* classLoaderPolicy: The policy to determine what class loader to use to load your classes. Is either ```RECOMMENDED``` or ```CUSTOM```. Must always be set.
* classLoaderClass: The class (in the target program) whose class loader should be used to load your classes. Must only be set when ```classLoaderPolicy``` is equal to ```CUSTOM```.
* mainClassPolicy: The policy to determine what class should be used as the main class. Is either ```MANIFEST```, ```CUSTOM``` or ```ANNOTATED```. Must always bet set.
* mainClass: The name of the class to use as the main class. Must only be set if ```mainClassPolicy``` is equal to ```CUSTOM```.
* mainClassAnnotation: The fully qualified class name of the annotation class that should be used to identify your main class. Must only be set if ```mainClassPolicy``` is equal to ```ANNOATED```.
* mainMethodPolicy: The policy that should be used to select an entry point method from your main class. Is either ```CUSTOM```, ```MAIN```, or ```ANNOTATED```. Must always be set.
* mainMethod: The method name of the method that should be used as an entry point. Must only be set if ```mainMethodPolicy``` is equal to ```CUSTOM```.
* mainMethodAnnotation: The fully qualified class name of the annotation class that be should to identify the main method within your main class. Must only be set if ```mainMethodPolicy``` is equals to ```ANNOTATED```.

A more thorough explanation of what different values for different policies do is as follows:
* ClassLoaderPolicy:
  * ```RECOMMENDED```: Will use the system class loader.
  * ```CUSTOM```: Will use a specified class' class loader. The classLoaderClass attribute is used to find that class. It cannot be a member of the jar that is being loaded.
* MainClassPolicy:
  * ```MANIFEST```: Will use the class found in the jar's manifest. The manifest attribute should be 'Main-Class'. Note that this will error if the jar to be loaded does not have a manifest
    or the manifest does not have the main class attribute.
  * ```CUSTOM```: Will use the class specified by the value of mainClass.
  * ```ANNOTATED```: Will use the first class that is found to have the annotation specified by the value of mainClassAnnotation.
* MainMethodPolicy:
  * ```CUSTOM```: Will use the method with the name specified by the value of mainMethod. If the method is overloaded, the one with the least parameters will be used.
  * ```MAIN```: Will use a method with the name 'main' that takes a String[] as its only parameter.
  * ```ANNOTATED```: Will use the first method that is found to have the annotation specified by the value of mainMethodAnnotation.

Note that the jar containing a program that has injected a jar into a jvm cannot be deleted until all target programs have terminated.
The injected jar can also not be deleted until the tartget program exits.

# Building
-----
This project uses gradle. Clone the repository and build the gradle model. The task ```build``` will build a jar containing only this tool. The task ```libBuild``` will build a jar containing
this tool as well as a copy of tools.jar. Note that the java version you use to build this tool should match the java version that your target program runs on! The project purposefully renounces
all fancy new java features to make sure it can be built and used with any version of java.

# Exception handling
-----
Because of the hacky nature of this tool, a lot of different exceptions can be thrown both in the loader and in the target program. You can pass your own ```ExceptionHandler``` implementation 
to the ```Injector.rum``` method. If something goes wrong within the target program, those exceptions will be thrown within the target program - and probably cause it to crash, because it was not
developed with the option of a jar being dynamically loaded in mind. This goes for exceptions raised by the injection tool as well as exceptions that can occurr in the injected code. Therefore, it can
be tricky to debug why injection does not work.
When an error in the target program occurs, an AgentInitializationException will be raised in the injector program. This can contain an error message that can at least give you a hint at what went wrong.
