package org.example;

public class Bracket {
    private String open;
    private String close;

    public Bracket(String first, String last)
    {
        open = first;
        close = last;
    }

    public Bracket(){}

    public String getOpen() {return this.open;}
    public String getClose() {return this.close;}
    public boolean isLeft(char compare){return compare==open.charAt(0);};
    public boolean isRight(char compare){return compare==close.charAt(0);};
}
