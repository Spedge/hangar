package com.spedge.hangar.repo.python;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.storage.IStorageTranslator;
import com.spedge.hangar.storage.local.LocalStorageException;

public class PythonStorageTranslator implements IStorageTranslator
{
    private final String[] delimiters = new String[]{};
    private RepositoryType type;
    private final Pattern regex;
    
    public PythonStorageTranslator(RepositoryType type)
    {
        this.type = type;
        
        String versionPattern = "([0-9A-Za-z-_]+)";
        regex = Pattern.compile(versionPattern);
    }
    
    @Override
    public String[] getDelimiters()
    {
        return delimiters;
    }

    @Override
    public IndexKey generateIndexKey(String prefixPath, String prefix) throws IndexException
    {
          Matcher match = regex.matcher(prefixPath);
          if (match.find())
          {
              String artifactPre = match.group(0);
              String artifact = artifactPre.substring(0, artifactPre.lastIndexOf("-")).toLowerCase();
              PythonIndexKey pk = new PythonIndexKey(type, artifact, prefix);
              return pk;
          }
          else
          {
              throw new IndexException("Cannot parse version " + prefixPath + " to find artifact name.");
          }
    }

    @Override
    public IndexArtifact generateIndexArtifact(IndexKey key, String uploadPath) throws IndexException
    {
        String artifact = key.toPath().replace(":", "/");
        IndexArtifact ia = new PythonIndexArtifact("/" + uploadPath + "/" + artifact);
        return ia;
    }

    @Override
    public RepositoryType getType()
    {
        return type;
    }

    @Override
    public List<IndexKey> getLocalStorageKeys(Path sourcePath) throws LocalStorageException
    {
        List<IndexKey> paths;
        try
        {
            paths = Files.walk(sourcePath)
                         .filter(Files::isRegularFile).map(e -> e.toString().replace(sourcePath.toString(), ""))
                         .map(e -> e.substring(e.indexOf(File.separator) + 1, e.length()))
                         .map(e -> e.replace(File.separator, ":"))
                         .distinct().map(e -> new IndexKey(getType(), e)).collect(Collectors.toList());
        }
        catch (IOException ioe)
        {
            throw new LocalStorageException(ioe);
        }
        
        return paths;
    }
}
