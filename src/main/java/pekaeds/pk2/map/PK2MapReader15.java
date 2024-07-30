package pekaeds.pk2.map;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

import org.json.JSONObject;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.util.file.PK2FileUtils;

public class PK2MapReader15 {
    public static void main(String args[]){
        System.out.println("Hello world!");

        try{
            PK2FileSystem.INSTANCE.setAssetsPath("/Users/saturninufolud/c++/pk2_greta");
            PK2FileSystem.INSTANCE.SetEpisodeName("Debug Island");

            File level = Paths.get(PK2FileSystem.INSTANCE.getAssetsPath(PK2FileSystem.EPISODES_DIR).getPath(),
            "Debug Island", "debug.map").toFile();

            Open15Map(level);


            //File level PK2FileSystem.INSTANCE.findAsset(null, null)
        }
        catch(Exception e){
            e.printStackTrace();
        }        
    }
    public static final List<Integer> ID = List.of((int)'1',(int)'.',(int)'5',(int)'\0');

    public static void Open15Map(File file) throws Exception{
        DataInputStream input = new DataInputStream(new FileInputStream(file));

        byte [] version = new byte[5];
        input.read(version);

        for (int i = 0; i < ID.size(); i++) {
            int by = version[i] & 0xFF; // Java doesn't have unsigned types and that makes me sad :(

            

            if (by != ID.get(i)){
                input.close();
                throw new IOException("Incorrect 1.5 map");
            }
        }

        JSONObject header =  PK2FileUtils.readCBOR(input);

        System.out.println(header.getString("name"));
        System.out.println(header.getInt("level_number"));

        //long busize = in.readLong();

        input.close();
        
    }
}
