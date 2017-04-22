package com.spedge.hangar.proxy;

public class ProxyRequest 
{
    private String remotePath;
    private String[] proxies;
    private ProxyResponse response;

    public ProxyRequest(String[] proxies, String[] array)
    {
        this.proxies = proxies;
        this.remotePath = String.join("/", array);
    }

    public String getRemotePath()
    {
        return remotePath;
    }
    
    public String[] getProxies()
    {
        return proxies;
    }

    public void saveResponse(int status, int length, byte[] byteArray)
    {
        this.response = new ProxyResponse(status, length, byteArray);
    }
    
    public byte[] getStream()
    {
        return this.response.stream;
    }
    
    public int getLength()
    {
        return this.response.length;
    }
    
    public int getStatus()
    {
        return this.response.status;
    }
    
    private class ProxyResponse
    {
        private int status;
        private int length;
        private byte[] stream;

        ProxyResponse(int status, int length, byte[] byteArray)
        {
            this.status = status;
            this.length = length;
            this.stream = byteArray;
        }
    }
}
