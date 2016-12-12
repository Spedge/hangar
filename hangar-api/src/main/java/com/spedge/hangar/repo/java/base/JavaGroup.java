package com.spedge.hangar.repo.java.base;

/**
 * A base object to allow easy generation of JavaGroup definitions (they come at us in many formats). 
 * @author Spedge
 *
 */
public class JavaGroup
{
    private String group;
    
    private JavaGroup(String group)
    {
        this.group = group;
    }
    
    /**
     * Returns the dot-delimited group.
     * @return Group using . (e.g. org.blah.things)
     */
    public String getGroup()
    {
        return group;
    }
    
    /**
     * Sometimes we want to return the Java Group as a path-based structure,
     * either for storage purposes or querying a proxy.
     * @return Group using / instead of . (e.g. org/blah/things)
     */
    public String getGroupAsPath()
    {
        return group.replace(".", "/");
    }
    
    /**
     * Used to generate a JavaGroup from a slash-delimited string
     * @param groupString Changing /org/blah/things -> org.blah.things
     * @return A new JavaGroup with the correct format.
     */
    public static JavaGroup slashDelimited(String groupString)
    {
        return new JavaGroup(groupString.replace('/', '.'));
    }
    
    /**
     * Used to generate a JavaGroup from a dot-delimited string
     * @param groupString org.blah.things
     * @return A new JavaGroup with the correct format.
     */
    public static JavaGroup dotDelimited(String groupString)
    {
        return new JavaGroup(groupString);
    }
    
    @Override
    public String toString()
    {
        return getGroup();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        JavaGroup other = (JavaGroup) obj;
        if (group == null)
        {
            if (other.group != null)
            {
                return false;
            }
        }
        else if (!group.equals(other.group))
        {
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        return result;
    }
    
}
