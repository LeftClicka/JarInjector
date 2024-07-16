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
