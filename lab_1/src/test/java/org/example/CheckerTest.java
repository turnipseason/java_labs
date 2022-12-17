package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckerTest {
    @Test
    void squareBracketWithoutAClosingOneIsUnbalanced()
    {
        var checker = new Checker("src/main/resources/brackets.json");
        assertFalse(checker.checkBracketsInFile("src/main/resources/bad_brackets"));
    }

    @Test
    void balancedBracketsReturnTrue()
    {
        var checker = new Checker("src/main/resources/brackets.json");
        assertTrue(checker.checkBracketsInFile("src/main/resources/good_brackets"));
    }

    @Test
    void emptyStackReturnsFalse()
    {
        var checker = new Checker("src/main/resources/brackets.json");
        assertFalse(checker.checkBracketsInFile("src/main/resources/empty_stack"));
    }
}