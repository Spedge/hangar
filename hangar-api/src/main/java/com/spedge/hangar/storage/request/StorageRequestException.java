package com.spedge.hangar.storage.request;

import java.io.IOException;

public class StorageRequestException extends Exception
{
    public static final int DOES_NOT_EXIST = 0;

    public StorageRequestException(int doesNotExist)
    {
        // TODO Auto-generated constructor stub
    }

    public StorageRequestException(IOException ioe)
    {
        // TODO Auto-generated constructor stub
    }

}
