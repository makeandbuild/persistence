package com.makeandbuild.fixture;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.type.JavaType;

@SuppressWarnings("rawtypes")
public class ResourceEntityLoaderImpl implements EntityLoader {
    protected static ObjectMapper mapper;
    protected Class entityClass;
    protected String resourcePath;
    protected String subtype=null;
    protected static Log logger = LogFactory.getLog(ResourceEntityLoaderImpl.class);
    
    public ResourceEntityLoaderImpl(String resourcePath) throws ClassNotFoundException{
        super();
        String classname = resourcePath.replace(".json", "");
        if (classname.lastIndexOf("/") != -1){
            classname = classname.substring(classname.lastIndexOf("/")+1);
            
        }
        String[] split = classname.split("-");
        this.entityClass = Class.forName(split[0]);
        if (split.length>1){
            subtype = split[1];
        }
        this.resourcePath = resourcePath;
    }
    public ResourceEntityLoaderImpl(String resourcePath,  Class entityClass, String subtype) throws ClassNotFoundException{
        super();
        this.entityClass = entityClass;
        this.subtype = subtype;
        this.resourcePath = resourcePath;
    }

    @Override
    public Class getEntityClass() {
        return entityClass;
    }
    @SuppressWarnings("deprecation")
    protected static ObjectMapper getInstance() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
            mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        }
        return mapper;
    }
    @Override
    public List<Object> load() throws IOException {
        try {
            String json = IOUtils.toString(this.getClass().getResourceAsStream(resourcePath));
            ArrayNode node = (ArrayNode) getInstance().readTree(json);
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, entityClass);
            return getInstance().readValue(node, type);
        }catch (IOException e){
            logger.error("problem loading resource "+resourcePath, e);
            throw e;
        }

    }

    @Override
    public String getSubtype() {
        return subtype;
    }

    @Override
    public List<Object> loadReverse() throws IOException {
        List<Object> items = this.load();
        Collections.reverse(items);
        return items;
    }
}
