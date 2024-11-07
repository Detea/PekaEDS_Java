package pekaeds.pk2.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.*;

public class PK2FileSystem {

/**
 * 
 * @param 
 * debugging
 */
public static void main(String[] args){
    System.out.println("Hello world");

    try{
        setAssetsPath("/Users/saturninufolud/c++/pk2_greta");
        
        System.out.println(getAssetsPath().toString());
                
        setEpisodeName("Debug Island");


        System.out.println("--------\nTEST: findAsset:");

        File f = findAsset("tiles01.bmp", TILESET_DIR);
        System.out.println(f);

        /**
         * 
         */

        System.out.println("--------\nTEST: findSprite:");

        f = findSprite("mune.spr2");
        System.out.println(f);

        f = findSprite("PiG.sPr");
        System.out.println(f);

        ArrayList<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(12);
        a.add(13);

        System.out.println(a.indexOf(12));
        System.out.println(a.indexOf(14));
        
    }
    catch(Exception e){
        System.out.println(e);
    }
}

public static final String PK2_STUFF_NAME = "pk2stuff.bmp";
public static final String SPRITES_DIR = "sprites";
public static final String EPISODES_DIR = "episodes";

public static final String GFX_DIR = "gfx";
public static final String TILESET_DIR = "gfx"+File.separator+"tiles";
public static final String SCENERY_DIR = "gfx"+File.separator+"scenery";

public static final String MUSIC_DIR = "music";
public static final String LUA_DIR = "lua";

private static File mAssetsPath;
private static String mEpisodeName;

public static void setAssetsPath(String assetsPath) throws FileNotFoundException{
    setAssetsPath(new File(assetsPath));
}

public static void setAssetsPath(File assetsPath) throws FileNotFoundException{   
    if(!assetsPath.exists() || !assetsPath.isDirectory()){
        throw new FileNotFoundException("Not a directory: "+assetsPath.toString());
    }

    File pk2stuff = Paths.get(assetsPath.getPath(), "gfx", PK2_STUFF_NAME).toFile();
    if(pk2stuff.exists() && !pk2stuff.isDirectory()){

        mAssetsPath = assetsPath;
    }
    else{
        pk2stuff = Paths.get(assetsPath.getPath(),"res", "gfx", PK2_STUFF_NAME).toFile();

        if(pk2stuff.exists() && !pk2stuff.isDirectory()){
            mAssetsPath = Paths.get(assetsPath.getPath(), "res").toFile();
        }
        else{
            throw new FileNotFoundException("Not a PK2 directory!");
        }
    }
}

public static File getAssetsPath(){
    return mAssetsPath;
}

public static File getAssetsPath(String subfolder){
    return Paths.get(mAssetsPath.getPath(), subfolder).toFile();
}

public static File getPK2StuffFile(){
    return Paths.get(mAssetsPath.getPath(), "gfx", PK2_STUFF_NAME).toFile();
}

public static void setEpisodeName(String name){
    mEpisodeName = name;
}

public static String getEpisodeName(){
    return mEpisodeName;
}

public static boolean isEpisodeSet(){
    return mEpisodeName!=null && !mEpisodeName.equals("");
}


private static File findFile(File dir, String lowercase){

    if(!dir.exists())return null;
           
    FilenameFilter filter = (d, name) -> lowercase.equals(name.toLowerCase());
    File[] res = dir.listFiles(filter);

    return (res ==null || res.length==0) ? null : res[0];
}

public static File findAsset(String assetName, String defaultDir) throws FileNotFoundException {

    File f = new File(assetName);
    /**
     * full path
     */
    if(f.exists() && !f.isDirectory())return f;

    String lowercase = f.getName().toLowerCase();

    if(isEpisodeSet()){
        f = findFile(
            Paths.get(mAssetsPath.getPath(), EPISODES_DIR, mEpisodeName).toFile(),lowercase);
        
        if(f!=null)return f;

        f = findFile(
            Paths.get(mAssetsPath.getPath(), EPISODES_DIR, mEpisodeName, defaultDir).toFile(),lowercase);

        if(f!=null)return f;
    }

    f = findFile(Paths.get(mAssetsPath.getPath(), defaultDir).toFile(),lowercase);

    if(f==null){
        throw new FileNotFoundException("PK2 file \""+assetName+"\" not found!");
    }

    return f;
}

public static File findSprite(String spriteName) throws FileNotFoundException{
    if(spriteName.toLowerCase().endsWith(".spr")){

        //.spr2 first
        try{
            return findAsset(spriteName + "2", SPRITES_DIR);
        }
        catch(FileNotFoundException e){
            return findAsset(spriteName, SPRITES_DIR);
        }        
    }
    else{
        return findAsset(spriteName, SPRITES_DIR);
    }
}

public static File findTileset(String name) throws FileNotFoundException{
    return findAsset(name, TILESET_DIR);
}

public static File findBackground(String name) throws FileNotFoundException{
    return findAsset(name, SCENERY_DIR);
}

}
