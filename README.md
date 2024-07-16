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
*jarPath: The path to the jar to be loaded. Should be a fully qualified path. Must always be set.
*classLoaderPolicy: The policy to determine what class loader to use to load your classes. Is either ```RECOMMENDED``` or ```CUSTOM```. Must always be set.
