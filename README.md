# About
-----
This java library can be used to dynamically load and run a jar into an already running jvm.

# How to use
-----
To use the library's functionality, pass a ```VirtualMachineDescriptor``` and a ```Configuration``` object to the
```Injector.run()``` method. To get a list of all available VirtualMachineDescriptors use ```VirtualMachine.list()```.

The ```Configuration``` class uses a builder pattern to set all its attributes. Not all attributes have to be set depending
on the value of other attributes. However, if an attribute is not set or invalidly set when it is required will lead to crashes.
A list of attributes, what they do and when they are required is as follows:
* jarPath: The path to the jar to be loaded. Should be a fully qualified path. Must always be set.
* classLoaderPolicy: The policy to determine what class loader to use to load your classes. Is either ```RECOMMENDED``` or ```CUSTOM```. Must always be set.
* classLoaderClass: The class (in the target program) whose class loader should be used to load your classes. Must only be set when ```classLoaderPolicy``` is equal to ```CUSTOM```.
* injectionMethod: The method that should be used to load your classes. Must either be ```DUMPCLASSES``` or ```INJECTCLASSPATH```. Must always be set.
* mainClassPolicy: The policy to determine what class should be used as the main class. Is either ```MANIFEST```, ```CUSTOM``` or ```ANNOTATED```. Must always bet set.
* mainClass: The name of the class to use as the main class. Must only be set if ```mainClassPolicy``` is equals to ```CUSTOM```.
* mainClassAnnotation: The fully qualified class name of the annotation class that should be used to identify your main class. Must only be set if ```mainClassPolicy``` is equal to ```ANNOATED```.
* mainMethodPolicy: The policy that should be used to select an entry point method from your main class. Is either ```CUSTOM```, ```MAIN```, or ```ANNOTATED```. Must always be set.
* mainMethod: The method name of the method that should be used as an entry point. Must only be set if ```mainMethodPolicy``` is equal to ```CUSTOM```.
* mainMethodAnnotation: The fully qualified class name of the annotation class that be should to identify the main method within your main class. Must only be set if ```mainMethodPolicy``` is equals to ```ANNOTATED```.

A more thorough explanation of what different values for different policies do is as follows:
* ClassLoaderPolicy:
  * ```RECOMMENDED```: Will use the system class loader.
  * ```CUSTOM```: Will use a specified class' class loader. The classLoaderClass attribute is used to find that class. It cannot be a member of the jar that is being loaded.
* InjectionMethod:
  * ```DUMPCLASSES```: Will extract the classes from the jar and load them onto the class loader supplied by the class loader policy. The loaded jar can be deleted after the
    injection process is complete. Classes from the loaded jar cannot access resources stored in the jar -  this will lead to crashes. They can access other resources present
    in the target program.
  * ```INJECTCLASSPATH```: Will append the jar to the class loader's search path. The loaded jar can only be deleted when the target program exits. Classes from the loaded jar can
    access resources found in the jar.
* MainClassPolicy:
  * ```MANIFEST```: Will use the class found in the jar's manifest. The manifest attribute should be 'Main-Class'. Note that this will error if the jar to be loaded does not have a manifest
    or the manifest does not have the main class attribute.
  * ```CUSTOM```: Will use the class specified by the value of mainClass.
  * ```ANNOTATED```: Will use the first class that is found to have the annotation specified by the value of mainClassAnnotation.
* MainMethodPolicy:
  * ```CUSTOM```: Will use the method with the name specified by the value of mainMethod. If the method is overloaded, the one with the least parameters will be used.
  * ```MAIN```: Will use a method with the name 'main' that takes a String[] as its only parameter.
  * ```ANNOTATED```: Will use the first method that is found to have the annotation specified by the value of mainMethodAnnotation.
