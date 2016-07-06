package com.spedge.hangar.index.zookeeper;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.index.ReservedArtifact;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.storage.IStorage;

/**
 * This is the interface for an index based on Zookeeper. 
 * 
 * It relies heavily on the <a href="http://curator.apache.org/">Curator</a> framework
 * developed by Netflix. 
 *  
 * @author Spedge
 *
 */
public class ZooKeeperIndex implements IIndex {

	protected final static Logger logger = LoggerFactory.getLogger(ZooKeeperIndex.class);
	
	@NotNull
	@JsonProperty
	private String connectionString;
	
	private CuratorFramework client;
    
	@Override
	public boolean isArtifact(IndexKey key) throws IndexException 
	{
		String path = convertPath(key);
		try
		{
			return client.checkExists().forPath(path) != null;
		} 
	    catch (Exception e) 
	    {
			throw new IndexException(e);
		}
	}

	@Override
	public void addArtifact(IndexKey key, IndexArtifact artifact) throws IndexException, IndexConfictException 
	{
		String path = convertPath(key);
		if(isArtifact(key))
		{
			if(!(getArtifact(key) instanceof ReservedArtifact))
			{
				try
				{
					client.setData()
				      	  .forPath(path, SerializationUtils.serialize(artifact));
				} 
				catch (Exception e) 
				{
					throw new IndexException(e);
				}
			}
			else
			{
				throw new IndexConfictException();
			}
		}
		else
		{
			try 
			{
				client.create()
				      .creatingParentsIfNeeded()
				      .forPath(path, SerializationUtils.serialize(artifact));
			} 
			catch (Exception e) 
			{
				throw new IndexException(e);
			}
		}	
	}

	@Override
	public IndexArtifact getArtifact(IndexKey key) throws IndexException 
	{
		try 
		{
			return SerializationUtils.deserialize(client.getData().forPath(convertPath(key)));
		} 
		catch (Exception e) 
		{
			throw new IndexException(e);
		}
	}

	@Override
	public void load(RepositoryType type, IStorage storage, String uploadPath) throws IndexException 
	{
		startClient(this.connectionString);
		
        try 
        {
			for(IndexKey key : storage.getArtifactKeys(type, uploadPath))
			{
				String path = convertPath(key);
				
				if(client.checkExists().forPath(path) == null)
				{
					client.create()
						  .creatingParentsIfNeeded()
						  .forPath(path, SerializationUtils.serialize(storage.generateArtifactPath(type, uploadPath, key)));
					
					logger.info("[ZookeeperIndex] Adding " + path + " to index.");
				}
			}
		} 
        catch (Exception e) 
        {
			throw new IndexException(e);
		}
		
	}

	@Override
	public ReservedArtifact addReservationKey(IndexKey key) throws IndexException 
	{
		ReservedArtifact reservation = new ReservedArtifact();
		
		try 
		{
			client.create()
			      .creatingParentsIfNeeded()
			      .forPath(convertPath(key), SerializationUtils.serialize(reservation));
		} 
		catch (Exception e) 
        {
			throw new IndexException(e);
		}
			  
		return reservation;
	}

	@Override
	public void addReservedArtifact(IndexKey key, ReservedArtifact reservation, IndexArtifact artifact)	throws IndexConfictException, IndexException 
	{
		if(getArtifact(key).equals(reservation))
		{
			try
			{
				client.setData()
				      .forPath(convertPath(key), SerializationUtils.serialize(artifact));				      
			} 
			catch (Exception e) 
			{
				throw new IndexException(e);
			}
		}
		else
		{
			throw new IndexConfictException();
		}
	}
	
	private String convertPath(IndexKey key)
	{
		return "/" + key.toString().replace(":", "/");
	}
		
	public void startClient(String connectionString)
	{
		if(client == null)
    	{
        	RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
			client = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
			client.start();
    	}
	}
}
