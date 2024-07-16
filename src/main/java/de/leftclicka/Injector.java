package de.leftclicka;

import com.sun.tools.attach.*;
import de.leftclicka.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Injector {

    public static void run(VirtualMachineDescriptor target, Configuration config) throws IOException, AttachNotSupportedException, URISyntaxException, AgentLoadException, AgentInitializationException {
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
    }

}
