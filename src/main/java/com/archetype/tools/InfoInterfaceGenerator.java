package com.archetype.tools;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * Simple generator that inspects a compiled controller class via reflection and emits
 * a starter *Info interface source for OpenAPI annotations.
 * <p>
 * Usage:
 * 1) Ensure the project is compiled (gradlew build) so classes exist under build/classes/java/main
 * 2) Run:
 * java -cp build/classes/java/main;build/resources/main com.archetype.tools.InfoInterfaceGenerator com.archetype.layer.controller.PokemonController [outputDir]
 * <p>
 * If outputDir is provided the generated file <ControllerName>Info.java will be written there,
 * otherwise the source is printed to stdout.
 * <p>
 * Notes:
 * - The generator emits @Operation/@ApiResponse placeholders and method signatures matching the controller.
 * - You should review and add detailed OpenAPI annotations (@Parameter, descriptions, responses) after generation.
 * - This is a lightweight helper for developer/agent use and is intentionally conservative (no code parsing).
 */
public class InfoInterfaceGenerator {

    public static void main(String[] args)
    throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: InfoInterfaceGenerator <fully.qualified.ControllerClass> [outputDir]");
            System.exit(2);
        }

        String className = args[0];
        Class<?> controller = Class.forName(className);

        String pkg = controller.getPackageName();
        String controllerSimple = controller.getSimpleName();
        String ifaceName = controllerSimple + "Info";

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(pkg).append(";\n\n");
        sb.append("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        sb.append("import io.swagger.v3.oas.annotations.Operation;\n");
        sb.append("import io.swagger.v3.oas.annotations.responses.ApiResponse;\n");
        sb.append("\n");
        sb.append("/**\n");
        sb.append(" * Generated Info interface for ").append(controllerSimple).append("\n");
        sb.append(" * Review and add detailed OpenAPI annotations as needed.\n");
        sb.append(" */\n");
        sb.append("@Tag(name = \"").append(controllerSimple.replace("Controller", "")).append(" API\")").append(System.lineSeparator());
        sb.append("public interface ").append(ifaceName).append(" {\n\n");

        for (Method m : controller.getDeclaredMethods()) {
            if (!Modifier.isPublic(m.getModifiers())) continue;

            sb.append("    @Operation(summary = \"TODO: summary for ").append(m.getName()).append("\")\n");
            sb.append("    @ApiResponse(responseCode = \"200\", description = \"TODO\")\n");

            // return type
            String returnType = m.getGenericReturnType().getTypeName();
            sb.append("    ").append(returnType).append(" ").append(m.getName()).append("(");

            Parameter[] params = m.getParameters();
            for (int i = 0; i < params.length; i++) {
                Parameter p = params[i];
                String typeName = p.getType().getTypeName();
                String paramName = p.isNamePresent() ? p.getName() : "param" + i;
                sb.append(typeName).append(" ").append(paramName);
                if (i < params.length - 1) sb.append(", ");
            }
            sb.append(");\n\n");
        }

        sb.append("}\n");

        String output = sb.toString();
        if (args.length > 1) {
            java.nio.file.Path outDir = java.nio.file.Path.of(args[1]);
            java.nio.file.Files.createDirectories(outDir);
            java.nio.file.Path outFile = outDir.resolve(ifaceName + ".java");
            java.nio.file.Files.writeString(outFile, output);
            System.out.println("Wrote: " + outFile.toAbsolutePath());
        } else {
            System.out.println(output);
        }
    }
}

