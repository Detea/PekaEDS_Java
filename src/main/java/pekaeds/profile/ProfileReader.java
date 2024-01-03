package pekaeds.profile;

//import java.util.logging.Logger;

public final class ProfileReader {
    //private static final Logger logger = Logger.getLogger(ProfileReader.class.getName());
    
    public static MapProfile readMapProfile(String filename) {
        /*var mapper = new YAMLMapper(new YAMLFactory());
        MapProfile profile = null; // TODO Hard code a default profile?
        
        try {
            profile = mapper.readValue(new File(filename), MapProfile.class);
    
            System.out.println(profile);
        } catch (IOException e) {
            logger.warning("Couldn't load map profile file.\n" + e.getMessage());
        }*/
        
        return new MapProfile();
    }
    
    public static SpriteProfile readSpriteProfile(String filename) {
        /*var mapper = new YAMLMapper(new YAMLFactory());
        SpriteProfile spriteProfile = null;
    
        try {
            spriteProfile = mapper.readValue(new File(filename), SpriteProfile.class);
        } catch (IOException e) {
            logger.warning("Couldn't load sprite profile.\n" + e.getMessage()); // TODO Load defaults
        }*/
        
        return new SpriteProfile();
    }
}
