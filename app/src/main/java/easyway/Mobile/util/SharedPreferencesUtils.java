package easyway.Mobile.util;

import android.content.Context;
import android.content.SharedPreferences;


/** 
 * SharedPreferences鐨勪竴涓伐鍏风被锛岃皟鐢╯etParam灏辫兘淇濆瓨String, Integer, Boolean, Float, Long绫诲瀷鐨勫弬鏁?
 * 鍚屾牱璋冪敤getParam灏辫兘鑾峰彇鍒颁繚瀛樺湪鎵嬫満閲岄潰鐨勬暟鎹?
 * @author lushiju 
 * 
 */  
public class SharedPreferencesUtils {  
    /** 
     * 淇濆瓨鍦ㄦ墜鏈洪噷闈㈢殑鏂囦欢鍚?
     */  
    private static final String FILE_NAME = "saveUser";  
      
      
    /** 
     * 淇濆瓨鏁版嵁鐨勬柟娉曪紝鎴戜滑闇?鎷垮埌淇濆瓨鏁版嵁鐨勫叿浣撶被鍨嬶紝鐒跺悗鏍规嵁绫诲瀷璋冪敤涓嶅悓鐨勪繚瀛樻柟娉?
     * @param context 
     * @param key 
     * @param object  
     */  
    public static void setParam(Context context , String key, Object object){  
          
        String type = object.getClass().getSimpleName();  
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);  
        SharedPreferences.Editor editor = sp.edit();  
          
        if("String".equals(type)){  
            editor.putString(key, (String)object);  
        }  
        else if("Integer".equals(type)){  
            editor.putInt(key, (Integer)object);  
        }  
        else if("Boolean".equals(type)){  
            editor.putBoolean(key, (Boolean)object);  
        }  
        else if("Float".equals(type)){  
            editor.putFloat(key, (Float)object);  
        }  
        else if("Long".equals(type)){  
            editor.putLong(key, (Long)object);  
        }  
          
        editor.commit();  
    }  
      
      
    /** 
     * 寰楀埌淇濆瓨鏁版嵁鐨勬柟娉曪紝鎴戜滑鏍规嵁榛樿鍊煎緱鍒颁繚瀛樼殑鏁版嵁鐨勫叿浣撶被鍨嬶紝鐒跺悗璋冪敤鐩稿浜庣殑鏂规硶鑾峰彇鍊?
     * @param context 
     * @param key 
     * @param defaultObject 
     * @return 
     */  
    public static Object getParam(Context context , String key, Object defaultObject){  
        String type = defaultObject.getClass().getSimpleName();  
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);  
          
        if("String".equals(type)){  
            return sp.getString(key, (String)defaultObject);  
        }  
        else if("Integer".equals(type)){  
            return sp.getInt(key, (Integer)defaultObject);  
        }  
        else if("Boolean".equals(type)){  
            return sp.getBoolean(key, (Boolean)defaultObject);  
        }  
        else if("Float".equals(type)){  
            return sp.getFloat(key, (Float)defaultObject);  
        }  
        else if("Long".equals(type)){  
            return sp.getLong(key, (Long)defaultObject);  
        }  
          
        return null;  
    }
}  
