package org.stringtree.mojasef.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.stringtree.finder.StringFinder;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.ContextClassUtils;
import org.stringtree.util.MethodCallUtils;
import org.stringtree.util.StringUtils;

public class RESTCollection {
	public static String RADAR_PREFIX = "radar";
	public static final String RADAR_BODY = RADAR_PREFIX + ".body";
	public static final String RADAR_METHOD = RADAR_PREFIX + ".method";
	
	protected String idKey = MojasefConstants.REQUEST_LOCALPATH;  
	private String personalityName;
    private String lookAndFeelName;
	private CollectionPersonality personality;
    private Object lookAndFeel;
	
	public RESTCollection(CollectionPersonality personality, Object lookAndFeel) {
		this.personality = personality;
        this.lookAndFeel = lookAndFeel;
	}
    
    public RESTCollection(CollectionPersonality personality) {
        this(personality, null);
    }
    
    public RESTCollection(String personalityName, String lookAndFeelName) {
        this.personalityName = personalityName;
        this.lookAndFeelName = lookAndFeelName;
    }
    
    public RESTCollection(String personalityName, Object lookAndFeel) {
        this.personalityName = personalityName;
        this.lookAndFeel = lookAndFeel;
    }
	
	public RESTCollection(String personalityName) {
        this(personalityName, null);
	}
    
    public void setLookAndFeelName(String lookAndFeelName) {
        this.lookAndFeelName = lookAndFeelName;
    }
	
	public void init(StringFinder context) {
        if (null == personality) {
            personality = (CollectionPersonality)context.getObject(personalityName);
        }

        if (null == personality) {
            personality = (CollectionPersonality) ContextClassUtils.ensureObject(personalityName, context);
        }
        
        if (null != personality) {
            MethodCallUtils.call(personality, "init", context);
        }
        
        if (null != lookAndFeelName) {
            if (null == lookAndFeel) {
                lookAndFeel = context.getObject(lookAndFeelName);
            }
            
            if (null == lookAndFeel) {
                lookAndFeel = ContextClassUtils.ensureObject(lookAndFeelName, context);;
            }
        }
        
        if (null == lookAndFeel){
            lookAndFeel = personality;
        }
	}
    
    public void warmup(StringKeeper context) {
        context.put(MojasefConstants.APPLICATION_BASE_URL, context.get(MojasefConstants.MOUNTCONTEXT) + context.get(MojasefConstants.MOUNTPOINT));
    }

	public void GET(StringKeeper context) {
		if (!radarRequest(context)) {
			String id = context.get(idKey);
			if (StringUtils.isBlank(id)) {
				personality.list(context);
			} else {
				Object object = null;
				if (personality.validate(context, id)) {
					object = personality.getObject(id);
					if (object == null) {
						object = personality.empty(context, id);
					}
					context.put("this", object);
					MethodCallUtils.call(lookAndFeel, "afterGet", context);
				} else {
					context.put(HTTPConstants.RESPONSE_CODE, "404");
				}
			}
		}
	}

	public void PUT(StringKeeper context) {
		String id = context.get(idKey);
		Object object = null;
		if (null != personality && personality.validate(context, id)) {
			InputStream in = (InputStream) context.getObject(MojasefConstants.REQUEST_STREAM);
			try {
				object = personality.parse(context, id, in);
				if (null != object) {
				    personality.put(id, object);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		context.put("this", object);
		MethodCallUtils.call(lookAndFeel, "afterPut", context);
	}

	public void POST(StringKeeper context) {
		if (!radarRequest(context)) {
			String id = personality.createId(context);
			context.put(HTTPConstants.RESPONSE_CODE, "303");
            context.put(HTTPConstants.RESPONSE_HEADER_LOCATION, context.get(MojasefConstants.APPLICATION_BASE_URL) + id);
			MethodCallUtils.call(lookAndFeel, "afterPost", context);
		}
	}

	public void DELETE(StringKeeper context) {
		String id = context.get(idKey);
		personality.remove(id);
		MethodCallUtils.call(lookAndFeel, "afterDelete", context);
	}


	private boolean radarRequest(StringKeeper context) {
		String radarMethod = context.get(RADAR_METHOD);
		if (StringUtils.isBlank(radarMethod)) return false;
		
		String radarBody = context.get(RADAR_BODY);
		if ("PUT".equals(radarMethod)) {
			context.put(MojasefConstants.REQUEST_STREAM, new ByteArrayInputStream(radarBody.getBytes()));
			removeRadarEntries(context);
			PUT(context);
			return true;
		} else if ("POST".equals(radarMethod)) {
			context.put(MojasefConstants.REQUEST_STREAM, new ByteArrayInputStream(radarBody.getBytes()));
			removeRadarEntries(context);
			POST(context);
			return true;
		} else if ("GET".equals(radarMethod)) {
			removeRadarEntries(context);
			GET(context);
			return true;
		} else if ("DELETE".equals(radarMethod)) {
			removeRadarEntries(context);
			DELETE(context);
			return true;
		}
		return false;
	}

	private void removeRadarEntries(StringKeeper context) {
		context.remove(RADAR_METHOD);
		context.remove(RADAR_BODY);
	}
}
