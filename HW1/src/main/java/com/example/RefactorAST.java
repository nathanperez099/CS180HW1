package com.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RefactorAST {
    public static void main(String[] args) throws IOException {
        String sourceFilePath = "src/main/resources/SimpleComparison.java"; // Hardcoded input file

        // Parse the Java file into an AST
        CompilationUnit compilationUnit = StaticJavaParser.parse(new File(sourceFilePath));

        // Perform refactoring: Change "!=" to "==" and swap "then" and "else" statements
        compilationUnit.findAll(IfStmt.class).forEach(conditionBlock -> {
            if (conditionBlock.hasElseBranch()) {
                conditionBlock.getCondition().ifBinaryExpr(expression -> {
                    if (expression.getOperator() == BinaryExpr.Operator.NOT_EQUALS) {
                        // Change "!=" to "=="
                        expression.setOperator(BinaryExpr.Operator.EQUALS);

                        // Swap "then" and "else" statements
                        conditionBlock.setThenStmt(conditionBlock.getElseStmt().get());
                        conditionBlock.setElseStmt(conditionBlock.getThenStmt());
                    }
                });
            }
        });

        // Save the refactored AST as YAML
        saveASTToYaml(compilationUnit, "refactored_ast.yaml");

        // Output the refactored code (for verification)
        System.out.println(compilationUnit.toString());
    }

    private static void saveASTToYaml(CompilationUnit compilationUnit, String yamlOutputPath) throws IOException {
        Yaml yamlFormatter = new Yaml();
        String yamlContent = yamlFormatter.dump(compilationUnit.toString());
        Files.write(Paths.get(yamlOutputPath), yamlContent.getBytes());
    }
}
