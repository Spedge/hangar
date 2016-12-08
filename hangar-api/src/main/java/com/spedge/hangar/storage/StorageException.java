package com.spedge.hangar.storage;

public abstract class StorageException extends Exception
{
    public StorageException(Exception exception)
    {
        super(exception);
    }

    private static final long serialVersionUID = -4510536728418574349L;
}
