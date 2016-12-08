package com.spedge.hangar.repo.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.IStorageTranslator;
import com.spedge.hangar.storage.local.LocalStorageException;

public class JavaStorageTranslator implements IStorageTranslator
{
    protected final Logger logger = LoggerFactory.getLogger(JavaStorageTranslator.class);
    private final String[] delimiters = new String[]{ ".pom", "maven-metadata.xml" };
    private RepositoryType type;
    
    public JavaStorageTranslator(RepositoryType type)
    {
        this.type = type;
    }
    
    @Override 
    public RepositoryType getType()
    {
        return type;
    }

    @Override
    public String[] getDelimiters()
    {
        return delimiters;
    }

    @Override
    public IndexKey generateIndexKey(String sourcePath, String prefixPath) throws IndexException
    {
        String[] sections = sourcePath.substring(prefixPath.length(), 
                                                 sourcePath.lastIndexOf("/"))
                                                 .split("/");

        if (sections.length < 3)
        {
            logger.info("[ERROR] Broken Artifact (less than 3 parameters) : " + sourcePath);
            throw new IndexException("Broken Artifact (less than 3 parameters) : " + sourcePath);
        }

        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < (sections.length - 2); i++)
        {
            strBuilder.append(sections[i]);
            if (i < (sections.length - 3))
            {
                strBuilder.append(".");
            }
        }

        return new JavaIndexKey(type, strBuilder.toString(), 
                                      sections[sections.length - 2],
                                      sections[sections.length - 1]);
    }
    
    @Override
    public IndexArtifact generateIndexArtifact(IndexKey key, String uploadPath)
    {
        JavaIndexKey jik;
        
        if (key instanceof JavaIndexKey)
        {
            jik = (JavaIndexKey) key;
        }
        else
        {
            String[] split = key.toPath().split(":");
            String group = split[0];
            String artifact = (split.length > 1) ? split[1] : "";
            String version = (split.length > 2) ? split[2] : "";
            jik = new JavaIndexKey(type, group, artifact, version);
        }
        
        String version = jik.getVersion().isEmpty() ? "" : "/" + jik.getVersion();
        String location = "/" + uploadPath + "/" + jik.getGroup().replace('.', '/') + "/"
                        + jik.getArtifact() + version;
        
        IndexArtifact ia = new JavaIndexArtifact(location);
        
        return ia;
    }

    @Override
    public List<IndexKey> getLocalStorageKeys(Path sourcePath) throws LocalStorageException
    {
        List<IndexKey> paths;
        try
        {
            paths = Files.walk(sourcePath)
                         .filter(Files::isRegularFile).map(e -> e.toString().replace(sourcePath.toString(), ""))
                         .map(e -> e.subSequence(0, e.lastIndexOf(File.separator)).toString())
                         .map(e -> e.substring(1, StringUtils.lastOrdinalIndexOf(e, File.separator, 2))
                                    .replace(File.separator, ".")
                                    + ":"
                                    + e.substring(StringUtils.lastOrdinalIndexOf(e, File.separator, 2),
                                      e.lastIndexOf(File.separator)).replace(File.separator, "")
                                    + ":"
                                    + e.substring(e.lastIndexOf(File.separator), e.length())
                                       .replace(File.separator, ""))
                        .distinct().map(e -> new IndexKey(getType(), e)).collect(Collectors.toList());
        }
        catch (IOException ioe)
        {
            throw new LocalStorageException(ioe);
        }
        
        return paths;
    }

}
