package com.github.filipmalczak.thrive.example.items;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import springfox.documentation.schema.AlternateTypeRule;

import java.util.List;

public class RecursiveAlternateTypeRule extends AlternateTypeRule {

    private List<AlternateTypeRule> rules;

    public RecursiveAlternateTypeRule(TypeResolver typeResolver, List<AlternateTypeRule> rules) {
        // Unused but cannot be null
        super(typeResolver.resolve(Object.class), typeResolver.resolve(Object.class));
        this.rules = rules;
    }

    @Override
    public ResolvedType alternateFor(ResolvedType type) {
        ResolvedType newType = rules
            .stream()
            .map(rule -> rule.alternateFor(type))
            .filter(alternateType -> alternateType != type)
            .findFirst()
            .orElse(type);

        if (appliesTo(newType)) {
            // Recursion happens here
            return alternateFor(newType);
        }

        return newType;
    }

    @Override
    public boolean appliesTo(ResolvedType type) {
        return rules.stream().anyMatch(rule -> rule.appliesTo(type));
    }
}
