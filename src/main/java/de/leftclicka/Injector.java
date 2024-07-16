package de.leftclicka;

import com.sun.tools.attach.*;
import de.leftclicka.configuration.Configuration;

import java.io.File;

public class Injector {

    public static void run(VirtualMachineDescriptor target, Configuration config, ExceptionHandler exceptionHandler) {
        try {
            VirtualMachine vm = VirtualMachine.attach(target);
            try {
                vm.loadAgent(
                        new File(Injector.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(),
                        config.encode()
                );
            } catch (AgentInitializationException e) {
                if (e.returnValue() != 0)
                    throw e;
            }
            vm.detach();
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    public static void run(VirtualMachineDescriptor target, Configuration config) {
        run(target, config, ExceptionHandler.WRAP_AND_THROW);
    }

}
