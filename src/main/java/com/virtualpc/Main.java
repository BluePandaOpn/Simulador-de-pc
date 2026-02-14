package com.virtualpc;

import com.virtualpc.config.VmConfig;
import com.virtualpc.core.VirtualMachine;
import com.virtualpc.programs.DemoProgram;
import com.virtualpc.programs.SampleBinPrograms;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        VmConfig config = VmConfig.defaultConfig();
        SampleBinPrograms.ensureDefaultPrograms(Path.of("data", "bin-programs"));
        VirtualMachine vm = new VirtualMachine(config);

        byte[] romImage;
        if (args.length > 0) {
            Path romPath = Path.of(args[0]);
            romImage = Files.readAllBytes(romPath);
            vm.getRam().writeBlock(config.romLoadAddress(), romImage);
            System.out.println("ROM loaded from: " + romPath.toAbsolutePath());
        } else {
            romImage = DemoProgram.build();
            vm.getRam().writeBlock(config.romLoadAddress(), romImage);
            System.out.println("No ROM provided. Demo program loaded into RAM.");
        }

        vm.setRomImage(romImage);
        vm.start();
    }
}
