package com.spedge.hangar.index.memory;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.index.ReservedArtifact;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageException;

public class InMemoryIndex implements IIndex
{
    private Map<String, IndexArtifact> index;
    protected final Logger logger = LoggerFactory.getLogger(InMemoryIndex.class);

    public InMemoryIndex()
    {
        this.index = new HashMap<String, IndexArtifact>();
    }

    public boolean isArtifact(IndexKey key)
    {
        return index.containsKey(key.toString());
    }

    /**
     * Registers an Artifact with the Index.
     */
    public void addArtifact(IndexKey key, IndexArtifact artifact) throws IndexConfictException
    {
        if (index.containsKey(key.toString()))
        {
            if (!(index.get(key.toString()) instanceof ReservedArtifact))
            {
                index.put(key.toString(), artifact);
            }
            else
            {
                throw new IndexConfictException();
            }
        }
        else
        {
            index.put(key.toString(), artifact);
        }
    }

    public IndexArtifact getArtifact(IndexKey key)
    {
        IndexArtifact ia = index.get(key.toString());
        return ia;
    }

    /**
     * Loads this index with the artifacts found in storage.
     */
    public void load(RepositoryType type, IStorage storage, String uploadPath)
            throws StorageException
    {
        for (IndexKey key : storage.getArtifactKeys(uploadPath))
        {
            try
            {
                index.put(key.toString(), storage.getIndexArtifact(key, uploadPath));
            }
            catch (IndexException ie)
            {
                logger.error("Could not parse artifact at " + uploadPath + "/" + key.toString());
            }
        }
    }

    @Override
    public ReservedArtifact addReservationKey(IndexKey key)
    {
        ReservedArtifact reservation = new ReservedArtifact(key.toString());
        index.put(key.toString(), reservation);
        return reservation;
    }

    @Override
    public void addReservedArtifact(IndexKey key, ReservedArtifact reservation, IndexArtifact ia)
            throws IndexConfictException
    {
        if (index.get(key.toString()).equals(reservation))
        {
            index.put(key.toString(), ia);
        }
        else
        {
            throw new IndexConfictException();
        }
    }
}
