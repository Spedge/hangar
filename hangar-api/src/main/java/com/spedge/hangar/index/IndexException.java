package com.spedge.hangar.index;

public class IndexException extends Exception
{
    private static final long serialVersionUID = 6617225189161552873L;
    
    public IndexException(Exception ie)
    {
        super(ie);
    }
}